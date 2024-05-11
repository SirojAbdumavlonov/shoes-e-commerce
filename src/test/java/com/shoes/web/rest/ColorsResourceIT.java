package com.shoes.web.rest;

import static com.shoes.domain.ColorsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.Colors;
import com.shoes.repository.ColorsRepository;
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
 * Integration tests for the {@link ColorsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ColorsResourceIT {

    private static final String DEFAULT_COLOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COLOR_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/colors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ColorsRepository colorsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restColorsMockMvc;

    private Colors colors;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colors createEntity(EntityManager em) {
        Colors colors = new Colors().colorName(DEFAULT_COLOR_NAME);
        return colors;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colors createUpdatedEntity(EntityManager em) {
        Colors colors = new Colors().colorName(UPDATED_COLOR_NAME);
        return colors;
    }

    @BeforeEach
    public void initTest() {
        colors = createEntity(em);
    }

    @Test
    @Transactional
    void createColors() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Colors
        var returnedColors = om.readValue(
            restColorsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colors)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Colors.class
        );

        // Validate the Colors in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertColorsUpdatableFieldsEquals(returnedColors, getPersistedColors(returnedColors));
    }

    @Test
    @Transactional
    void createColorsWithExistingId() throws Exception {
        // Create the Colors with an existing ID
        colors.setId(1);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colors)))
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colors.getId().intValue())))
            .andExpect(jsonPath("$.[*].colorName").value(hasItem(DEFAULT_COLOR_NAME)));
    }

    @Test
    @Transactional
    void getColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get the colors
        restColorsMockMvc
            .perform(get(ENTITY_API_URL_ID, colors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(colors.getId().intValue()))
            .andExpect(jsonPath("$.colorName").value(DEFAULT_COLOR_NAME));
    }

    @Test
    @Transactional
    void getNonExistingColors() throws Exception {
        // Get the colors
        restColorsMockMvc.perform(get(ENTITY_API_URL_ID, Integer.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colors
        Colors updatedColors = colorsRepository.findById(colors.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedColors are not directly saved in db
        em.detach(updatedColors);
        updatedColors.colorName(UPDATED_COLOR_NAME);

        restColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedColors.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedColorsToMatchAllProperties(updatedColors);
    }

    @Test
    @Transactional
    void putNonExistingColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(put(ENTITY_API_URL_ID, colors.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colors)))
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateColorsWithPatch() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colors using partial update
        Colors partialUpdatedColors = new Colors();
        partialUpdatedColors.setId(colors.getId());

        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColorsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedColors, colors), getPersistedColors(colors));
    }

    @Test
    @Transactional
    void fullUpdateColorsWithPatch() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colors using partial update
        Colors partialUpdatedColors = new Colors();
        partialUpdatedColors.setId(colors.getId());

        partialUpdatedColors.colorName(UPDATED_COLOR_NAME);

        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColorsUpdatableFieldsEquals(partialUpdatedColors, getPersistedColors(partialUpdatedColors));
    }

    @Test
    @Transactional
    void patchNonExistingColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, colors.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colors.setId(intCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(colors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the colors
        restColorsMockMvc
            .perform(delete(ENTITY_API_URL_ID, colors.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return colorsRepository.count();
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

    protected Colors getPersistedColors(Colors colors) {
        return colorsRepository.findById(colors.getId()).orElseThrow();
    }

    protected void assertPersistedColorsToMatchAllProperties(Colors expectedColors) {
        assertColorsAllPropertiesEquals(expectedColors, getPersistedColors(expectedColors));
    }

    protected void assertPersistedColorsToMatchUpdatableProperties(Colors expectedColors) {
        assertColorsAllUpdatablePropertiesEquals(expectedColors, getPersistedColors(expectedColors));
    }
}
