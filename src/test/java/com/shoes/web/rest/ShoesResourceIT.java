package com.shoes.web.rest;

import static com.shoes.domain.ShoesAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.Shoes;
import com.shoes.repository.ShoesRepository;
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
 * Integration tests for the {@link ShoesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShoesResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoesRepository shoesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoesMockMvc;

    private Shoes shoes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shoes createEntity(EntityManager em) {
        Shoes shoes = new Shoes().description(DEFAULT_DESCRIPTION);
        return shoes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shoes createUpdatedEntity(EntityManager em) {
        Shoes shoes = new Shoes().description(UPDATED_DESCRIPTION);
        return shoes;
    }

    @BeforeEach
    public void initTest() {
        shoes = createEntity(em);
    }

    @Test
    @Transactional
    void createShoes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Shoes
        var returnedShoes = om.readValue(
            restShoesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoes)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Shoes.class
        );

        // Validate the Shoes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertShoesUpdatableFieldsEquals(returnedShoes, getPersistedShoes(returnedShoes));
    }

    @Test
    @Transactional
    void createShoesWithExistingId() throws Exception {
        // Create the Shoes with an existing ID
        shoes.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoes)))
            .andExpect(status().isBadRequest());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShoes() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        // Get all the shoesList
        restShoesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoes.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getShoes() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        // Get the shoes
        restShoesMockMvc
            .perform(get(ENTITY_API_URL_ID, shoes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoes.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingShoes() throws Exception {
        // Get the shoes
        restShoesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoes() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoes
        Shoes updatedShoes = shoesRepository.findById(shoes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShoes are not directly saved in db
        em.detach(updatedShoes);
        updatedShoes.description(UPDATED_DESCRIPTION);

        restShoesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedShoes))
            )
            .andExpect(status().isOk());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoesToMatchAllProperties(updatedShoes);
    }

    @Test
    @Transactional
    void putNonExistingShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(put(ENTITY_API_URL_ID, shoes.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoes)))
            .andExpect(status().isBadRequest());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoesWithPatch() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoes using partial update
        Shoes partialUpdatedShoes = new Shoes();
        partialUpdatedShoes.setId(shoes.getId());

        partialUpdatedShoes.description(UPDATED_DESCRIPTION);

        restShoesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoes))
            )
            .andExpect(status().isOk());

        // Validate the Shoes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShoes, shoes), getPersistedShoes(shoes));
    }

    @Test
    @Transactional
    void fullUpdateShoesWithPatch() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoes using partial update
        Shoes partialUpdatedShoes = new Shoes();
        partialUpdatedShoes.setId(shoes.getId());

        partialUpdatedShoes.description(UPDATED_DESCRIPTION);

        restShoesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoes))
            )
            .andExpect(status().isOk());

        // Validate the Shoes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoesUpdatableFieldsEquals(partialUpdatedShoes, getPersistedShoes(partialUpdatedShoes));
    }

    @Test
    @Transactional
    void patchNonExistingShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoes.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shoes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoes() throws Exception {
        // Initialize the database
        shoesRepository.saveAndFlush(shoes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoes
        restShoesMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoesRepository.count();
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

    protected Shoes getPersistedShoes(Shoes shoes) {
        return shoesRepository.findById(shoes.getId()).orElseThrow();
    }

    protected void assertPersistedShoesToMatchAllProperties(Shoes expectedShoes) {
        assertShoesAllPropertiesEquals(expectedShoes, getPersistedShoes(expectedShoes));
    }

    protected void assertPersistedShoesToMatchUpdatableProperties(Shoes expectedShoes) {
        assertShoesAllUpdatablePropertiesEquals(expectedShoes, getPersistedShoes(expectedShoes));
    }
}
