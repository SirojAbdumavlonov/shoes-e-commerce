package com.shoes.domain;

import static com.shoes.domain.ShoePurposeTestSamples.*;
import static com.shoes.domain.ShoesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shoes.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShoePurposeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoePurpose.class);
        ShoePurpose shoePurpose1 = getShoePurposeSample1();
        ShoePurpose shoePurpose2 = new ShoePurpose();
        assertThat(shoePurpose1).isNotEqualTo(shoePurpose2);

        shoePurpose2.setId(shoePurpose1.getId());
        assertThat(shoePurpose1).isEqualTo(shoePurpose2);

        shoePurpose2 = getShoePurposeSample2();
        assertThat(shoePurpose1).isNotEqualTo(shoePurpose2);
    }

    @Test
    void shoesTest() throws Exception {
        ShoePurpose shoePurpose = getShoePurposeRandomSampleGenerator();
        Shoes shoesBack = getShoesRandomSampleGenerator();

        shoePurpose.addShoes(shoesBack);
        assertThat(shoePurpose.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getShoePurpose()).isEqualTo(shoePurpose);

        shoePurpose.removeShoes(shoesBack);
        assertThat(shoePurpose.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getShoePurpose()).isNull();

        shoePurpose.shoes(new HashSet<>(Set.of(shoesBack)));
        assertThat(shoePurpose.getShoes()).containsOnly(shoesBack);
        assertThat(shoesBack.getShoePurpose()).isEqualTo(shoePurpose);

        shoePurpose.setShoes(new HashSet<>());
        assertThat(shoePurpose.getShoes()).doesNotContain(shoesBack);
        assertThat(shoesBack.getShoePurpose()).isNull();
    }
}
