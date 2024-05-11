package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OrderItemsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OrderItems getOrderItemsSample1() {
        return new OrderItems().id(1L).quantity(1).price(1L);
    }

    public static OrderItems getOrderItemsSample2() {
        return new OrderItems().id(2L).quantity(2).price(2L);
    }

    public static OrderItems getOrderItemsRandomSampleGenerator() {
        return new OrderItems().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet()).price(longCount.incrementAndGet());
    }
}
