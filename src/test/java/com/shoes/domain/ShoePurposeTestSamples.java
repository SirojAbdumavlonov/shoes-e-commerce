package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ShoePurposeTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ShoePurpose getShoePurposeSample1() {
        return new ShoePurpose().id(1);
    }

    public static ShoePurpose getShoePurposeSample2() {
        return new ShoePurpose().id(2);
    }

    public static ShoePurpose getShoePurposeRandomSampleGenerator() {
        return new ShoePurpose().id(intCount.incrementAndGet());
    }
}
