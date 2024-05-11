package com.shoes.domain;

import static com.shoes.domain.SalesTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SalesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sales.class);
        Sales sales1 = getSalesSample1();
        Sales sales2 = new Sales();
        assertThat(sales1).isNotEqualTo(sales2);

        sales2.setId(sales1.getId());
        assertThat(sales1).isEqualTo(sales2);

        sales2 = getSalesSample2();
        assertThat(sales1).isNotEqualTo(sales2);
    }

    @Test
    void shoeVariantsTest() throws Exception {
        Sales sales = getSalesRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        sales.addShoeVariants(shoeVariantsBack);
        assertThat(sales.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSales()).isEqualTo(sales);

        sales.removeShoeVariants(shoeVariantsBack);
        assertThat(sales.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSales()).isNull();

        sales.shoeVariants(new HashSet<>(Set.of(shoeVariantsBack)));
        assertThat(sales.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSales()).isEqualTo(sales);

        sales.setShoeVariants(new HashSet<>());
        assertThat(sales.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSales()).isNull();
    }
}
