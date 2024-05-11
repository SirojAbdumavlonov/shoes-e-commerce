package com.shoes.repository;

import com.shoes.domain.Shoes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Shoes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoesRepository extends JpaRepository<Shoes, Long> {}
