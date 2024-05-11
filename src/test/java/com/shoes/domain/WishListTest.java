package com.shoes.domain;

import static com.shoes.domain.CustomerTestSamples.*;
import static com.shoes.domain.WishListItemsTestSamples.*;
import static com.shoes.domain.WishListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WishListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WishList.class);
        WishList wishList1 = getWishListSample1();
        WishList wishList2 = new WishList();
        assertThat(wishList1).isNotEqualTo(wishList2);

        wishList2.setId(wishList1.getId());
        assertThat(wishList1).isEqualTo(wishList2);

        wishList2 = getWishListSample2();
        assertThat(wishList1).isNotEqualTo(wishList2);
    }

    @Test
    void customerTest() throws Exception {
        WishList wishList = getWishListRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        wishList.setCustomer(customerBack);
        assertThat(wishList.getCustomer()).isEqualTo(customerBack);

        wishList.customer(null);
        assertThat(wishList.getCustomer()).isNull();
    }

    @Test
    void wishListItemsTest() throws Exception {
        WishList wishList = getWishListRandomSampleGenerator();
        WishListItems wishListItemsBack = getWishListItemsRandomSampleGenerator();

        wishList.addWishListItems(wishListItemsBack);
        assertThat(wishList.getWishListItems()).containsOnly(wishListItemsBack);
        assertThat(wishListItemsBack.getWishList()).isEqualTo(wishList);

        wishList.removeWishListItems(wishListItemsBack);
        assertThat(wishList.getWishListItems()).doesNotContain(wishListItemsBack);
        assertThat(wishListItemsBack.getWishList()).isNull();

        wishList.wishListItems(new HashSet<>(Set.of(wishListItemsBack)));
        assertThat(wishList.getWishListItems()).containsOnly(wishListItemsBack);
        assertThat(wishListItemsBack.getWishList()).isEqualTo(wishList);

        wishList.setWishListItems(new HashSet<>());
        assertThat(wishList.getWishListItems()).doesNotContain(wishListItemsBack);
        assertThat(wishListItemsBack.getWishList()).isNull();
    }
}
