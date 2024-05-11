package com.shoes.domain;

import static com.shoes.domain.ColorsTestSamples.*;
import static com.shoes.domain.OrderItemsTestSamples.*;
import static com.shoes.domain.OrdersTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.SizesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItems.class);
        OrderItems orderItems1 = getOrderItemsSample1();
        OrderItems orderItems2 = new OrderItems();
        assertThat(orderItems1).isNotEqualTo(orderItems2);

        orderItems2.setId(orderItems1.getId());
        assertThat(orderItems1).isEqualTo(orderItems2);

        orderItems2 = getOrderItemsSample2();
        assertThat(orderItems1).isNotEqualTo(orderItems2);
    }

    @Test
    void ordersTest() throws Exception {
        OrderItems orderItems = getOrderItemsRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        orderItems.setOrders(ordersBack);
        assertThat(orderItems.getOrders()).isEqualTo(ordersBack);

        orderItems.orders(null);
        assertThat(orderItems.getOrders()).isNull();
    }

    @Test
    void colorsTest() throws Exception {
        OrderItems orderItems = getOrderItemsRandomSampleGenerator();
        Colors colorsBack = getColorsRandomSampleGenerator();

        orderItems.setColors(colorsBack);
        assertThat(orderItems.getColors()).isEqualTo(colorsBack);

        orderItems.colors(null);
        assertThat(orderItems.getColors()).isNull();
    }

    @Test
    void shoeVariantsTest() throws Exception {
        OrderItems orderItems = getOrderItemsRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        orderItems.setShoeVariants(shoeVariantsBack);
        assertThat(orderItems.getShoeVariants()).isEqualTo(shoeVariantsBack);

        orderItems.shoeVariants(null);
        assertThat(orderItems.getShoeVariants()).isNull();
    }

    @Test
    void sizesTest() throws Exception {
        OrderItems orderItems = getOrderItemsRandomSampleGenerator();
        Sizes sizesBack = getSizesRandomSampleGenerator();

        orderItems.setSizes(sizesBack);
        assertThat(orderItems.getSizes()).isEqualTo(sizesBack);

        orderItems.sizes(null);
        assertThat(orderItems.getSizes()).isNull();
    }
}
