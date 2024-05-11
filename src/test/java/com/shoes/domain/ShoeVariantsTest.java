package com.shoes.domain;

import static com.shoes.domain.CartItemsTestSamples.*;
import static com.shoes.domain.OrderItemsTestSamples.*;
import static com.shoes.domain.SalesTestSamples.*;
import static com.shoes.domain.ShoeVariantColorsTestSamples.*;
import static com.shoes.domain.ShoeVariantSizesTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static com.shoes.domain.SizesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShoeVariantsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoeVariants.class);
        ShoeVariants shoeVariants1 = getShoeVariantsSample1();
        ShoeVariants shoeVariants2 = new ShoeVariants();
        assertThat(shoeVariants1).isNotEqualTo(shoeVariants2);

        shoeVariants2.setId(shoeVariants1.getId());
        assertThat(shoeVariants1).isEqualTo(shoeVariants2);

        shoeVariants2 = getShoeVariantsSample2();
        assertThat(shoeVariants1).isNotEqualTo(shoeVariants2);
    }

    @Test
    void shoesTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        Shoes shoesBack = getShoesRandomSampleGenerator();

        shoeVariants.setShoes(shoesBack);
        assertThat(shoeVariants.getShoes()).isEqualTo(shoesBack);

        shoeVariants.shoes(null);
        assertThat(shoeVariants.getShoes()).isNull();
    }

    @Test
    void salesTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        Sales salesBack = getSalesRandomSampleGenerator();

        shoeVariants.setSales(salesBack);
        assertThat(shoeVariants.getSales()).isEqualTo(salesBack);

        shoeVariants.sales(null);
        assertThat(shoeVariants.getSales()).isNull();
    }

    @Test
    void sizesTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        Sizes sizesBack = getSizesRandomSampleGenerator();

        shoeVariants.addSizes(sizesBack);
        assertThat(shoeVariants.getSizes()).containsOnly(sizesBack);

        shoeVariants.removeSizes(sizesBack);
        assertThat(shoeVariants.getSizes()).doesNotContain(sizesBack);

        shoeVariants.sizes(new HashSet<>(Set.of(sizesBack)));
        assertThat(shoeVariants.getSizes()).containsOnly(sizesBack);

        shoeVariants.setSizes(new HashSet<>());
        assertThat(shoeVariants.getSizes()).doesNotContain(sizesBack);
    }

    @Test
    void shoeVariantColorsTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        ShoeVariantColors shoeVariantColorsBack = getShoeVariantColorsRandomSampleGenerator();

        shoeVariants.addShoeVariantColors(shoeVariantColorsBack);
        assertThat(shoeVariants.getShoeVariantColors()).containsOnly(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.removeShoeVariantColors(shoeVariantColorsBack);
        assertThat(shoeVariants.getShoeVariantColors()).doesNotContain(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getShoeVariants()).isNull();

        shoeVariants.shoeVariantColors(new HashSet<>(Set.of(shoeVariantColorsBack)));
        assertThat(shoeVariants.getShoeVariantColors()).containsOnly(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.setShoeVariantColors(new HashSet<>());
        assertThat(shoeVariants.getShoeVariantColors()).doesNotContain(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getShoeVariants()).isNull();
    }

    @Test
    void shoeVariantSizesTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        ShoeVariantSizes shoeVariantSizesBack = getShoeVariantSizesRandomSampleGenerator();

        shoeVariants.addShoeVariantSizes(shoeVariantSizesBack);
        assertThat(shoeVariants.getShoeVariantSizes()).containsOnly(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.removeShoeVariantSizes(shoeVariantSizesBack);
        assertThat(shoeVariants.getShoeVariantSizes()).doesNotContain(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getShoeVariants()).isNull();

        shoeVariants.shoeVariantSizes(new HashSet<>(Set.of(shoeVariantSizesBack)));
        assertThat(shoeVariants.getShoeVariantSizes()).containsOnly(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.setShoeVariantSizes(new HashSet<>());
        assertThat(shoeVariants.getShoeVariantSizes()).doesNotContain(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getShoeVariants()).isNull();
    }

    @Test
    void cartItemsTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        CartItems cartItemsBack = getCartItemsRandomSampleGenerator();

        shoeVariants.addCartItems(cartItemsBack);
        assertThat(shoeVariants.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.removeCartItems(cartItemsBack);
        assertThat(shoeVariants.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getShoeVariants()).isNull();

        shoeVariants.cartItems(new HashSet<>(Set.of(cartItemsBack)));
        assertThat(shoeVariants.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.setCartItems(new HashSet<>());
        assertThat(shoeVariants.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getShoeVariants()).isNull();
    }

    @Test
    void orderItemsTest() throws Exception {
        ShoeVariants shoeVariants = getShoeVariantsRandomSampleGenerator();
        OrderItems orderItemsBack = getOrderItemsRandomSampleGenerator();

        shoeVariants.addOrderItems(orderItemsBack);
        assertThat(shoeVariants.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.removeOrderItems(orderItemsBack);
        assertThat(shoeVariants.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getShoeVariants()).isNull();

        shoeVariants.orderItems(new HashSet<>(Set.of(orderItemsBack)));
        assertThat(shoeVariants.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getShoeVariants()).isEqualTo(shoeVariants);

        shoeVariants.setOrderItems(new HashSet<>());
        assertThat(shoeVariants.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getShoeVariants()).isNull();
    }
}
