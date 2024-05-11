package com.shoes.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerDetailsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CustomerDetails getCustomerDetailsSample1() {
        return new CustomerDetails().id(1L).firstName("firstName1").secondName("secondName1").gender("gender1");
    }

    public static CustomerDetails getCustomerDetailsSample2() {
        return new CustomerDetails().id(2L).firstName("firstName2").secondName("secondName2").gender("gender2");
    }

    public static CustomerDetails getCustomerDetailsRandomSampleGenerator() {
        return new CustomerDetails()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .secondName(UUID.randomUUID().toString())
            .gender(UUID.randomUUID().toString());
    }
}
