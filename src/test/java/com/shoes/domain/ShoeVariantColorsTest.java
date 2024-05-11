package com.shoes.domain;

import static com.shoes.domain.ColorsTestSamples.*;
import static com.shoes.domain.ShoeVariantColorsTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoeVariantColorsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoeVariantColors.class);
        ShoeVariantColors shoeVariantColors1 = getShoeVariantColorsSample1();
        ShoeVariantColors shoeVariantColors2 = new ShoeVariantColors();
        assertThat(shoeVariantColors1).isNotEqualTo(shoeVariantColors2);

        shoeVariantColors2.setId(shoeVariantColors1.getId());
        assertThat(shoeVariantColors1).isEqualTo(shoeVariantColors2);

        shoeVariantColors2 = getShoeVariantColorsSample2();
        assertThat(shoeVariantColors1).isNotEqualTo(shoeVariantColors2);
    }

    @Test
    void shoeVariantsTest() throws Exception {
        ShoeVariantColors shoeVariantColors = getShoeVariantColorsRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        shoeVariantColors.setShoeVariants(shoeVariantsBack);
        assertThat(shoeVariantColors.getShoeVariants()).isEqualTo(shoeVariantsBack);

        shoeVariantColors.shoeVariants(null);
        assertThat(shoeVariantColors.getShoeVariants()).isNull();
    }

    @Test
    void colorsTest() throws Exception {
        ShoeVariantColors shoeVariantColors = getShoeVariantColorsRandomSampleGenerator();
        Colors colorsBack = getColorsRandomSampleGenerator();

        shoeVariantColors.setColors(colorsBack);
        assertThat(shoeVariantColors.getColors()).isEqualTo(colorsBack);

        shoeVariantColors.colors(null);
        assertThat(shoeVariantColors.getColors()).isNull();
    }
}
