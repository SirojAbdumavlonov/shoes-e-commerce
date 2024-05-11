package com.shoes.domain;

import static com.shoes.domain.CategoryTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void shoesTest() throws Exception {
        Category category = getCategoryRandomSampleGenerator();
        Shoes shoesBack = getShoesRandomSampleGenerator();

        category.addShoes(shoesBack);
        assertThat(category.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getCategory()).isEqualTo(category);

        category.removeShoes(shoesBack);
        assertThat(category.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getCategory()).isNull();

        category.shoes(new HashSet<>(Set.of(shoesBack)));
        assertThat(category.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getCategory()).isEqualTo(category);

        category.setShoes(new HashSet<>());
        assertThat(category.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getCategory()).isNull();
    }
}
