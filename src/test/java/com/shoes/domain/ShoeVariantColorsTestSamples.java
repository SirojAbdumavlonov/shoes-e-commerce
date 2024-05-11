package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShoeVariantColorsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ShoeVariantColors getShoeVariantColorsSample1() {
        return new ShoeVariantColors().id(1L).imageUrl("imageUrl1");
    }

    public static ShoeVariantColors getShoeVariantColorsSample2() {
        return new ShoeVariantColors().id(2L).imageUrl("imageUrl2");
    }

    public static ShoeVariantColors getShoeVariantColorsRandomSampleGenerator() {
        return new ShoeVariantColors().id(longCount.incrementAndGet()).imageUrl(UUID.randomUUID().toString());
    }
}
