package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerDetailsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerDetailsAllPropertiesEquals(CustomerDetails expected, CustomerDetails actual) {
        assertCustomerDetailsAutoGeneratedPropertiesEquals(expected, actual);
        assertCustomerDetailsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerDetailsAllUpdatablePropertiesEquals(CustomerDetails expected, CustomerDetails actual) {
        assertCustomerDetailsUpdatableFieldsEquals(expected, actual);
        assertCustomerDetailsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerDetailsAutoGeneratedPropertiesEquals(CustomerDetails expected, CustomerDetails actual) {
        assertThat(expected)
            .as("Verify CustomerDetails auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerDetailsUpdatableFieldsEquals(CustomerDetails expected, CustomerDetails actual) {
        assertThat(expected)
            .as("Verify CustomerDetails relevant properties")
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getSecondName()).as("check secondName").isEqualTo(actual.getSecondName()))
            .satisfies(e -> assertThat(e.getGender()).as("check gender").isEqualTo(actual.getGender()))
            .satisfies(e -> assertThat(e.getDateOfBirth()).as("check dateOfBirth").isEqualTo(actual.getDateOfBirth()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerDetailsUpdatableRelationshipsEquals(CustomerDetails expected, CustomerDetails actual) {
        assertThat(expected)
            .as("Verify CustomerDetails relationships")
            .satisfies(e -> assertThat(e.getCustomer()).as("check customer").isEqualTo(actual.getCustomer()));
    }
}