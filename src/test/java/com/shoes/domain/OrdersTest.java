package com.shoes.domain;

import static com.shoes.domain.CustomerTestSamples.*;
import static com.shoes.domain.OrderItemsTestSamples.*;
import static com.shoes.domain.OrdersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Orders.class);
        Orders orders1 = getOrdersSample1();
        Orders orders2 = new Orders();
        assertThat(orders1).isNotEqualTo(orders2);

        orders2.setId(orders1.getId());
        assertThat(orders1).isEqualTo(orders2);

        orders2 = getOrdersSample2();
        assertThat(orders1).isNotEqualTo(orders2);
    }

    @Test
    void customerTest() throws Exception {
        Orders orders = getOrdersRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        orders.setCustomer(customerBack);
        assertThat(orders.getCustomer()).isEqualTo(customerBack);

        orders.customer(null);
        assertThat(orders.getCustomer()).isNull();
    }

    @Test
    void orderItemsTest() throws Exception {
        Orders orders = getOrdersRandomSampleGenerator();
        OrderItems orderItemsBack = getOrderItemsRandomSampleGenerator();

        orders.addOrderItems(orderItemsBack);
        assertThat(orders.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getOrders()).isEqualTo(orders);

        orders.removeOrderItems(orderItemsBack);
        assertThat(orders.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getOrders()).isNull();

        orders.orderItems(new HashSet<>(Set.of(orderItemsBack)));
        assertThat(orders.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getOrders()).isEqualTo(orders);

        orders.setOrderItems(new HashSet<>());
        assertThat(orders.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getOrders()).isNull();
    }
}
