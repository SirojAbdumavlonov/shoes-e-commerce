package com.shoes.web.rest;

import com.shoes.domain.ShoeVariantColors;
import com.shoes.repository.ShoeVariantColorsRepository;
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
 * REST controller for managing {@link com.shoes.domain.ShoeVariantColors}.
 */
@RestController
@RequestMapping("/api/shoe-variant-colors")
@Transactional
public class ShoeVariantColorsResource {

    private final Logger log = LoggerFactory.getLogger(ShoeVariantColorsResource.class);

    private static final String ENTITY_NAME = "shoeVariantColors";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoeVariantColorsRepository shoeVariantColorsRepository;

    public ShoeVariantColorsResource(ShoeVariantColorsRepository shoeVariantColorsRepository) {
        this.shoeVariantColorsRepository = shoeVariantColorsRepository;
    }

    /**
     * {@code POST  /shoe-variant-colors} : Create a new shoeVariantColors.
     *
     * @param shoeVariantColors the shoeVariantColors to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoeVariantColors, or with status {@code 400 (Bad Request)} if the shoeVariantColors has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShoeVariantColors> createShoeVariantColors(@RequestBody ShoeVariantColors shoeVariantColors)
        throws URISyntaxException {
        log.debug("REST request to save ShoeVariantColors : {}", shoeVariantColors);
        if (shoeVariantColors.getId() != null) {
            throw new BadRequestAlertException("A new shoeVariantColors cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoeVariantColors = shoeVariantColorsRepository.save(shoeVariantColors);
        return ResponseEntity.created(new URI("/api/shoe-variant-colors/" + shoeVariantColors.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoeVariantColors.getId().toString()))
            .body(shoeVariantColors);
    }

    /**
     * {@code PUT  /shoe-variant-colors/:id} : Updates an existing shoeVariantColors.
     *
     * @param id the id of the shoeVariantColors to save.
     * @param shoeVariantColors the shoeVariantColors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariantColors,
     * or with status {@code 400 (Bad Request)} if the shoeVariantColors is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariantColors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShoeVariantColors> updateShoeVariantColors(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariantColors shoeVariantColors
    ) throws URISyntaxException {
        log.debug("REST request to update ShoeVariantColors : {}, {}", id, shoeVariantColors);
        if (shoeVariantColors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariantColors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantColorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoeVariantColors = shoeVariantColorsRepository.save(shoeVariantColors);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariantColors.getId().toString()))
            .body(shoeVariantColors);
    }

    /**
     * {@code PATCH  /shoe-variant-colors/:id} : Partial updates given fields of an existing shoeVariantColors, field will ignore if it is null
     *
     * @param id the id of the shoeVariantColors to save.
     * @param shoeVariantColors the shoeVariantColors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoeVariantColors,
     * or with status {@code 400 (Bad Request)} if the shoeVariantColors is not valid,
     * or with status {@code 404 (Not Found)} if the shoeVariantColors is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoeVariantColors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoeVariantColors> partialUpdateShoeVariantColors(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShoeVariantColors shoeVariantColors
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoeVariantColors partially : {}, {}", id, shoeVariantColors);
        if (shoeVariantColors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoeVariantColors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoeVariantColorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoeVariantColors> result = shoeVariantColorsRepository
            .findById(shoeVariantColors.getId())
            .map(existingShoeVariantColors -> {
                if (shoeVariantColors.getPrice() != null) {
                    existingShoeVariantColors.setPrice(shoeVariantColors.getPrice());
                }
                if (shoeVariantColors.getImageUrl() != null) {
                    existingShoeVariantColors.setImageUrl(shoeVariantColors.getImageUrl());
                }

                return existingShoeVariantColors;
            })
            .map(shoeVariantColorsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoeVariantColors.getId().toString())
        );
    }

    /**
     * {@code GET  /shoe-variant-colors} : get all the shoeVariantColors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoeVariantColors in body.
     */
    @GetMapping("")
    public List<ShoeVariantColors> getAllShoeVariantColors() {
        log.debug("REST request to get all ShoeVariantColors");
        return shoeVariantColorsRepository.findAll();
    }

    /**
     * {@code GET  /shoe-variant-colors/:id} : get the "id" shoeVariantColors.
     *
     * @param id the id of the shoeVariantColors to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoeVariantColors, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoeVariantColors> getShoeVariantColors(@PathVariable("id") Long id) {
        log.debug("REST request to get ShoeVariantColors : {}", id);
        Optional<ShoeVariantColors> shoeVariantColors = shoeVariantColorsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoeVariantColors);
    }

    /**
     * {@code DELETE  /shoe-variant-colors/:id} : delete the "id" shoeVariantColors.
     *
     * @param id the id of the shoeVariantColors to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoeVariantColors(@PathVariable("id") Long id) {
        log.debug("REST request to delete ShoeVariantColors : {}", id);
        shoeVariantColorsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
