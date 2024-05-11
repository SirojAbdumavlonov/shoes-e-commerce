package com.shoes.repository;

import com.shoes.domain.ShoeVariants;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ShoeVariantsRepositoryWithBagRelationships {
    Optional<ShoeVariants> fetchBagRelationships(Optional<ShoeVariants> shoeVariants);

    List<ShoeVariants> fetchBagRelationships(List<ShoeVariants> shoeVariants);

    Page<ShoeVariants> fetchBagRelationships(Page<ShoeVariants> shoeVariants);
}
