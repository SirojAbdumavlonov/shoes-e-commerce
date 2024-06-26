package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class SizesAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSizesAllPropertiesEquals(Sizes expected, Sizes actual) {
        assertSizesAutoGeneratedPropertiesEquals(expected, actual);
        assertSizesAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSizesAllUpdatablePropertiesEquals(Sizes expected, Sizes actual) {
        assertSizesUpdatableFieldsEquals(expected, actual);
        assertSizesUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSizesAutoGeneratedPropertiesEquals(Sizes expected, Sizes actual) {
        assertThat(expected)
            .as("Verify Sizes auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSizesUpdatableFieldsEquals(Sizes expected, Sizes actual) {
        assertThat(expected)
            .as("Verify Sizes relevant properties")
            .satisfies(e -> assertThat(e.getSizeInNumbers()).as("check sizeInNumbers").isEqualTo(actual.getSizeInNumbers()))
            .satisfies(e -> assertThat(e.getSizeInLetters()).as("check sizeInLetters").isEqualTo(actual.getSizeInLetters()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSizesUpdatableRelationshipsEquals(Sizes expected, Sizes actual) {
        assertThat(expected)
            .as("Verify Sizes relationships")
            .satisfies(e -> assertThat(e.getShoeVariants()).as("check shoeVariants").isEqualTo(actual.getShoeVariants()));
    }
}
