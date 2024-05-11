package com.shoes.web.rest;

import static com.shoes.domain.ShoeVariantColorsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.ShoeVariantColors;
import com.shoes.repository.ShoeVariantColorsRepository;
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
 * Integration tests for the {@link ShoeVariantColorsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShoeVariantColorsResourceIT {

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shoe-variant-colors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoeVariantColorsRepository shoeVariantColorsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoeVariantColorsMockMvc;

    private ShoeVariantColors shoeVariantColors;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariantColors createEntity(EntityManager em) {
        ShoeVariantColors shoeVariantColors = new ShoeVariantColors().price(DEFAULT_PRICE).imageUrl(DEFAULT_IMAGE_URL);
        return shoeVariantColors;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariantColors createUpdatedEntity(EntityManager em) {
        ShoeVariantColors shoeVariantColors = new ShoeVariantColors().price(UPDATED_PRICE).imageUrl(UPDATED_IMAGE_URL);
        return shoeVariantColors;
    }

    @BeforeEach
    public void initTest() {
        shoeVariantColors = createEntity(em);
    }

    @Test
    @Transactional
    void createShoeVariantColors() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoeVariantColors
        var returnedShoeVariantColors = om.readValue(
            restShoeVariantColorsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantColors)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShoeVariantColors.class
        );

        // Validate the ShoeVariantColors in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertShoeVariantColorsUpdatableFieldsEquals(returnedShoeVariantColors, getPersistedShoeVariantColors(returnedShoeVariantColors));
    }

    @Test
    @Transactional
    void createShoeVariantColorsWithExistingId() throws Exception {
        // Create the ShoeVariantColors with an existing ID
        shoeVariantColors.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoeVariantColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantColors)))
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShoeVariantColors() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        // Get all the shoeVariantColorsList
        restShoeVariantColorsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoeVariantColors.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @Test
    @Transactional
    void getShoeVariantColors() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        // Get the shoeVariantColors
        restShoeVariantColorsMockMvc
            .perform(get(ENTITY_API_URL_ID, shoeVariantColors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoeVariantColors.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingShoeVariantColors() throws Exception {
        // Get the shoeVariantColors
        restShoeVariantColorsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoeVariantColors() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantColors
        ShoeVariantColors updatedShoeVariantColors = shoeVariantColorsRepository.findById(shoeVariantColors.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShoeVariantColors are not directly saved in db
        em.detach(updatedShoeVariantColors);
        updatedShoeVariantColors.price(UPDATED_PRICE).imageUrl(UPDATED_IMAGE_URL);

        restShoeVariantColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoeVariantColors.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedShoeVariantColors))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoeVariantColorsToMatchAllProperties(updatedShoeVariantColors);
    }

    @Test
    @Transactional
    void putNonExistingShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoeVariantColors.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariantColors))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariantColors))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariantColors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoeVariantColorsWithPatch() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantColors using partial update
        ShoeVariantColors partialUpdatedShoeVariantColors = new ShoeVariantColors();
        partialUpdatedShoeVariantColors.setId(shoeVariantColors.getId());

        restShoeVariantColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariantColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariantColors))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantColors in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantColorsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoeVariantColors, shoeVariantColors),
            getPersistedShoeVariantColors(shoeVariantColors)
        );
    }

    @Test
    @Transactional
    void fullUpdateShoeVariantColorsWithPatch() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariantColors using partial update
        ShoeVariantColors partialUpdatedShoeVariantColors = new ShoeVariantColors();
        partialUpdatedShoeVariantColors.setId(shoeVariantColors.getId());

        partialUpdatedShoeVariantColors.price(UPDATED_PRICE).imageUrl(UPDATED_IMAGE_URL);

        restShoeVariantColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariantColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariantColors))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariantColors in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantColorsUpdatableFieldsEquals(
            partialUpdatedShoeVariantColors,
            getPersistedShoeVariantColors(partialUpdatedShoeVariantColors)
        );
    }

    @Test
    @Transactional
    void patchNonExistingShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoeVariantColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariantColors))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariantColors))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoeVariantColors() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariantColors.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantColorsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoeVariantColors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariantColors in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoeVariantColors() throws Exception {
        // Initialize the database
        shoeVariantColorsRepository.saveAndFlush(shoeVariantColors);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoeVariantColors
        restShoeVariantColorsMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoeVariantColors.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoeVariantColorsRepository.count();
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

    protected ShoeVariantColors getPersistedShoeVariantColors(ShoeVariantColors shoeVariantColors) {
        return shoeVariantColorsRepository.findById(shoeVariantColors.getId()).orElseThrow();
    }

    protected void assertPersistedShoeVariantColorsToMatchAllProperties(ShoeVariantColors expectedShoeVariantColors) {
        assertShoeVariantColorsAllPropertiesEquals(expectedShoeVariantColors, getPersistedShoeVariantColors(expectedShoeVariantColors));
    }

    protected void assertPersistedShoeVariantColorsToMatchUpdatableProperties(ShoeVariantColors expectedShoeVariantColors) {
        assertShoeVariantColorsAllUpdatablePropertiesEquals(
            expectedShoeVariantColors,
            getPersistedShoeVariantColors(expectedShoeVariantColors)
        );
    }
}
