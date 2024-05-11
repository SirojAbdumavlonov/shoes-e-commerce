package com.shoes.domain;

import static com.shoes.domain.CartItemsTestSamples.*;
import static com.shoes.domain.ColorsTestSamples.*;
import static com.shoes.domain.OrderItemsTestSamples.*;
import static com.shoes.domain.ShoeVariantColorsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ColorsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Colors.class);
        Colors colors1 = getColorsSample1();
        Colors colors2 = new Colors();
        assertThat(colors1).isNotEqualTo(colors2);

        colors2.setId(colors1.getId());
        assertThat(colors1).isEqualTo(colors2);

        colors2 = getColorsSample2();
        assertThat(colors1).isNotEqualTo(colors2);
    }

    @Test
    void shoeVariantColorsTest() throws Exception {
        Colors colors = getColorsRandomSampleGenerator();
        ShoeVariantColors shoeVariantColorsBack = getShoeVariantColorsRandomSampleGenerator();

        colors.addShoeVariantColors(shoeVariantColorsBack);
        assertThat(colors.getShoeVariantColors()).containsOnly(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getColors()).isEqualTo(colors);

        colors.removeShoeVariantColors(shoeVariantColorsBack);
        assertThat(colors.getShoeVariantColors()).doesNotContain(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getColors()).isNull();

        colors.shoeVariantColors(new HashSet<>(Set.of(shoeVariantColorsBack)));
        assertThat(colors.getShoeVariantColors()).containsOnly(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getColors()).isEqualTo(colors);

        colors.setShoeVariantColors(new HashSet<>());
        assertThat(colors.getShoeVariantColors()).doesNotContain(shoeVariantColorsBack);
        assertThat(shoeVariantColorsBack.getColors()).isNull();
    }

    @Test
    void cartItemsTest() throws Exception {
        Colors colors = getColorsRandomSampleGenerator();
        CartItems cartItemsBack = getCartItemsRandomSampleGenerator();

        colors.addCartItems(cartItemsBack);
        assertThat(colors.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getColors()).isEqualTo(colors);

        colors.removeCartItems(cartItemsBack);
        assertThat(colors.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getColors()).isNull();

        colors.cartItems(new HashSet<>(Set.of(cartItemsBack)));
        assertThat(colors.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getColors()).isEqualTo(colors);

        colors.setCartItems(new HashSet<>());
        assertThat(colors.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getColors()).isNull();
    }

    @Test
    void orderItemsTest() throws Exception {
        Colors colors = getColorsRandomSampleGenerator();
        OrderItems orderItemsBack = getOrderItemsRandomSampleGenerator();

        colors.addOrderItems(orderItemsBack);
        assertThat(colors.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getColors()).isEqualTo(colors);

        colors.removeOrderItems(orderItemsBack);
        assertThat(colors.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getColors()).isNull();

        colors.orderItems(new HashSet<>(Set.of(orderItemsBack)));
        assertThat(colors.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getColors()).isEqualTo(colors);

        colors.setOrderItems(new HashSet<>());
        assertThat(colors.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getColors()).isNull();
    }
}
