package com.shoes.domain;

import static com.shoes.domain.CustomerDetailsTestSamples.*;
import static com.shoes.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerDetails.class);
        CustomerDetails customerDetails1 = getCustomerDetailsSample1();
        CustomerDetails customerDetails2 = new CustomerDetails();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);

        customerDetails2.setId(customerDetails1.getId());
        assertThat(customerDetails1).isEqualTo(customerDetails2);

        customerDetails2 = getCustomerDetailsSample2();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);
    }

    @Test
    void customerTest() throws Exception {
        CustomerDetails customerDetails = getCustomerDetailsRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        customerDetails.setCustomer(customerBack);
        assertThat(customerDetails.getCustomer()).isEqualTo(customerBack);

        customerDetails.customer(null);
        assertThat(customerDetails.getCustomer()).isNull();
    }
}
