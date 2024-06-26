package com.shoes.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoeVariantSizesAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantSizesAllPropertiesEquals(ShoeVariantSizes expected, ShoeVariantSizes actual) {
        assertShoeVariantSizesAutoGeneratedPropertiesEquals(expected, actual);
        assertShoeVariantSizesAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantSizesAllUpdatablePropertiesEquals(ShoeVariantSizes expected, ShoeVariantSizes actual) {
        assertShoeVariantSizesUpdatableFieldsEquals(expected, actual);
        assertShoeVariantSizesUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantSizesAutoGeneratedPropertiesEquals(ShoeVariantSizes expected, ShoeVariantSizes actual) {
        assertThat(expected)
            .as("Verify ShoeVariantSizes auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantSizesUpdatableFieldsEquals(ShoeVariantSizes expected, ShoeVariantSizes actual) {
        assertThat(expected)
            .as("Verify ShoeVariantSizes relevant properties")
            .satisfies(e -> assertThat(e.getQuantity()).as("check quantity").isEqualTo(actual.getQuantity()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShoeVariantSizesUpdatableRelationshipsEquals(ShoeVariantSizes expected, ShoeVariantSizes actual) {
        assertThat(expected)
            .as("Verify ShoeVariantSizes relationships")
            .satisfies(e -> assertThat(e.getSizes()).as("check sizes").isEqualTo(actual.getSizes()))
            .satisfies(e -> assertThat(e.getShoeVariants()).as("check shoeVariants").isEqualTo(actual.getShoeVariants()));
    }
}
