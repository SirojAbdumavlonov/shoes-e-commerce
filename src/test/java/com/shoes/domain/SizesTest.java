package com.shoes.domain;

import static com.shoes.domain.CartItemsTestSamples.*;
import static com.shoes.domain.OrderItemsTestSamples.*;
import static com.shoes.domain.ShoeVariantSizesTestSamples.*;
import static com.shoes.domain.ShoeVariantsTestSamples.*;
import static com.shoes.domain.SizesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SizesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sizes.class);
        Sizes sizes1 = getSizesSample1();
        Sizes sizes2 = new Sizes();
        assertThat(sizes1).isNotEqualTo(sizes2);

        sizes2.setId(sizes1.getId());
        assertThat(sizes1).isEqualTo(sizes2);

        sizes2 = getSizesSample2();
        assertThat(sizes1).isNotEqualTo(sizes2);
    }

    @Test
    void shoeVariantSizesTest() throws Exception {
        Sizes sizes = getSizesRandomSampleGenerator();
        ShoeVariantSizes shoeVariantSizesBack = getShoeVariantSizesRandomSampleGenerator();

        sizes.addShoeVariantSizes(shoeVariantSizesBack);
        assertThat(sizes.getShoeVariantSizes()).containsOnly(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getSizes()).isEqualTo(sizes);

        sizes.removeShoeVariantSizes(shoeVariantSizesBack);
        assertThat(sizes.getShoeVariantSizes()).doesNotContain(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getSizes()).isNull();

        sizes.shoeVariantSizes(new HashSet<>(Set.of(shoeVariantSizesBack)));
        assertThat(sizes.getShoeVariantSizes()).containsOnly(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getSizes()).isEqualTo(sizes);

        sizes.setShoeVariantSizes(new HashSet<>());
        assertThat(sizes.getShoeVariantSizes()).doesNotContain(shoeVariantSizesBack);
        assertThat(shoeVariantSizesBack.getSizes()).isNull();
    }

    @Test
    void cartItemsTest() throws Exception {
        Sizes sizes = getSizesRandomSampleGenerator();
        CartItems cartItemsBack = getCartItemsRandomSampleGenerator();

        sizes.addCartItems(cartItemsBack);
        assertThat(sizes.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getSizes()).isEqualTo(sizes);

        sizes.removeCartItems(cartItemsBack);
        assertThat(sizes.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getSizes()).isNull();

        sizes.cartItems(new HashSet<>(Set.of(cartItemsBack)));
        assertThat(sizes.getCartItems()).containsOnly(cartItemsBack);
        assertThat(cartItemsBack.getSizes()).isEqualTo(sizes);

        sizes.setCartItems(new HashSet<>());
        assertThat(sizes.getCartItems()).doesNotContain(cartItemsBack);
        assertThat(cartItemsBack.getSizes()).isNull();
    }

    @Test
    void orderItemsTest() throws Exception {
        Sizes sizes = getSizesRandomSampleGenerator();
        OrderItems orderItemsBack = getOrderItemsRandomSampleGenerator();

        sizes.addOrderItems(orderItemsBack);
        assertThat(sizes.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getSizes()).isEqualTo(sizes);

        sizes.removeOrderItems(orderItemsBack);
        assertThat(sizes.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getSizes()).isNull();

        sizes.orderItems(new HashSet<>(Set.of(orderItemsBack)));
        assertThat(sizes.getOrderItems()).containsOnly(orderItemsBack);
        assertThat(orderItemsBack.getSizes()).isEqualTo(sizes);

        sizes.setOrderItems(new HashSet<>());
        assertThat(sizes.getOrderItems()).doesNotContain(orderItemsBack);
        assertThat(orderItemsBack.getSizes()).isNull();
    }

    @Test
    void shoeVariantsTest() throws Exception {
        Sizes sizes = getSizesRandomSampleGenerator();
        ShoeVariants shoeVariantsBack = getShoeVariantsRandomSampleGenerator();

        sizes.addShoeVariants(shoeVariantsBack);
        assertThat(sizes.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSizes()).containsOnly(sizes);

        sizes.removeShoeVariants(shoeVariantsBack);
        assertThat(sizes.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSizes()).doesNotContain(sizes);

        sizes.shoeVariants(new HashSet<>(Set.of(shoeVariantsBack)));
        assertThat(sizes.getShoeVariants()).containsOnly(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSizes()).containsOnly(sizes);

        sizes.setShoeVariants(new HashSet<>());
        assertThat(sizes.getShoeVariants()).doesNotContain(shoeVariantsBack);
        assertThat(shoeVariantsBack.getSizes()).doesNotContain(sizes);
    }
}
