package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BrandTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Brand getBrandSample1() {
        return new Brand().id(1).name("name1");
    }

    public static Brand getBrandSample2() {
        return new Brand().id(2).name("name2");
    }

    public static Brand getBrandRandomSampleGenerator() {
        return new Brand().id(intCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
