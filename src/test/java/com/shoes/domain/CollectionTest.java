package com.shoes.domain;

import static com.shoes.domain.CollectionTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collection.class);
        Collection collection1 = getCollectionSample1();
        Collection collection2 = new Collection();
        assertThat(collection1).isNotEqualTo(collection2);

        collection2.setId(collection1.getId());
        assertThat(collection1).isEqualTo(collection2);

        collection2 = getCollectionSample2();
        assertThat(collection1).isNotEqualTo(collection2);
    }

    @Test
    void shoesTest() throws Exception {
        Collection collection = getCollectionRandomSampleGenerator();
        Shoes shoesBack = getShoesRandomSampleGenerator();

        collection.addShoes(shoesBack);
        assertThat(collection.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getCollection()).isEqualTo(collection);

        collection.removeShoes(shoesBack);
        assertThat(collection.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getCollection()).isNull();

        collection.shoes(new HashSet<>(Set.of(shoesBack)));
        assertThat(collection.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getCollection()).isEqualTo(collection);

        collection.setShoes(new HashSet<>());
        assertThat(collection.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getCollection()).isNull();
    }
}
