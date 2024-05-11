package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CartTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Cart getCartSample1() {
        return new Cart().id(1);
    }

    public static Cart getCartSample2() {
        return new Cart().id(2);
    }

    public static Cart getCartRandomSampleGenerator() {
        return new Cart().id(intCount.incrementAndGet());
    }
}
