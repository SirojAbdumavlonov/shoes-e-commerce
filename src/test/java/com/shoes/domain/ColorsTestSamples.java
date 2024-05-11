package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorsTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Colors getColorsSample1() {
        return new Colors().id(1).colorName("colorName1");
    }

    public static Colors getColorsSample2() {
        return new Colors().id(2).colorName("colorName2");
    }

    public static Colors getColorsRandomSampleGenerator() {
        return new Colors().id(intCount.incrementAndGet()).colorName(UUID.randomUUID().toString());
    }
}
