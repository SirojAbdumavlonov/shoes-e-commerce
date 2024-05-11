package com.shoes.repository;

import com.shoes.domain.ShoeVariantColors;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShoeVariantColors entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoeVariantColorsRepository extends JpaRepository<ShoeVariantColors, Long> {}
