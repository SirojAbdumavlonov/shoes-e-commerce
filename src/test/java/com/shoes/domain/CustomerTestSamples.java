package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id(1L).email("email1").password("password1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(2L).email("email2").password("password2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer().id(longCount.incrementAndGet()).email(UUID.randomUUID().toString()).password(UUID.randomUUID().toString());
    }
}
