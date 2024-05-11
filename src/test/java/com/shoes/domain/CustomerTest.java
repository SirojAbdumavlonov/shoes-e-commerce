package com.shoes.domain;

import static com.shoes.domain.CartTestSamples.*;
import static com.shoes.domain.CustomerDetailsTestSamples.*;
import static com.shoes.domain.CustomerTestSamples.*;
import static com.shoes.domain.OrdersTestSamples.*;
import static com.shoes.domain.WishListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void cartTest() throws Exception {
        Customer customer = getCustomerRandomSampleGenerator();
        Cart cartBack = getCartRandomSampleGenerator();

        customer.setCart(cartBack);
        assertThat(customer.getCart()).isEqualTo(cartBack);
        assertThat(cartBack.getCustomer()).isEqualTo(customer);

        customer.cart(null);
        assertThat(customer.getCart()).isNull();
        assertThat(cartBack.getCustomer()).isNull();
    }

    @Test
    void customerDetailsTest() throws Exception {
        Customer customer = getCustomerRandomSampleGenerator();
        CustomerDetails customerDetailsBack = getCustomerDetailsRandomSampleGenerator();

        customer.setCustomerDetails(customerDetailsBack);
        assertThat(customer.getCustomerDetails()).isEqualTo(customerDetailsBack);
        assertThat(customerDetailsBack.getCustomer()).isEqualTo(customer);

        customer.customerDetails(null);
        assertThat(customer.getCustomerDetails()).isNull();
        assertThat(customerDetailsBack.getCustomer()).isNull();
    }

    @Test
    void wishListTest() throws Exception {
        Customer customer = getCustomerRandomSampleGenerator();
        WishList wishListBack = getWishListRandomSampleGenerator();

        customer.setWishList(wishListBack);
        assertThat(customer.getWishList()).isEqualTo(wishListBack);
        assertThat(wishListBack.getCustomer()).isEqualTo(customer);

        customer.wishList(null);
        assertThat(customer.getWishList()).isNull();
        assertThat(wishListBack.getCustomer()).isNull();
    }

    @Test
    void ordersTest() throws Exception {
        Customer customer = getCustomerRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        customer.addOrders(ordersBack);
        assertThat(customer.getOrders()).containsOnly(ordersBack);
        assertThat(ordersBack.getCustomer()).isEqualTo(customer);

        customer.removeOrders(ordersBack);
        assertThat(customer.getOrders()).doesNotContain(ordersBack);
        assertThat(ordersBack.getCustomer()).isNull();

        customer.orders(new HashSet<>(Set.of(ordersBack)));
        assertThat(customer.getOrders()).containsOnly(ordersBack);
        assertThat(ordersBack.getCustomer()).isEqualTo(customer);

        customer.setOrders(new HashSet<>());
        assertThat(customer.getOrders()).doesNotContain(ordersBack);
        assertThat(ordersBack.getCustomer()).isNull();
    }
}
