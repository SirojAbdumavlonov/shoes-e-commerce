package com.shoes.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class WishListItemsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static WishListItems getWishListItemsSample1() {
        return new WishListItems().id(1L);
    }

    public static WishListItems getWishListItemsSample2() {
        return new WishListItems().id(2L);
    }

    public static WishListItems getWishListItemsRandomSampleGenerator() {
        return new WishListItems().id(longCount.incrementAndGet());
    }
}
