package com.shoes.web.rest;

import static com.shoes.domain.ShoeVariantSizesAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.ShoeVariantSizes;
import com.shoes.domain.enumeration.Status;
import com.shoes.repository.ShoeVariantSizesRepository;
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
 * Integration tests for the {@link ShoeVariantSizesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShoeVariantSizesResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Status DEFAULT_STATUS = Status.BEST_SELLER;
    private static final Status UPDATED_STATUS = Status.SALE;

    private static final String ENTITY_API_URL = "/api/shoe-variant-sizes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoeVariantSizesRepository shoeVariantSizesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoeVariantSizesMockMvc;

    private ShoeVariantSizes shoeVariantSizes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariantSizes createEntity(EntityManager em) {
        ShoeVariantSizes shoeVariantSizes = new ShoeVariantSizes().quantity(DEFAULT_QUANTITY).status(DEFAULT_STATUS);
        return shoeVariantSizes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariantSizes createUpdatedEntity(EntityManager em) {
        ShoeVariantSizes shoeVariantSizes = new ShoeVariantSizes().quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);
        return shoeVariantSizes;
    }

    @BeforeEach
    public void initTest() {
        shoeVariantSizes = createEntity(em);
    }

    @Test
    @Transactional
    void createShoeVariantSizes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoeVariantSizes
        var returnedShoeVariantSizes = om.readValue(
            restShoeVariantSizesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantSizes)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShoeVariantSizes.class
        );

        // Validate the ShoeVariantSizes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertShoeVariantSizesUpdatableFieldsEquals(returnedShoeVariantSizes, getPersistedShoeVariantSizes(returnedShoeVariantSizes));
    }

    @Test
    @Transactional
    void createShoeVariantSizesWithExistingId() throws Exception {
        // Create the ShoeVariantSizes with an existing ID
        shoeVariantSizes.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoeVariantSizesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantSizes)))
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShoeVariantSizes() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        // Get all the shoeVariantSizesList
        restShoeVariantSizesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoeVariantSizes.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getShoeVariantSizes() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        // Get the shoeVariantSizes
        restShoeVariantSizesMockMvc
            .perform(get(ENTITY_API_URL_ID, shoeVariantSizes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoeVariantSizes.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingShoeVariantSizes() throws Exception {
        // Get the shoeVariantSizes
        restShoeVariantSizesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoeVariantSizes() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantSizes
        ShoeVariantSizes updatedShoeVariantSizes = shoeVariantSizesRepository.findById(shoeVariantSizes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShoeVariantSizes are not directly saved in db
        em.detach(updatedShoeVariantSizes);
        updatedShoeVariantSizes.quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);

        restShoeVariantSizesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoeVariantSizes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedShoeVariantSizes))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoeVariantSizesToMatchAllProperties(updatedShoeVariantSizes);
    }

    @Test
    @Transactional
    void putNonExistingShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoeVariantSizes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariantSizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariantSizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantSizes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoeVariantSizesWithPatch() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantSizes using partial update
        ShoeVariantSizes partialUpdatedShoeVariantSizes = new ShoeVariantSizes();
        partialUpdatedShoeVariantSizes.setId(shoeVariantSizes.getId());

        restShoeVariantSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariantSizes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariantSizes))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantSizes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantSizesUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoeVariantSizes, shoeVariantSizes),
            getPersistedShoeVariantSizes(shoeVariantSizes)
        );
    }

    @Test
    @Transactional
    void fullUpdateShoeVariantSizesWithPatch() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantSizes using partial update
        ShoeVariantSizes partialUpdatedShoeVariantSizes = new ShoeVariantSizes();
        partialUpdatedShoeVariantSizes.setId(shoeVariantSizes.getId());

        partialUpdatedShoeVariantSizes.quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);

        restShoeVariantSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariantSizes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariantSizes))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantSizes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantSizesUpdatableFieldsEquals(
            partialUpdatedShoeVariantSizes,
            getPersistedShoeVariantSizes(partialUpdatedShoeVariantSizes)
        );
    }

    @Test
    @Transactional
    void patchNonExistingShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoeVariantSizes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariantSizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariantSizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoeVariantSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantSizes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantSizesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoeVariantSizes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariantSizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoeVariantSizes() throws Exception {
        // Initialize the database
        shoeVariantSizesRepository.saveAndFlush(shoeVariantSizes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoeVariantSizes
        restShoeVariantSizesMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoeVariantSizes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoeVariantSizesRepository.count();
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

    protected ShoeVariantSizes getPersistedShoeVariantSizes(ShoeVariantSizes shoeVariantSizes) {
        return shoeVariantSizesRepository.findById(shoeVariantSizes.getId()).orElseThrow();
    }

    protected void assertPersistedShoeVariantSizesToMatchAllProperties(ShoeVariantSizes expectedShoeVariantSizes) {
        assertShoeVariantSizesAllPropertiesEquals(expectedShoeVariantSizes, getPersistedShoeVariantSizes(expectedShoeVariantSizes));
    }

    protected void assertPersistedShoeVariantSizesToMatchUpdatableProperties(ShoeVariantSizes expectedShoeVariantSizes) {
        assertShoeVariantSizesAllUpdatablePropertiesEquals(
            expectedShoeVariantSizes,
            getPersistedShoeVariantSizes(expectedShoeVariantSizes)
        );
    }
}
