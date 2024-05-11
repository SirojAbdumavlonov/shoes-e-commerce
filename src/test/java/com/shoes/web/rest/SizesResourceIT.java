package com.shoes.web.rest;

import static com.shoes.domain.SizesAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.Sizes;
import com.shoes.repository.SizesRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SizesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SizesResourceIT {

    private static final Float DEFAULT_SIZE_IN_NUMBERS = 1F;
    private static final Float UPDATED_SIZE_IN_NUMBERS = 2F;

    private static final String DEFAULT_SIZE_IN_LETTERS = "AAAAAAAAAA";
    private static final String UPDATED_SIZE_IN_LETTERS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sizes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SizesRepository sizesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSizesMockMvc;

    private Sizes sizes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sizes createEntity(EntityManager em) {
        Sizes sizes = new Sizes().sizeInNumbers(DEFAULT_SIZE_IN_NUMBERS).sizeInLetters(DEFAULT_SIZE_IN_LETTERS);
        return sizes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sizes createUpdatedEntity(EntityManager em) {
        Sizes sizes = new Sizes().sizeInNumbers(UPDATED_SIZE_IN_NUMBERS).sizeInLetters(UPDATED_SIZE_IN_LETTERS);
        return sizes;
    }

    @BeforeEach
    public void initTest() {
        sizes = createEntity(em);
    }

    @Test
    @Transactional
    void createSizes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sizes
        var returnedSizes = om.readValue(
            restSizesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sizes)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Sizes.class
        );

        // Validate the Sizes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSizesUpdatableFieldsEquals(returnedSizes, getPersistedSizes(returnedSizes));
    }

    @Test
    @Transactional
    void createSizesWithExistingId() throws Exception {
        // Create the Sizes with an existing ID
        sizes.setId(1);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSizesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sizes)))
            .andExpect(status().isBadRequest());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSizes() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        // Get all the sizesList
        restSizesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sizes.getId().intValue())))
            .andExpect(jsonPath("$.[*].sizeInNumbers").value(hasItem(DEFAULT_SIZE_IN_NUMBERS.doubleValue())))
            .andExpect(jsonPath("$.[*].sizeInLetters").value(hasItem(DEFAULT_SIZE_IN_LETTERS)));
    }

    @Test
    @Transactional
    void getSizes() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        // Get the sizes
        restSizesMockMvc
            .perform(get(ENTITY_API_URL_ID, sizes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sizes.getId().intValue()))
            .andExpect(jsonPath("$.sizeInNumbers").value(DEFAULT_SIZE_IN_NUMBERS.doubleValue()))
            .andExpect(jsonPath("$.sizeInLetters").value(DEFAULT_SIZE_IN_LETTERS));
    }

    @Test
    @Transactional
    void getNonExistingSizes() throws Exception {
        // Get the sizes
        restSizesMockMvc.perform(get(ENTITY_API_URL_ID, Integer.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSizes() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sizes
        Sizes updatedSizes = sizesRepository.findById(sizes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSizes are not directly saved in db
        em.detach(updatedSizes);
        updatedSizes.sizeInNumbers(UPDATED_SIZE_IN_NUMBERS).sizeInLetters(UPDATED_SIZE_IN_LETTERS);

        restSizesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSizes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSizes))
            )
            .andExpect(status().isOk());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSizesToMatchAllProperties(updatedSizes);
    }

    @Test
    @Transactional
    void putNonExistingSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(put(ENTITY_API_URL_ID, sizes.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sizes)))
            .andExpect(status().isBadRequest());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sizes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSizesWithPatch() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sizes using partial update
        Sizes partialUpdatedSizes = new Sizes();
        partialUpdatedSizes.setId(sizes.getId());

        partialUpdatedSizes.sizeInNumbers(UPDATED_SIZE_IN_NUMBERS);

        restSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSizes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSizes))
            )
            .andExpect(status().isOk());

        // Validate the Sizes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSizesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSizes, sizes), getPersistedSizes(sizes));
    }

    @Test
    @Transactional
    void fullUpdateSizesWithPatch() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sizes using partial update
        Sizes partialUpdatedSizes = new Sizes();
        partialUpdatedSizes.setId(sizes.getId());

        partialUpdatedSizes.sizeInNumbers(UPDATED_SIZE_IN_NUMBERS).sizeInLetters(UPDATED_SIZE_IN_LETTERS);

        restSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSizes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSizes))
            )
            .andExpect(status().isOk());

        // Validate the Sizes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSizesUpdatableFieldsEquals(partialUpdatedSizes, getPersistedSizes(partialUpdatedSizes));
    }

    @Test
    @Transactional
    void patchNonExistingSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sizes.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sizes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSizes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sizes.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sizes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sizes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSizes() throws Exception {
        // Initialize the database
        sizesRepository.saveAndFlush(sizes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sizes
        restSizesMockMvc
            .perform(delete(ENTITY_API_URL_ID, sizes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sizesRepository.count();
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

    protected Sizes getPersistedSizes(Sizes sizes) {
        return sizesRepository.findById(sizes.getId()).orElseThrow();
    }

    protected void assertPersistedSizesToMatchAllProperties(Sizes expectedSizes) {
        assertSizesAllPropertiesEquals(expectedSizes, getPersistedSizes(expectedSizes));
    }

    protected void assertPersistedSizesToMatchUpdatableProperties(Sizes expectedSizes) {
        assertSizesAllUpdatablePropertiesEquals(expectedSizes, getPersistedSizes(expectedSizes));
    }
}
