package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CartItemsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CartItems getCartItemsSample1() {
        return new CartItems().id(1L).quantity(1);
    }

    public static CartItems getCartItemsSample2() {
        return new CartItems().id(2L).quantity(2);
    }

    public static CartItems getCartItemsRandomSampleGenerator() {
        return new CartItems().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
