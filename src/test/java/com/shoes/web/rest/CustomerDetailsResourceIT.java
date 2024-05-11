package com.shoes.web.rest;

import static com.shoes.domain.CustomerDetailsAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.CustomerDetails;
import com.shoes.repository.CustomerDetailsRepository;
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
 * Integration tests for the {@link CustomerDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomerDetailsResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SECOND_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SECOND_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GENDER = "AAAAAAAAAA";
    private static final String UPDATED_GENDER = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_OF_BIRTH = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_OF_BIRTH = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/customer-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerDetailsMockMvc;

    private CustomerDetails customerDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerDetails createEntity(EntityManager em) {
        CustomerDetails customerDetails = new CustomerDetails()
            .firstName(DEFAULT_FIRST_NAME)
            .secondName(DEFAULT_SECOND_NAME)
            .gender(DEFAULT_GENDER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
        return customerDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerDetails createUpdatedEntity(EntityManager em) {
        CustomerDetails customerDetails = new CustomerDetails()
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);
        return customerDetails;
    }

    @BeforeEach
    public void initTest() {
        customerDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createCustomerDetails() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerDetails
        var returnedCustomerDetails = om.readValue(
            restCustomerDetailsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomerDetails.class
        );

        // Validate the CustomerDetails in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCustomerDetailsUpdatableFieldsEquals(returnedCustomerDetails, getPersistedCustomerDetails(returnedCustomerDetails));
    }

    @Test
    @Transactional
    void createCustomerDetailsWithExistingId() throws Exception {
        // Create the CustomerDetails with an existing ID
        customerDetails.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCustomerDetails() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        // Get all the customerDetailsList
        restCustomerDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].secondName").value(hasItem(DEFAULT_SECOND_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())));
    }

    @Test
    @Transactional
    void getCustomerDetails() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        // Get the customerDetails
        restCustomerDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, customerDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customerDetails.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.secondName").value(DEFAULT_SECOND_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCustomerDetails() throws Exception {
        // Get the customerDetails
        restCustomerDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomerDetails() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails
        CustomerDetails updatedCustomerDetails = customerDetailsRepository.findById(customerDetails.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomerDetails are not directly saved in db
        em.detach(updatedCustomerDetails);
        updatedCustomerDetails
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomerDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerDetailsToMatchAllProperties(updatedCustomerDetails);
    }

    @Test
    @Transactional
    void putNonExistingCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerDetailsWithPatch() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails using partial update
        CustomerDetails partialUpdatedCustomerDetails = new CustomerDetails();
        partialUpdatedCustomerDetails.setId(customerDetails.getId());

        partialUpdatedCustomerDetails.firstName(UPDATED_FIRST_NAME).dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerDetailsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerDetails, customerDetails),
            getPersistedCustomerDetails(customerDetails)
        );
    }

    @Test
    @Transactional
    void fullUpdateCustomerDetailsWithPatch() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails using partial update
        CustomerDetails partialUpdatedCustomerDetails = new CustomerDetails();
        partialUpdatedCustomerDetails.setId(customerDetails.getId());

        partialUpdatedCustomerDetails
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerDetailsUpdatableFieldsEquals(
            partialUpdatedCustomerDetails,
            getPersistedCustomerDetails(partialUpdatedCustomerDetails)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomerDetails() throws Exception {
        // Initialize the database
        customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerDetails
        restCustomerDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, customerDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerDetailsRepository.count();
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

    protected CustomerDetails getPersistedCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.findById(customerDetails.getId()).orElseThrow();
    }

    protected void assertPersistedCustomerDetailsToMatchAllProperties(CustomerDetails expectedCustomerDetails) {
        assertCustomerDetailsAllPropertiesEquals(expectedCustomerDetails, getPersistedCustomerDetails(expectedCustomerDetails));
    }

    protected void assertPersistedCustomerDetailsToMatchUpdatableProperties(CustomerDetails expectedCustomerDetails) {
        assertCustomerDetailsAllUpdatablePropertiesEquals(expectedCustomerDetails, getPersistedCustomerDetails(expectedCustomerDetails));
    }
}
