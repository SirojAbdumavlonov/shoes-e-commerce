package com.shoes.domain;

import static com.shoes.domain.CartItemsTestSamples.*;
import static com.shoes.domain.CartTestSamples.*;
import static com.shoes.domain.ColorsTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.SizesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItems.class);
        CartItems cartItems1 = getCartItemsSample1();
        CartItems cartItems2 = new CartItems();
        assertThat(cartItems1).isNotEqualTo(cartItems2);

        cartItems2.setId(cartItems1.getId());
        assertThat(cartItems1).isEqualTo(cartItems2);

        cartItems2 = getCartItemsSample2();
        assertThat(cartItems1).isNotEqualTo(cartItems2);
    }

    @Test
    void cartTest() throws Exception {
        CartItems cartItems = getCartItemsRandomSampleGenerator();
        Cart cartBack = getCartRandomSampleGenerator();

        cartItems.setCart(cartBack);
        assertThat(cartItems.getCart()).isEqualTo(cartBack);

        cartItems.cart(null);
        assertThat(cartItems.getCart()).isNull();
    }

    @Test
    void colorsTest() throws Exception {
        CartItems cartItems = getCartItemsRandomSampleGenerator();
        Colors colorsBack = getColorsRandomSampleGenerator();

        cartItems.setColors(colorsBack);
        assertThat(cartItems.getColors()).isEqualTo(colorsBack);

        cartItems.colors(null);
        assertThat(cartItems.getColors()).isNull();
    }

    @Test
    void shoeVariantsTest() throws Exception {
        CartItems cartItems = getCartItemsRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        cartItems.setShoeVariants(shoeVariantsBack);
        assertThat(cartItems.getShoeVariants()).isEqualTo(shoeVariantsBack);

        cartItems.shoeVariants(null);
        assertThat(cartItems.getShoeVariants()).isNull();
    }

    @Test
    void sizesTest() throws Exception {
        CartItems cartItems = getCartItemsRandomSampleGenerator();
        Sizes sizesBack = getSizesRandomSampleGenerator();

        cartItems.setSizes(sizesBack);
        assertThat(cartItems.getSizes()).isEqualTo(sizesBack);

        cartItems.sizes(null);
        assertThat(cartItems.getSizes()).isNull();
    }
}
