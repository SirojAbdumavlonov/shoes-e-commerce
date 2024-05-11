package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoesAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoesAllPropertiesEquals(Shoes expected, Shoes actual) {
        assertShoesAutoGeneratedPropertiesEquals(expected, actual);
        assertShoesAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoesAllUpdatablePropertiesEquals(Shoes expected, Shoes actual) {
        assertShoesUpdatableFieldsEquals(expected, actual);
        assertShoesUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoesAutoGeneratedPropertiesEquals(Shoes expected, Shoes actual) {
        assertThat(expected)
            .as("Verify Shoes auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoesUpdatableFieldsEquals(Shoes expected, Shoes actual) {
        assertThat(expected)
            .as("Verify Shoes relevant properties")
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoesUpdatableRelationshipsEquals(Shoes expected, Shoes actual) {
        assertThat(expected)
            .as("Verify Shoes relationships")
            .satisfies(e -> assertThat(e.getCategory()).as("check category").isEqualTo(actual.getCategory()))
            .satisfies(e -> assertThat(e.getBrand()).as("check brand").isEqualTo(actual.getBrand()))
            .satisfies(e -> assertThat(e.getCollection()).as("check collection").isEqualTo(actual.getCollection()))
            .satisfies(e -> assertThat(e.getShoePurpose()).as("check shoePurpose").isEqualTo(actual.getShoePurpose()));
    }
}