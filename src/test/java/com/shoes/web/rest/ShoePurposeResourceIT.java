package com.shoes.web.rest;

import static com.shoes.domain.ShoePurposeAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.ShoePurpose;
import com.shoes.domain.enumeration.ShoePurposeType;
import com.shoes.repository.ShoePurposeRepository;
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
 * Integration tests for the {@link ShoePurposeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShoePurposeResourceIT {

    private static final ShoePurposeType DEFAULT_TYPE = ShoePurposeType.SPORT;
    private static final ShoePurposeType UPDATED_TYPE = ShoePurposeType.LIFESTYLE;

    private static final String ENTITY_API_URL = "/api/shoe-purposes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoePurposeRepository shoePurposeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoePurposeMockMvc;

    private ShoePurpose shoePurpose;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoePurpose createEntity(EntityManager em) {
        ShoePurpose shoePurpose = new ShoePurpose().type(DEFAULT_TYPE);
        return shoePurpose;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoePurpose createUpdatedEntity(EntityManager em) {
        ShoePurpose shoePurpose = new ShoePurpose().type(UPDATED_TYPE);
        return shoePurpose;
    }

    @BeforeEach
    public void initTest() {
        shoePurpose = createEntity(em);
    }

    @Test
    @Transactional
    void createShoePurpose() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoePurpose
        var returnedShoePurpose = om.readValue(
            restShoePurposeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoePurpose)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShoePurpose.class
        );

        // Validate the ShoePurpose in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertShoePurposeUpdatableFieldsEquals(returnedShoePurpose, getPersistedShoePurpose(returnedShoePurpose));
    }

    @Test
    @Transactional
    void createShoePurposeWithExistingId() throws Exception {
        // Create the ShoePurpose with an existing ID
        shoePurpose.setId(1);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoePurposeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoePurpose)))
            .andExpect(status().isBadRequest());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShoePurposes() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        // Get all the shoePurposeList
        restShoePurposeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoePurpose.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getShoePurpose() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        // Get the shoePurpose
        restShoePurposeMockMvc
            .perform(get(ENTITY_API_URL_ID, shoePurpose.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoePurpose.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingShoePurpose() throws Exception {
        // Get the shoePurpose
        restShoePurposeMockMvc.perform(get(ENTITY_API_URL_ID, Integer.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoePurpose() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoePurpose
        ShoePurpose updatedShoePurpose = shoePurposeRepository.findById(shoePurpose.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShoePurpose are not directly saved in db
        em.detach(updatedShoePurpose);
        updatedShoePurpose.type(UPDATED_TYPE);

        restShoePurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoePurpose.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedShoePurpose))
            )
            .andExpect(status().isOk());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoePurposeToMatchAllProperties(updatedShoePurpose);
    }

    @Test
    @Transactional
    void putNonExistingShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoePurpose.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoePurpose))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoePurpose))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoePurpose)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoePurposeWithPatch() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoePurpose using partial update
        ShoePurpose partialUpdatedShoePurpose = new ShoePurpose();
        partialUpdatedShoePurpose.setId(shoePurpose.getId());

        partialUpdatedShoePurpose.type(UPDATED_TYPE);

        restShoePurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoePurpose.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoePurpose))
            )
            .andExpect(status().isOk());

        // Validate the ShoePurpose in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoePurposeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoePurpose, shoePurpose),
            getPersistedShoePurpose(shoePurpose)
        );
    }

    @Test
    @Transactional
    void fullUpdateShoePurposeWithPatch() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoePurpose using partial update
        ShoePurpose partialUpdatedShoePurpose = new ShoePurpose();
        partialUpdatedShoePurpose.setId(shoePurpose.getId());

        partialUpdatedShoePurpose.type(UPDATED_TYPE);

        restShoePurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoePurpose.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoePurpose))
            )
            .andExpect(status().isOk());

        // Validate the ShoePurpose in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoePurposeUpdatableFieldsEquals(partialUpdatedShoePurpose, getPersistedShoePurpose(partialUpdatedShoePurpose));
    }

    @Test
    @Transactional
    void patchNonExistingShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoePurpose.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoePurpose))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoePurpose))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoePurpose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoePurpose.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoePurposeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoePurpose)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoePurpose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoePurpose() throws Exception {
        // Initialize the database
        shoePurposeRepository.saveAndFlush(shoePurpose);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoePurpose
        restShoePurposeMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoePurpose.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoePurposeRepository.count();
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

    protected ShoePurpose getPersistedShoePurpose(ShoePurpose shoePurpose) {
        return shoePurposeRepository.findById(shoePurpose.getId()).orElseThrow();
    }

    protected void assertPersistedShoePurposeToMatchAllProperties(ShoePurpose expectedShoePurpose) {
        assertShoePurposeAllPropertiesEquals(expectedShoePurpose, getPersistedShoePurpose(expectedShoePurpose));
    }

    protected void assertPersistedShoePurposeToMatchUpdatableProperties(ShoePurpose expectedShoePurpose) {
        assertShoePurposeAllUpdatablePropertiesEquals(expectedShoePurpose, getPersistedShoePurpose(expectedShoePurpose));
    }
}
