package com.shoes.domain;

import static com.shoes.domain.BrandTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = getBrandSample1();
        Brand brand2 = new Brand();
        assertThat(brand1).isNotEqualTo(brand2);

        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);

        brand2 = getBrandSample2();
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    void shoesTest() throws Exception {
        Brand brand = getBrandRandomSampleGenerator();
        Shoes shoesBack = getShoesRandomSampleGenerator();

        brand.addShoes(shoesBack);
        assertThat(brand.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getBrand()).isEqualTo(brand);

        brand.removeShoes(shoesBack);
        assertThat(brand.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getBrand()).isNull();

        brand.shoes(new HashSet<>(Set.of(shoesBack)));
        assertThat(brand.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getBrand()).isEqualTo(brand);

        brand.setShoes(new HashSet<>());
        assertThat(brand.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getBrand()).isNull();
    }
}
