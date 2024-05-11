package com.shoes.domain;

import static com.shoes.domain.WishListItemsTestSamples.*;
import static com.shoes.domain.WishListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WishListItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WishListItems.class);
        WishListItems wishListItems1 = getWishListItemsSample1();
        WishListItems wishListItems2 = new WishListItems();
        assertThat(wishListItems1).isNotEqualTo(wishListItems2);

        wishListItems2.setId(wishListItems1.getId());
        assertThat(wishListItems1).isEqualTo(wishListItems2);

        wishListItems2 = getWishListItemsSample2();
        assertThat(wishListItems1).isNotEqualTo(wishListItems2);
    }

    @Test
    void wishListTest() throws Exception {
        WishListItems wishListItems = getWishListItemsRandomSampleGenerator();
        WishList wishListBack = getWishListRandomSampleGenerator();

        wishListItems.setWishList(wishListBack);
        assertThat(wishListItems.getWishList()).isEqualTo(wishListBack);

        wishListItems.wishList(null);
        assertThat(wishListItems.getWishList()).isNull();
    }
}
