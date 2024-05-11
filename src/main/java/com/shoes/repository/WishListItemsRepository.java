package com.shoes.repository;

import com.shoes.domain.WishListItems;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WishListItems entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WishListItemsRepository extends JpaRepository<WishListItems, Long> {}
