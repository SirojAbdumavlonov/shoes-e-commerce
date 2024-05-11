package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SizesTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Sizes getSizesSample1() {
        return new Sizes().id(1).sizeInLetters("sizeInLetters1");
    }

    public static Sizes getSizesSample2() {
        return new Sizes().id(2).sizeInLetters("sizeInLetters2");
    }

    public static Sizes getSizesRandomSampleGenerator() {
        return new Sizes().id(intCount.incrementAndGet()).sizeInLetters(UUID.randomUUID().toString());
    }
}
