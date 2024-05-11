package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Collection getCollectionSample1() {
        return new Collection().id(1).name("name1");
    }

    public static Collection getCollectionSample2() {
        return new Collection().id(2).name("name2");
    }

    public static Collection getCollectionRandomSampleGenerator() {
        return new Collection().id(intCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
