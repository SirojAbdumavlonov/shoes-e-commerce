package com.shoes.web.rest;

import static com.shoes.domain.ShoeVariantsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.ShoeVariants;
import com.shoes.domain.enumeration.Status;
import com.shoes.repository.ShoeVariantsRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShoeVariantsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ShoeVariantsResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Status DEFAULT_STATUS = Status.BEST_SELLER;
    private static final Status UPDATED_STATUS = Status.SALE;

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shoe-variants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoeVariantsRepository shoeVariantsRepository;

    @Mock
    private ShoeVariantsRepository shoeVariantsRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoeVariantsMockMvc;

    private ShoeVariants shoeVariants;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariants createEntity(EntityManager em) {
        ShoeVariants shoeVariants = new ShoeVariants().quantity(DEFAULT_QUANTITY).status(DEFAULT_STATUS).photoUrl(DEFAULT_PHOTO_URL);
        return shoeVariants;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoeVariants createUpdatedEntity(EntityManager em) {
        ShoeVariants shoeVariants = new ShoeVariants().quantity(UPDATED_QUANTITY).status(UPDATED_STATUS).photoUrl(UPDATED_PHOTO_URL);
        return shoeVariants;
    }

    @BeforeEach
    public void initTest() {
        shoeVariants = createEntity(em);
    }

    @Test
    @Transactional
    void createShoeVariants() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoeVariants
        var returnedShoeVariants = om.readValue(
            restShoeVariantsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariants)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShoeVariants.class
        );

        // Validate the ShoeVariants in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertShoeVariantsUpdatableFieldsEquals(returnedShoeVariants, getPersistedShoeVariants(returnedShoeVariants));
    }

    @Test
    @Transactional
    void createShoeVariantsWithExistingId() throws Exception {
        // Create the ShoeVariants with an existing ID
        shoeVariants.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoeVariantsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariants)))
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShoeVariants() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        // Get all the shoeVariantsList
        restShoeVariantsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoeVariants.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoeVariantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(shoeVariantsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShoeVariantsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(shoeVariantsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoeVariantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(shoeVariantsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShoeVariantsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(shoeVariantsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getShoeVariants() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        // Get the shoeVariants
        restShoeVariantsMockMvc
            .perform(get(ENTITY_API_URL_ID, shoeVariants.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoeVariants.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.photoUrl").value(DEFAULT_PHOTO_URL));
    }

    @Test
    @Transactional
    void getNonExistingShoeVariants() throws Exception {
        // Get the shoeVariants
        restShoeVariantsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoeVariants() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariants
        ShoeVariants updatedShoeVariants = shoeVariantsRepository.findById(shoeVariants.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShoeVariants are not directly saved in db
        em.detach(updatedShoeVariants);
        updatedShoeVariants.quantity(UPDATED_QUANTITY).status(UPDATED_STATUS).photoUrl(UPDATED_PHOTO_URL);

        restShoeVariantsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoeVariants.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedShoeVariants))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoeVariantsToMatchAllProperties(updatedShoeVariants);
    }

    @Test
    @Transactional
    void putNonExistingShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoeVariants.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariants))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shoeVariants))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shoeVariants)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoeVariantsWithPatch() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariants using partial update
        ShoeVariants partialUpdatedShoeVariants = new ShoeVariants();
        partialUpdatedShoeVariants.setId(shoeVariants.getId());

        partialUpdatedShoeVariants.quantity(UPDATED_QUANTITY).photoUrl(UPDATED_PHOTO_URL);

        restShoeVariantsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariants.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariants))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariants in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoeVariants, shoeVariants),
            getPersistedShoeVariants(shoeVariants)
        );
    }

    @Test
    @Transactional
    void fullUpdateShoeVariantsWithPatch() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoeVariants using partial update
        ShoeVariants partialUpdatedShoeVariants = new ShoeVariants();
        partialUpdatedShoeVariants.setId(shoeVariants.getId());

        partialUpdatedShoeVariants.quantity(UPDATED_QUANTITY).status(UPDATED_STATUS).photoUrl(UPDATED_PHOTO_URL);

        restShoeVariantsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoeVariants.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShoeVariants))
            )
            .andExpect(status().isOk());

        // Validate the ShoeVariants in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoeVariantsUpdatableFieldsEquals(partialUpdatedShoeVariants, getPersistedShoeVariants(partialUpdatedShoeVariants));
    }

    @Test
    @Transactional
    void patchNonExistingShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoeVariants.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariants))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shoeVariants))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoeVariants() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoeVariants.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoeVariantsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shoeVariants)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoeVariants in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoeVariants() throws Exception {
        // Initialize the database
        shoeVariantsRepository.saveAndFlush(shoeVariants);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoeVariants
        restShoeVariantsMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoeVariants.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoeVariantsRepository.count();
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

    protected ShoeVariants getPersistedShoeVariants(ShoeVariants shoeVariants) {
        return shoeVariantsRepository.findById(shoeVariants.getId()).orElseThrow();
    }

    protected void assertPersistedShoeVariantsToMatchAllProperties(ShoeVariants expectedShoeVariants) {
        assertShoeVariantsAllPropertiesEquals(expectedShoeVariants, getPersistedShoeVariants(expectedShoeVariants));
    }

    protected void assertPersistedShoeVariantsToMatchUpdatableProperties(ShoeVariants expectedShoeVariants) {
        assertShoeVariantsAllUpdatablePropertiesEquals(expectedShoeVariants, getPersistedShoeVariants(expectedShoeVariants));
    }
}
