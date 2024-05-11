package com.shoes.web.rest;

import static com.shoes.domain.CartItemsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.CartItems;
import com.shoes.repository.CartItemsRepository;
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
 * Integration tests for the {@link CartItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemsResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemsMockMvc;

    private CartItems cartItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createEntity(EntityManager em) {
        CartItems cartItems = new CartItems().quantity(DEFAULT_QUANTITY);
        return cartItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createUpdatedEntity(EntityManager em) {
        CartItems cartItems = new CartItems().quantity(UPDATED_QUANTITY);
        return cartItems;
    }

    @BeforeEach
    public void initTest() {
        cartItems = createEntity(em);
    }

    @Test
    @Transactional
    void createCartItems() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CartItems
        var returnedCartItems = om.readValue(
            restCartItemsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItems)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CartItems.class
        );

        // Validate the CartItems in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCartItemsUpdatableFieldsEquals(returnedCartItems, getPersistedCartItems(returnedCartItems));
    }

    @Test
    @Transactional
    void createCartItemsWithExistingId() throws Exception {
        // Create the CartItems with an existing ID
        cartItems.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItems)))
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get all the cartItemsList
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItems.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get the cartItems
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItems.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItems.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingCartItems() throws Exception {
        // Get the cartItems
        restCartItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItems
        CartItems updatedCartItems = cartItemsRepository.findById(cartItems.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCartItems are not directly saved in db
        em.detach(updatedCartItems);
        updatedCartItems.quantity(UPDATED_QUANTITY);

        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCartItems.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCartItemsToMatchAllProperties(updatedCartItems);
    }

    @Test
    @Transactional
    void putNonExistingCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItems.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setId(cartItems.getId());

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCartItems, cartItems),
            getPersistedCartItems(cartItems)
        );
    }

    @Test
    @Transactional
    void fullUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setId(cartItems.getId());

        partialUpdatedCartItems.quantity(UPDATED_QUANTITY);

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemsUpdatableFieldsEquals(partialUpdatedCartItems, getPersistedCartItems(partialUpdatedCartItems));
    }

    @Test
    @Transactional
    void patchNonExistingCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cartItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cartItems
        restCartItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItems.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cartItemsRepository.count();
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

    protected CartItems getPersistedCartItems(CartItems cartItems) {
        return cartItemsRepository.findById(cartItems.getId()).orElseThrow();
    }

    protected void assertPersistedCartItemsToMatchAllProperties(CartItems expectedCartItems) {
        assertCartItemsAllPropertiesEquals(expectedCartItems, getPersistedCartItems(expectedCartItems));
    }

    protected void assertPersistedCartItemsToMatchUpdatableProperties(CartItems expectedCartItems) {
        assertCartItemsAllUpdatablePropertiesEquals(expectedCartItems, getPersistedCartItems(expectedCartItems));
    }
}
