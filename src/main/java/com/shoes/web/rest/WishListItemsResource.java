package com.shoes.web.rest;

import com.shoes.domain.WishListItems;
import com.shoes.repository.WishListItemsRepository;
import com.shoes.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shoes.domain.WishListItems}.
 */
@RestController
@RequestMapping("/api/wish-list-items")
@Transactional
public class WishListItemsResource {

    private final Logger log = LoggerFactory.getLogger(WishListItemsResource.class);

    private static final String ENTITY_NAME = "wishListItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishListItemsRepository wishListItemsRepository;

    public WishListItemsResource(WishListItemsRepository wishListItemsRepository) {
        this.wishListItemsRepository = wishListItemsRepository;
    }

    /**
     * {@code POST  /wish-list-items} : Create a new wishListItems.
     *
     * @param wishListItems the wishListItems to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishListItems, or with status {@code 400 (Bad Request)} if the wishListItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WishListItems> createWishListItems(@RequestBody WishListItems wishListItems) throws URISyntaxException {
        log.debug("REST request to save WishListItems : {}", wishListItems);
        if (wishListItems.getId() != null) {
            throw new BadRequestAlertException("A new wishListItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        wishListItems = wishListItemsRepository.save(wishListItems);
        return ResponseEntity.created(new URI("/api/wish-list-items/" + wishListItems.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, wishListItems.getId().toString()))
            .body(wishListItems);
    }

    /**
     * {@code PUT  /wish-list-items/:id} : Updates an existing wishListItems.
     *
     * @param id the id of the wishListItems to save.
     * @param wishListItems the wishListItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishListItems,
     * or with status {@code 400 (Bad Request)} if the wishListItems is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishListItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WishListItems> updateWishListItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WishListItems wishListItems
    ) throws URISyntaxException {
        log.debug("REST request to update WishListItems : {}, {}", id, wishListItems);
        if (wishListItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishListItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        wishListItems = wishListItemsRepository.save(wishListItems);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishListItems.getId().toString()))
            .body(wishListItems);
    }

    /**
     * {@code PATCH  /wish-list-items/:id} : Partial updates given fields of an existing wishListItems, field will ignore if it is null
     *
     * @param id the id of the wishListItems to save.
     * @param wishListItems the wishListItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishListItems,
     * or with status {@code 400 (Bad Request)} if the wishListItems is not valid,
     * or with status {@code 404 (Not Found)} if the wishListItems is not found,
     * or with status {@code 500 (Internal Server Error)} if the wishListItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WishListItems> partialUpdateWishListItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WishListItems wishListItems
    ) throws URISyntaxException {
        log.debug("REST request to partial update WishListItems partially : {}, {}", id, wishListItems);
        if (wishListItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishListItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WishListItems> result = wishListItemsRepository.findById(wishListItems.getId()).map(wishListItemsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishListItems.getId().toString())
        );
    }

    /**
     * {@code GET  /wish-list-items} : get all the wishListItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishListItems in body.
     */
    @GetMapping("")
    public List<WishListItems> getAllWishListItems() {
        log.debug("REST request to get all WishListItems");
        return wishListItemsRepository.findAll();
    }

    /**
     * {@code GET  /wish-list-items/:id} : get the "id" wishListItems.
     *
     * @param id the id of the wishListItems to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishListItems, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WishListItems> getWishListItems(@PathVariable("id") Long id) {
        log.debug("REST request to get WishListItems : {}", id);
        Optional<WishListItems> wishListItems = wishListItemsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wishListItems);
    }

    /**
     * {@code DELETE  /wish-list-items/:id} : delete the "id" wishListItems.
     *
     * @param id the id of the wishListItems to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishListItems(@PathVariable("id") Long id) {
        log.debug("REST request to delete WishListItems : {}", id);
        wishListItemsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
