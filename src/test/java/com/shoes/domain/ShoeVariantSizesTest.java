package com.shoes.domain;

import static com.shoes.domain.ShoeVariantSizesTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.SizesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoeVariantSizesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoeVariantSizes.class);
        ShoeVariantSizes shoeVariantSizes1 = getShoeVariantSizesSample1();
        ShoeVariantSizes shoeVariantSizes2 = new ShoeVariantSizes();
        assertThat(shoeVariantSizes1).isNotEqualTo(shoeVariantSizes2);

        shoeVariantSizes2.setId(shoeVariantSizes1.getId());
        assertThat(shoeVariantSizes1).isEqualTo(shoeVariantSizes2);

        shoeVariantSizes2 = getShoeVariantSizesSample2();
        assertThat(shoeVariantSizes1).isNotEqualTo(shoeVariantSizes2);
    }

    @Test
    void sizesTest() throws Exception {
        ShoeVariantSizes shoeVariantSizes = getShoeVariantSizesRandomSampleGenerator();
        Sizes sizesBack = getSizesRandomSampleGenerator();

        shoeVariantSizes.setSizes(sizesBack);
        assertThat(shoeVariantSizes.getSizes()).isEqualTo(sizesBack);

        shoeVariantSizes.sizes(null);
        assertThat(shoeVariantSizes.getSizes()).isNull();
    }

    @Test
    void shoeVariantsTest() throws Exception {
        ShoeVariantSizes shoeVariantSizes = getShoeVariantSizesRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        shoeVariantSizes.setShoeVariants(shoeVariantsBack);
        assertThat(shoeVariantSizes.getShoeVariants()).isEqualTo(shoeVariantsBack);

        shoeVariantSizes.shoeVariants(null);
        assertThat(shoeVariantSizes.getShoeVariants()).isNull();
    }
}
