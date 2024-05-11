package com.shoes.repository;

import com.shoes.domain.Sizes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sizes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SizesRepository extends JpaRepository<Sizes, Integer> {}
