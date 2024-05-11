package com.shoes.repository;

import com.shoes.domain.ShoeVariants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ShoeVariantsRepositoryWithBagRelationshipsImpl implements ShoeVariantsRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String SHOEVARIANTS_PARAMETER = "shoeVariants";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ShoeVariants> fetchBagRelationships(Optional<ShoeVariants> shoeVariants) {
        return shoeVariants.map(this::fetchSizes);
    }

    @Override
    public Page<ShoeVariants> fetchBagRelationships(Page<ShoeVariants> shoeVariants) {
        return new PageImpl<>(
            fetchBagRelationships(shoeVariants.getContent()),
            shoeVariants.getPageable(),
            shoeVariants.getTotalElements()
        );
    }

    @Override
    public List<ShoeVariants> fetchBagRelationships(List<ShoeVariants> shoeVariants) {
        return Optional.of(shoeVariants).map(this::fetchSizes).orElse(Collections.emptyList());
    }

    ShoeVariants fetchSizes(ShoeVariants result) {
        return entityManager
            .createQuery(
                "select shoeVariants from ShoeVariants shoeVariants left join fetch shoeVariants.sizes where shoeVariants.id = :id",
                ShoeVariants.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ShoeVariants> fetchSizes(List<ShoeVariants> shoeVariants) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, shoeVariants.size()).forEach(index -> order.put(shoeVariants.get(index).getId(), index));
        List<ShoeVariants> result = entityManager
            .createQuery(
                "select shoeVariants from ShoeVariants shoeVariants left join fetch shoeVariants.sizes where shoeVariants in :shoeVariants",
                ShoeVariants.class
            )
            .setParameter(SHOEVARIANTS_PARAMETER, shoeVariants)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
