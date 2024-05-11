package com.shoes.domain;

import static com.shoes.domain.CartItemsTestSamples.*;
import static com.shoes.domain.CartTestSamples.*;
import static com.shoes.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cart.class);
        Cart cart1 = getCartSample1();
        Cart cart2 = new Cart();
        assertThat(cart1).isNotEqualTo(cart2);

        cart2.setId(cart1.getId());
        assertThat(cart1).isEqualTo(cart2);

        cart2 = getCartSample2();
        assertThat(cart1).isNotEqualTo(cart2);
    }

    @Test
    void customerTest() throws Exception {
        Cart cart = getCartRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        cart.setCustomer(customerBack);
        assertThat(cart.getCustomer()).isEqualTo(customerBack);

        cart.customer(null);
        assertThat(cart.getCustomer()).isNull();
    }

    @Test
    void cartItemsTest() throws Exception {
        Cart cart = getCartRandomSampleGenerator();
        CartItems cartItemsBack = getCartItemsRandomSampleGenerator();

        cart.addCartItems(cartItemsBack);
        assertThat(cart.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getCart()).isEqualTo(cart);

        cart.removeCartItems(cartItemsBack);
        assertThat(cart.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getCart()).isNull();

        cart.cartItems(new HashSet<>(Set.of(cartItemsBack)));
        assertThat(cart.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getCart()).isEqualTo(cart);

        cart.setCartItems(new HashSet<>());
        assertThat(cart.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getCart()).isNull();
    }
}
