package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ShoeVariantsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ShoeVariants getShoeVariantsSample1() {
        return new ShoeVariants().id(1L).quantity(1).photoUrl("photoUrl1");
    }

    public static ShoeVariants getShoeVariantsSample2() {
        return new ShoeVariants().id(2L).quantity(2).photoUrl("photoUrl2");
    }

    public static ShoeVariants getShoeVariantsRandomSampleGenerator() {
        return new ShoeVariants()
            .id(longCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .photoUrl(UUID.randomUUID().toString());
    }
}
