package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoeVariantsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantsAllPropertiesEquals(ShoeVariants expected, ShoeVariants actual) {
        assertShoeVariantsAutoGeneratedPropertiesEquals(expected, actual);
        assertShoeVariantsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantsAllUpdatablePropertiesEquals(ShoeVariants expected, ShoeVariants actual) {
        assertShoeVariantsUpdatableFieldsEquals(expected, actual);
        assertShoeVariantsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantsAutoGeneratedPropertiesEquals(ShoeVariants expected, ShoeVariants actual) {
        assertThat(expected)
            .as("Verify ShoeVariants auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantsUpdatableFieldsEquals(ShoeVariants expected, ShoeVariants actual) {
        assertThat(expected)
            .as("Verify ShoeVariants relevant properties")
            .satisfies(e -> assertThat(e.getQuantity()).as("check quantity").isEqualTo(actual.getQuantity()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e -> assertThat(e.getPhotoUrl()).as("check photoUrl").isEqualTo(actual.getPhotoUrl()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantsUpdatableRelationshipsEquals(ShoeVariants expected, ShoeVariants actual) {
        assertThat(expected)
            .as("Verify ShoeVariants relationships")
            .satisfies(e -> assertThat(e.getShoes()).as("check shoes").isEqualTo(actual.getShoes()))
            .satisfies(e -> assertThat(e.getSales()).as("check sales").isEqualTo(actual.getSales()))
            .satisfies(e -> assertThat(e.getSizes()).as("check sizes").isEqualTo(actual.getSizes()));
    }
}
