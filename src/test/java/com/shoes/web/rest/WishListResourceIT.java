package com.shoes.web.rest;

import static com.shoes.domain.WishListAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.WishList;
import com.shoes.repository.WishListRepository;
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
 * Integration tests for the {@link WishListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WishListResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/wish-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishListMockMvc;

    private WishList wishList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createEntity(EntityManager em) {
        WishList wishList = new WishList().createdAt(DEFAULT_CREATED_AT);
        return wishList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createUpdatedEntity(EntityManager em) {
        WishList wishList = new WishList().createdAt(UPDATED_CREATED_AT);
        return wishList;
    }

    @BeforeEach
    public void initTest() {
        wishList = createEntity(em);
    }

    @Test
    @Transactional
    void createWishList() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WishList
        var returnedWishList = om.readValue(
            restWishListMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishList)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WishList.class
        );

        // Validate the WishList in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWishListUpdatableFieldsEquals(returnedWishList, getPersistedWishList(returnedWishList));
    }

    @Test
    @Transactional
    void createWishListWithExistingId() throws Exception {
        // Create the WishList with an existing ID
        wishList.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishList)))
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWishLists() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get the wishList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL_ID, wishList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishList.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingWishList() throws Exception {
        // Get the wishList
        restWishListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishList
        WishList updatedWishList = wishListRepository.findById(wishList.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWishList are not directly saved in db
        em.detach(updatedWishList);
        updatedWishList.createdAt(UPDATED_CREATED_AT);

        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWishList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWishListToMatchAllProperties(updatedWishList);
    }

    @Test
    @Transactional
    void putNonExistingWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishList.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wishList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWishListUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWishList, wishList), getPersistedWishList(wishList));
    }

    @Test
    @Transactional
    void fullUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.createdAt(UPDATED_CREATED_AT);

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWishListUpdatableFieldsEquals(partialUpdatedWishList, getPersistedWishList(partialUpdatedWishList));
    }

    @Test
    @Transactional
    void patchNonExistingWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(wishList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the wishList
        restWishListMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wishListRepository.count();
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

    protected WishList getPersistedWishList(WishList wishList) {
        return wishListRepository.findById(wishList.getId()).orElseThrow();
    }

    protected void assertPersistedWishListToMatchAllProperties(WishList expectedWishList) {
        assertWishListAllPropertiesEquals(expectedWishList, getPersistedWishList(expectedWishList));
    }

    protected void assertPersistedWishListToMatchUpdatableProperties(WishList expectedWishList) {
        assertWishListAllUpdatablePropertiesEquals(expectedWishList, getPersistedWishList(expectedWishList));
    }
}
