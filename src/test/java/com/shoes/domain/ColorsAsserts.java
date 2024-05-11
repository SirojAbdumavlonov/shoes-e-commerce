package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertColorsAllPropertiesEquals(Colors expected, Colors actual) {
        assertColorsAutoGeneratedPropertiesEquals(expected, actual);
        assertColorsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertColorsAllUpdatablePropertiesEquals(Colors expected, Colors actual) {
        assertColorsUpdatableFieldsEquals(expected, actual);
        assertColorsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertColorsAutoGeneratedPropertiesEquals(Colors expected, Colors actual) {
        assertThat(expected)
            .as("Verify Colors auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertColorsUpdatableFieldsEquals(Colors expected, Colors actual) {
        assertThat(expected)
            .as("Verify Colors relevant properties")
            .satisfies(e -> assertThat(e.getColorName()).as("check colorName").isEqualTo(actual.getColorName()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertColorsUpdatableRelationshipsEquals(Colors expected, Colors actual) {}
}