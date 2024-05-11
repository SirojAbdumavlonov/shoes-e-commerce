package com.shoes.repository;

import com.shoes.domain.ShoePurpose;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShoePurpose entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoePurposeRepository extends JpaRepository<ShoePurpose, Integer> {}
