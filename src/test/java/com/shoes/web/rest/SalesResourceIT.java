package com.shoes.web.rest;

import static com.shoes.domain.SalesAsserts.*;
import static com.shoes.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.IntegrationTest;
import com.shoes.domain.Sales;
import com.shoes.repository.SalesRepository;
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
 * Integration tests for the {@link SalesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalesResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_DISCOUNT_PERCENTAGE = 1;
    private static final Integer UPDATED_DISCOUNT_PERCENTAGE = 2;

    private static final Integer DEFAULT_NEW_PRICE = 1;
    private static final Integer UPDATED_NEW_PRICE = 2;

    private static final String ENTITY_API_URL = "/api/sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesMockMvc;

    private Sales sales;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sales createEntity(EntityManager em) {
        Sales sales = new Sales()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .discountPercentage(DEFAULT_DISCOUNT_PERCENTAGE)
            .newPrice(DEFAULT_NEW_PRICE);
        return sales;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sales createUpdatedEntity(EntityManager em) {
        Sales sales = new Sales()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .discountPercentage(UPDATED_DISCOUNT_PERCENTAGE)
            .newPrice(UPDATED_NEW_PRICE);
        return sales;
    }

    @BeforeEach
    public void initTest() {
        sales = createEntity(em);
    }

    @Test
    @Transactional
    void createSales() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sales
        var returnedSales = om.readValue(
            restSalesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sales)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Sales.class
        );

        // Validate the Sales in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSalesUpdatableFieldsEquals(returnedSales, getPersistedSales(returnedSales));
    }

    @Test
    @Transactional
    void createSalesWithExistingId() throws Exception {
        // Create the Sales with an existing ID
        sales.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sales)))
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sales.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].discountPercentage").value(hasItem(DEFAULT_DISCOUNT_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].newPrice").value(hasItem(DEFAULT_NEW_PRICE)));
    }

    @Test
    @Transactional
    void getSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get the sales
        restSalesMockMvc
            .perform(get(ENTITY_API_URL_ID, sales.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sales.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.discountPercentage").value(DEFAULT_DISCOUNT_PERCENTAGE))
            .andExpect(jsonPath("$.newPrice").value(DEFAULT_NEW_PRICE));
    }

    @Test
    @Transactional
    void getNonExistingSales() throws Exception {
        // Get the sales
        restSalesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sales
        Sales updatedSales = salesRepository.findById(sales.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSales are not directly saved in db
        em.detach(updatedSales);
        updatedSales
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .discountPercentage(UPDATED_DISCOUNT_PERCENTAGE)
            .newPrice(UPDATED_NEW_PRICE);

        restSalesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSales.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSales))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSalesToMatchAllProperties(updatedSales);
    }

    @Test
    @Transactional
    void putNonExistingSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(put(ENTITY_API_URL_ID, sales.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sales)))
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sales))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sales)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesWithPatch() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sales using partial update
        Sales partialUpdatedSales = new Sales();
        partialUpdatedSales.setId(sales.getId());

        partialUpdatedSales.startDate(UPDATED_START_DATE).discountPercentage(UPDATED_DISCOUNT_PERCENTAGE);

        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSales.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSales))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSales, sales), getPersistedSales(sales));
    }

    @Test
    @Transactional
    void fullUpdateSalesWithPatch() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sales using partial update
        Sales partialUpdatedSales = new Sales();
        partialUpdatedSales.setId(sales.getId());

        partialUpdatedSales
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .discountPercentage(UPDATED_DISCOUNT_PERCENTAGE)
            .newPrice(UPDATED_NEW_PRICE);

        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSales.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSales))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesUpdatableFieldsEquals(partialUpdatedSales, getPersistedSales(partialUpdatedSales));
    }

    @Test
    @Transactional
    void patchNonExistingSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sales.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sales))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sales))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSales() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sales.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sales)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sales in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sales
        restSalesMockMvc
            .perform(delete(ENTITY_API_URL_ID, sales.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return salesRepository.count();
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

    protected Sales getPersistedSales(Sales sales) {
        return salesRepository.findById(sales.getId()).orElseThrow();
    }

    protected void assertPersistedSalesToMatchAllProperties(Sales expectedSales) {
        assertSalesAllPropertiesEquals(expectedSales, getPersistedSales(expectedSales));
    }

    protected void assertPersistedSalesToMatchUpdatableProperties(Sales expectedSales) {
        assertSalesAllUpdatablePropertiesEquals(expectedSales, getPersistedSales(expectedSales));
    }
}
