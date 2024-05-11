package com.shoes.web.rest;

import static com.shoes.domain.OrderItemsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.OrderItems;
import com.shoes.repository.OrderItemsRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link OrderItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderItemsResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemsMockMvc;

    private OrderItems orderItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItems createEntity(EntityManager em) {
        OrderItems orderItems = new OrderItems().quantity(DEFAULT_QUANTITY).price(DEFAULT_PRICE);
        return orderItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItems createUpdatedEntity(EntityManager em) {
        OrderItems orderItems = new OrderItems().quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
        return orderItems;
    }

    @BeforeEach
    public void initTest() {
        orderItems = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderItems() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderItems
        var returnedOrderItems = om.readValue(
            restOrderItemsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItems)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderItems.class
        );

        // Validate the OrderItems in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrderItemsUpdatableFieldsEquals(returnedOrderItems, getPersistedOrderItems(returnedOrderItems));
    }

    @Test
    @Transactional
    void createOrderItemsWithExistingId() throws Exception {
        // Create the OrderItems with an existing ID
        orderItems.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItems)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        // Get all the orderItemsList
        restOrderItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItems.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }

    @Test
    @Transactional
    void getOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        // Get the orderItems
        restOrderItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItems.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItems.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingOrderItems() throws Exception {
        // Get the orderItems
        restOrderItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItems
        OrderItems updatedOrderItems = orderItemsRepository.findById(orderItems.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderItems are not directly saved in db
        em.detach(updatedOrderItems);
        updatedOrderItems.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderItems.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedOrderItems))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemsToMatchAllProperties(updatedOrderItems);
    }

    @Test
    @Transactional
    void putNonExistingOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItems.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemsWithPatch() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItems using partial update
        OrderItems partialUpdatedOrderItems = new OrderItems();
        partialUpdatedOrderItems.setId(orderItems.getId());

        partialUpdatedOrderItems.quantity(UPDATED_QUANTITY);

        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItems))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItems, orderItems),
            getPersistedOrderItems(orderItems)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderItemsWithPatch() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItems using partial update
        OrderItems partialUpdatedOrderItems = new OrderItems();
        partialUpdatedOrderItems.setId(orderItems.getId());

        partialUpdatedOrderItems.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItems))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemsUpdatableFieldsEquals(partialUpdatedOrderItems, getPersistedOrderItems(partialUpdatedOrderItems));
    }

    @Test
    @Transactional
    void patchNonExistingOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItems
        restOrderItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItems.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemsRepository.count();
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

    protected OrderItems getPersistedOrderItems(OrderItems orderItems) {
        return orderItemsRepository.findById(orderItems.getId()).orElseThrow();
    }

    protected void assertPersistedOrderItemsToMatchAllProperties(OrderItems expectedOrderItems) {
        assertOrderItemsAllPropertiesEquals(expectedOrderItems, getPersistedOrderItems(expectedOrderItems));
    }

    protected void assertPersistedOrderItemsToMatchUpdatableProperties(OrderItems expectedOrderItems) {
        assertOrderItemsAllUpdatablePropertiesEquals(expectedOrderItems, getPersistedOrderItems(expectedOrderItems));
    }
}
