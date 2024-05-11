package com.shoes.domain;

import static com.shoes.domain.BrandTestSamples.*;
import static com.shoes.domain.CategoryTestSamples.*;
import static com.shoes.domain.CollectionTestSamples.*;
import static com.shoes.domain.ShoePurposeTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShoesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shoes.class);
        Shoes shoes1 = getShoesSample1();
        Shoes shoes2 = new Shoes();
        assertThat(shoes1).isNotEqualTo(shoes2);

        shoes2.setId(shoes1.getId());
        assertThat(shoes1).isEqualTo(shoes2);

        shoes2 = getShoesSample2();
        assertThat(shoes1).isNotEqualTo(shoes2);
    }

    @Test
    void categoryTest() throws Exception {
        Shoes shoes = getShoesRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        shoes.setCategory(categoryBack);
        assertThat(shoes.getCategory()).isEqualTo(categoryBack);

        shoes.category(null);
        assertThat(shoes.getCategory()).isNull();
    }

    @Test
    void brandTest() throws Exception {
        Shoes shoes = getShoesRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        shoes.setBrand(brandBack);
        assertThat(shoes.getBrand()).isEqualTo(brandBack);

        shoes.brand(null);
        assertThat(shoes.getBrand()).isNull();
    }

    @Test
    void collectionTest() throws Exception {
        Shoes shoes = getShoesRandomSampleGenerator();
        Collection collectionBack = getCollectionRandomSampleGenerator();

        shoes.setCollection(collectionBack);
        assertThat(shoes.getCollection()).isEqualTo(collectionBack);

        shoes.collection(null);
        assertThat(shoes.getCollection()).isNull();
    }

    @Test
    void shoePurposeTest() throws Exception {
        Shoes shoes = getShoesRandomSampleGenerator();
        ShoePurpose shoePurposeBack = getShoePurposeRandomSampleGenerator();

        shoes.setShoePurpose(shoePurposeBack);
        assertThat(shoes.getShoePurpose()).isEqualTo(shoePurposeBack);

        shoes.shoePurpose(null);
        assertThat(shoes.getShoePurpose()).isNull();
    }

    @Test
    void shoeVariantsTest() throws Exception {
        Shoes shoes = getShoesRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        shoes.addShoeVariants(shoeVariantsBack);
        assertThat(shoes.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getShoes()).isEqualTo(shoes);

        shoes.removeShoeVariants(shoeVariantsBack);
        assertThat(shoes.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getShoes()).isNull();

        shoes.shoeVariants(new HashSet<>(Set.of(shoeVariantsBack)));
        assertThat(shoes.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getShoes()).isEqualTo(shoes);

        shoes.setShoeVariants(new HashSet<>());
        assertThat(shoes.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getShoes()).isNull();
    }
}
