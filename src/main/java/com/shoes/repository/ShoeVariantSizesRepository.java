package com.shoes.repository;

import com.shoes.domain.ShoeVariantSizes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShoeVariantSizes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoeVariantSizesRepository extends JpaRepository<ShoeVariantSizes, Long> {}
