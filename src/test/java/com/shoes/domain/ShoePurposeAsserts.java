package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoePurposeAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoePurposeAllPropertiesEquals(ShoePurpose expected, ShoePurpose actual) {
        assertShoePurposeAutoGeneratedPropertiesEquals(expected, actual);
        assertShoePurposeAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoePurposeAllUpdatablePropertiesEquals(ShoePurpose expected, ShoePurpose actual) {
        assertShoePurposeUpdatableFieldsEquals(expected, actual);
        assertShoePurposeUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoePurposeAutoGeneratedPropertiesEquals(ShoePurpose expected, ShoePurpose actual) {
        assertThat(expected)
            .as("Verify ShoePurpose auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoePurposeUpdatableFieldsEquals(ShoePurpose expected, ShoePurpose actual) {
        assertThat(expected)
            .as("Verify ShoePurpose relevant properties")
            .satisfies(e -> assertThat(e.getType()).as("check type").isEqualTo(actual.getType()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoePurposeUpdatableRelationshipsEquals(ShoePurpose expected, ShoePurpose actual) {}
}
