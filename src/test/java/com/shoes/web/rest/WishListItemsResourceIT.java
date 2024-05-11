package com.shoes.web.rest;

import static com.shoes.domain.WishListItemsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.WishListItems;
import com.shoes.repository.WishListItemsRepository;
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
 * Integration tests for the {@link WishListItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WishListItemsResourceIT {

    private static final String ENTITY_API_URL = "/api/wish-list-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WishListItemsRepository wishListItemsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishListItemsMockMvc;

    private WishListItems wishListItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishListItems createEntity(EntityManager em) {
        WishListItems wishListItems = new WishListItems();
        return wishListItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishListItems createUpdatedEntity(EntityManager em) {
        WishListItems wishListItems = new WishListItems();
        return wishListItems;
    }

    @BeforeEach
    public void initTest() {
        wishListItems = createEntity(em);
    }

    @Test
    @Transactional
    void createWishListItems() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WishListItems
        var returnedWishListItems = om.readValue(
            restWishListItemsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishListItems)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WishListItems.class
        );

        // Validate the WishListItems in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWishListItemsUpdatableFieldsEquals(returnedWishListItems, getPersistedWishListItems(returnedWishListItems));
    }

    @Test
    @Transactional
    void createWishListItemsWithExistingId() throws Exception {
        // Create the WishListItems with an existing ID
        wishListItems.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishListItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishListItems)))
            .andExpect(status().isBadRequest());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWishListItems() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        // Get all the wishListItemsList
        restWishListItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishListItems.getId().intValue())));
    }

    @Test
    @Transactional
    void getWishListItems() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        // Get the wishListItems
        restWishListItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, wishListItems.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishListItems.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingWishListItems() throws Exception {
        // Get the wishListItems
        restWishListItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWishListItems() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishListItems
        WishListItems updatedWishListItems = wishListItemsRepository.findById(wishListItems.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWishListItems are not directly saved in db
        em.detach(updatedWishListItems);

        restWishListItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWishListItems.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWishListItems))
            )
            .andExpect(status().isOk());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWishListItemsToMatchAllProperties(updatedWishListItems);
    }

    @Test
    @Transactional
    void putNonExistingWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishListItems.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishListItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishListItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishListItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWishListItemsWithPatch() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishListItems using partial update
        WishListItems partialUpdatedWishListItems = new WishListItems();
        partialUpdatedWishListItems.setId(wishListItems.getId());

        restWishListItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishListItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWishListItems))
            )
            .andExpect(status().isOk());

        // Validate the WishListItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWishListItemsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWishListItems, wishListItems),
            getPersistedWishListItems(wishListItems)
        );
    }

    @Test
    @Transactional
    void fullUpdateWishListItemsWithPatch() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishListItems using partial update
        WishListItems partialUpdatedWishListItems = new WishListItems();
        partialUpdatedWishListItems.setId(wishListItems.getId());

        restWishListItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishListItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWishListItems))
            )
            .andExpect(status().isOk());

        // Validate the WishListItems in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWishListItemsUpdatableFieldsEquals(partialUpdatedWishListItems, getPersistedWishListItems(partialUpdatedWishListItems));
    }

    @Test
    @Transactional
    void patchNonExistingWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishListItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishListItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishListItems))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishListItems() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishListItems.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListItemsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(wishListItems)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishListItems in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWishListItems() throws Exception {
        // Initialize the database
        wishListItemsRepository.saveAndFlush(wishListItems);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the wishListItems
        restWishListItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishListItems.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wishListItemsRepository.count();
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

    protected WishListItems getPersistedWishListItems(WishListItems wishListItems) {
        return wishListItemsRepository.findById(wishListItems.getId()).orElseThrow();
    }

    protected void assertPersistedWishListItemsToMatchAllProperties(WishListItems expectedWishListItems) {
        assertWishListItemsAllPropertiesEquals(expectedWishListItems, getPersistedWishListItems(expectedWishListItems));
    }

    protected void assertPersistedWishListItemsToMatchUpdatableProperties(WishListItems expectedWishListItems) {
        assertWishListItemsAllUpdatablePropertiesEquals(expectedWishListItems, getPersistedWishListItems(expectedWishListItems));
    }
}
