package com.shoes.web.rest;

import com.shoes.domain.ShoeVariantSizes;
import com.shoes.repository.ShoeVariantSizesRepository;
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
 * REST controller for managing {@link com.shoes.domain.ShoeVariantSizes}.
 */
@RestController
@RequestMapping("/api/shoe-variant-sizes")
@Transactional
public class ShoeVariantSizesResource {

    private final Logger log = LoggerFactory.getLogger(ShoeVariantSizesResource.class);

    private static final String ENTITY_NAME = "shoeVariantSizes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoeVariantSizesRepository shoeVariantSizesRepository;

    public ShoeVariantSizesResource(ShoeVariantSizesRepository shoeVariantSizesRepository) {
        this.shoeVariantSizesRepository = shoeVariantSizesRepository;
    }

    /**
     * {@code POST  /shoe-variant-sizes} : Create a new shoeVariantSizes.
     *
     * @param shoeVariantSizes the shoeVariantSizes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoeVariantSizes, or with status {@code 400 (Bad Request)} if the shoeVariantSizes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShoeVariantSizes> createShoeVariantSizes(@RequestBody ShoeVariantSizes shoeVariantSizes)
        throws URISyntaxException {
        log.debug("REST request to save ShoeVariantSizes : {}", shoeVariantSizes);
        if (shoeVariantSizes.getId() != null) {
            throw new BadRequestAlertException("A new shoeVariantSizes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoeVariantSizes = shoeVariantSizesRepository.save(shoeVariantSizes);
        return ResponseEntity.created(new URI("/api/shoe-variant-sizes/" + shoeVariantSizes.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoeVariantSizes.getId().toString()))
            .body(shoeVariantSizes);
    }

    /**
     * {@code PUT  /shoe-variant-sizes/:id} : Updates an existing shoeVariantSizes.
     *
     * @param id the id of the shoeVariantSizes to save.
     * @param shoeVariantSizes the shoeVariantSizes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariantSizes,
     * or with status {@code 400 (Bad Request)} if the shoeVariantSizes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariantSizes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShoeVariantSizes> updateShoeVariantSizes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariantSizes shoeVariantSizes
    ) throws URISyntaxException {
        log.debug("REST request to update ShoeVariantSizes : {}, {}", id, shoeVariantSizes);
        if (shoeVariantSizes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariantSizes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantSizesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoeVariantSizes = shoeVariantSizesRepository.save(shoeVariantSizes);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariantSizes.getId().toString()))
            .body(shoeVariantSizes);
    }

    /**
     * {@code PATCH  /shoe-variant-sizes/:id} : Partial updates given fields of an existing shoeVariantSizes, field will ignore if it is null
     *
     * @param id the id of the shoeVariantSizes to save.
     * @param shoeVariantSizes the shoeVariantSizes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariantSizes,
     * or with status {@code 400 (Bad Request)} if the shoeVariantSizes is not valid,
     * or with status {@code 404 (Not Found)} if the shoeVariantSizes is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariantSizes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoeVariantSizes> partialUpdateShoeVariantSizes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariantSizes shoeVariantSizes
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoeVariantSizes partially : {}, {}", id, shoeVariantSizes);
        if (shoeVariantSizes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariantSizes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantSizesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoeVariantSizes> result = shoeVariantSizesRepository
            .findById(shoeVariantSizes.getId())
            .map(existingShoeVariantSizes -> {
                if (shoeVariantSizes.getQuantity() != null) {
                    existingShoeVariantSizes.setQuantity(shoeVariantSizes.getQuantity());
                }
                if (shoeVariantSizes.getStatus() != null) {
                    existingShoeVariantSizes.setStatus(shoeVariantSizes.getStatus());
                }

                return existingShoeVariantSizes;
            })
            .map(shoeVariantSizesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariantSizes.getId().toString())
        );
    }

    /**
     * {@code GET  /shoe-variant-sizes} : get all the shoeVariantSizes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoeVariantSizes in body.
     */
    @GetMapping("")
    public List<ShoeVariantSizes> getAllShoeVariantSizes() {
        log.debug("REST request to get all ShoeVariantSizes");
        return shoeVariantSizesRepository.findAll();
    }

    /**
     * {@code GET  /shoe-variant-sizes/:id} : get the "id" shoeVariantSizes.
     *
     * @param id the id of the shoeVariantSizes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoeVariantSizes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoeVariantSizes> getShoeVariantSizes(@PathVariable("id") Long id) {
        log.debug("REST request to get ShoeVariantSizes : {}", id);
        Optional<ShoeVariantSizes> shoeVariantSizes = shoeVariantSizesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoeVariantSizes);
    }

    /**
     * {@code DELETE  /shoe-variant-sizes/:id} : delete the "id" shoeVariantSizes.
     *
     * @param id the id of the shoeVariantSizes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoeVariantSizes(@PathVariable("id") Long id) {
        log.debug("REST request to delete ShoeVariantSizes : {}", id);
        shoeVariantSizesRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
