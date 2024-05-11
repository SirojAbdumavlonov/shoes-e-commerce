package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShoesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Shoes getShoesSample1() {
        return new Shoes().id(1L).description("description1");
    }

    public static Shoes getShoesSample2() {
        return new Shoes().id(2L).description("description2");
    }

    public static Shoes getShoesRandomSampleGenerator() {
        return new Shoes().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
