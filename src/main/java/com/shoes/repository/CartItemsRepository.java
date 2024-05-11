package com.shoes.repository;

import com.shoes.domain.CartItems;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CartItems entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {}
