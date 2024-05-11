package com.shoes.web.rest;

import com.shoes.domain.ShoeVariants;
import com.shoes.repository.ShoeVariantsRepository;
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
 * REST controller for managing {@link com.shoes.domain.ShoeVariants}.
 */
@RestController
@RequestMapping("/api/shoe-variants")
@Transactional
public class ShoeVariantsResource {

    private final Logger log = LoggerFactory.getLogger(ShoeVariantsResource.class);

    private static final String ENTITY_NAME = "shoeVariants";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoeVariantsRepository shoeVariantsRepository;

    public ShoeVariantsResource(ShoeVariantsRepository shoeVariantsRepository) {
        this.shoeVariantsRepository = shoeVariantsRepository;
    }

    /**
     * {@code POST  /shoe-variants} : Create a new shoeVariants.
     *
     * @param shoeVariants the shoeVariants to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoeVariants, or with status {@code 400 (Bad Request)} if the shoeVariants has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShoeVariants> createShoeVariants(@RequestBody ShoeVariants shoeVariants) throws URISyntaxException {
        log.debug("REST request to save ShoeVariants : {}", shoeVariants);
        if (shoeVariants.getId() != null) {
            throw new BadRequestAlertException("A new shoeVariants cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoeVariants = shoeVariantsRepository.save(shoeVariants);
        return ResponseEntity.created(new URI("/api/shoe-variants/" + shoeVariants.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoeVariants.getId().toString()))
            .body(shoeVariants);
    }

    /**
     * {@code PUT  /shoe-variants/:id} : Updates an existing shoeVariants.
     *
     * @param id the id of the shoeVariants to save.
     * @param shoeVariants the shoeVariants to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariants,
     * or with status {@code 400 (Bad Request)} if the shoeVariants is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariants couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShoeVariants> updateShoeVariants(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariants shoeVariants
    ) throws URISyntaxException {
        log.debug("REST request to update ShoeVariants : {}, {}", id, shoeVariants);
        if (shoeVariants.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariants.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoeVariants = shoeVariantsRepository.save(shoeVariants);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariants.getId().toString()))
            .body(shoeVariants);
    }

    /**
     * {@code PATCH  /shoe-variants/:id} : Partial updates given fields of an existing shoeVariants, field will ignore if it is null
     *
     * @param id the id of the shoeVariants to save.
     * @param shoeVariants the shoeVariants to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariants,
     * or with status {@code 400 (Bad Request)} if the shoeVariants is not valid,
     * or with status {@code 404 (Not Found)} if the shoeVariants is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariants couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoeVariants> partialUpdateShoeVariants(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariants shoeVariants
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoeVariants partially : {}, {}", id, shoeVariants);
        if (shoeVariants.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariants.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoeVariants> result = shoeVariantsRepository
            .findById(shoeVariants.getId())
            .map(existingShoeVariants -> {
                if (shoeVariants.getQuantity() != null) {
                    existingShoeVariants.setQuantity(shoeVariants.getQuantity());
                }
                if (shoeVariants.getStatus() != null) {
                    existingShoeVariants.setStatus(shoeVariants.getStatus());
                }
                if (shoeVariants.getPhotoUrl() != null) {
                    existingShoeVariants.setPhotoUrl(shoeVariants.getPhotoUrl());
                }

                return existingShoeVariants;
            })
            .map(shoeVariantsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariants.getId().toString())
        );
    }

    /**
     * {@code GET  /shoe-variants} : get all the shoeVariants.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoeVariants in body.
     */
    @GetMapping("")
    public List<ShoeVariants> getAllShoeVariants(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all ShoeVariants");
        if (eagerload) {
            return shoeVariantsRepository.findAllWithEagerRelationships();
        } else {
            return shoeVariantsRepository.findAll();
        }
    }

    /**
     * {@code GET  /shoe-variants/:id} : get the "id" shoeVariants.
     *
     * @param id the id of the shoeVariants to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoeVariants, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoeVariants> getShoeVariants(@PathVariable("id") Long id) {
        log.debug("REST request to get ShoeVariants : {}", id);
        Optional<ShoeVariants> shoeVariants = shoeVariantsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(shoeVariants);
    }

    /**
     * {@code DELETE  /shoe-variants/:id} : delete the "id" shoeVariants.
     *
     * @param id the id of the shoeVariants to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoeVariants(@PathVariable("id") Long id) {
        log.debug("REST request to delete ShoeVariants : {}", id);
        shoeVariantsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
