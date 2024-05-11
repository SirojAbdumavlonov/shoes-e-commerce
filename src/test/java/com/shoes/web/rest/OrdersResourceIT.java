package com.shoes.web.rest;

import static com.shoes.domain.OrdersAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.Orders;
import com.shoes.domain.enumeration.OrderStatus;
import com.shoes.repository.OrdersRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrdersResourceIT {

    private static final Long DEFAULT_TOTAL_PRICE = 1L;
    private static final Long UPDATED_TOTAL_PRICE = 2L;

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DELIVERED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.COLLECTED;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdersMockMvc;

    private Orders orders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity(EntityManager em) {
        Orders orders = new Orders().totalPrice(DEFAULT_TOTAL_PRICE).orderDate(DEFAULT_ORDER_DATE).status(DEFAULT_STATUS);
        return orders;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity(EntityManager em) {
        Orders orders = new Orders().totalPrice(UPDATED_TOTAL_PRICE).orderDate(UPDATED_ORDER_DATE).status(UPDATED_STATUS);
        return orders;
    }

    @BeforeEach
    public void initTest() {
        orders = createEntity(em);
    }

    @Test
    @Transactional
    void createOrders() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Orders
        var returnedOrders = om.readValue(
            restOrdersMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orders)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Orders.class
        );

        // Validate the Orders in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrdersUpdatableFieldsEquals(returnedOrders, getPersistedOrders(returnedOrders));
    }

    @Test
    @Transactional
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId().intValue()))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE.intValue()))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders.totalPrice(UPDATED_TOTAL_PRICE).orderDate(UPDATED_ORDER_DATE).status(UPDATED_STATUS);

        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrdersToMatchAllProperties(updatedOrders);
    }

    @Test
    @Transactional
    void putNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL_ID, orders.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.totalPrice(UPDATED_TOTAL_PRICE).orderDate(UPDATED_ORDER_DATE).status(UPDATED_STATUS);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrders, orders), getPersistedOrders(orders));
    }

    @Test
    @Transactional
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.totalPrice(UPDATED_TOTAL_PRICE).orderDate(UPDATED_ORDER_DATE).status(UPDATED_STATUS);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(partialUpdatedOrders, getPersistedOrders(partialUpdatedOrders));
    }

    @Test
    @Transactional
    void patchNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orders.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orders
        restOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, orders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ordersRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Orders getPersistedOrders(Orders orders) {
        return ordersRepository.findById(orders.getId()).orElseThrow();
    }

    protected void assertPersistedOrdersToMatchAllProperties(Orders expectedOrders) {
        assertOrdersAllPropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }

    protected void assertPersistedOrdersToMatchUpdatableProperties(Orders expectedOrders) {
        assertOrdersAllUpdatablePropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }
}
