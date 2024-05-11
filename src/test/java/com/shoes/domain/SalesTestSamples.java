package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SalesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Sales getSalesSample1() {
        return new Sales().id(1L).discountPercentage(1).newPrice(1);
    }

    public static Sales getSalesSample2() {
        return new Sales().id(2L).discountPercentage(2).newPrice(2);
    }

    public static Sales getSalesRandomSampleGenerator() {
        return new Sales()
            .id(longCount.incrementAndGet())
            .discountPercentage(intCount.incrementAndGet())
            .newPrice(intCount.incrementAndGet());
    }
}
