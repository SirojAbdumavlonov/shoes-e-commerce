package com.shoes.web.rest;

import com.shoes.domain.ShoePurpose;
import com.shoes.repository.ShoePurposeRepository;
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
 * REST controller for managing {@link com.shoes.domain.ShoePurpose}.
 */
@RestController
@RequestMapping("/api/shoe-purposes")
@Transactional
public class ShoePurposeResource {

    private final Logger log = LoggerFactory.getLogger(ShoePurposeResource.class);

    private static final String ENTITY_NAME = "shoePurpose";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoePurposeRepository shoePurposeRepository;

    public ShoePurposeResource(ShoePurposeRepository shoePurposeRepository) {
        this.shoePurposeRepository = shoePurposeRepository;
    }

    /**
     * {@code POST  /shoe-purposes} : Create a new shoePurpose.
     *
     * @param shoePurpose the shoePurpose to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoePurpose, or with status {@code 400 (Bad Request)} if the shoePurpose has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShoePurpose> createShoePurpose(@RequestBody ShoePurpose shoePurpose) throws URISyntaxException {
        log.debug("REST request to save ShoePurpose : {}", shoePurpose);
        if (shoePurpose.getId() != null) {
            throw new BadRequestAlertException("A new shoePurpose cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoePurpose = shoePurposeRepository.save(shoePurpose);
        return ResponseEntity.created(new URI("/api/shoe-purposes/" + shoePurpose.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoePurpose.getId().toString()))
            .body(shoePurpose);
    }

    /**
     * {@code PUT  /shoe-purposes/:id} : Updates an existing shoePurpose.
     *
     * @param id the id of the shoePurpose to save.
     * @param shoePurpose the shoePurpose to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoePurpose,
     * or with status {@code 400 (Bad Request)} if the shoePurpose is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoePurpose couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShoePurpose> updateShoePurpose(
        @PathVariable(value = "id", required = false) final Integer id,
        @RequestBody ShoePurpose shoePurpose
    ) throws URISyntaxException {
        log.debug("REST request to update ShoePurpose : {}, {}", id, shoePurpose);
        if (shoePurpose.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoePurpose.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoePurposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoePurpose = shoePurposeRepository.save(shoePurpose);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoePurpose.getId().toString()))
            .body(shoePurpose);
    }

    /**
     * {@code PATCH  /shoe-purposes/:id} : Partial updates given fields of an existing shoePurpose, field will ignore if it is null
     *
     * @param id the id of the shoePurpose to save.
     * @param shoePurpose the shoePurpose to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoePurpose,
     * or with status {@code 400 (Bad Request)} if the shoePurpose is not valid,
     * or with status {@code 404 (Not Found)} if the shoePurpose is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoePurpose couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoePurpose> partialUpdateShoePurpose(
        @PathVariable(value = "id", required = false) final Integer id,
        @RequestBody ShoePurpose shoePurpose
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoePurpose partially : {}, {}", id, shoePurpose);
        if (shoePurpose.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoePurpose.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoePurposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoePurpose> result = shoePurposeRepository
            .findById(shoePurpose.getId())
            .map(existingShoePurpose -> {
                if (shoePurpose.getType() != null) {
                    existingShoePurpose.setType(shoePurpose.getType());
                }

                return existingShoePurpose;
            })
            .map(shoePurposeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoePurpose.getId().toString())
        );
    }

    /**
     * {@code GET  /shoe-purposes} : get all the shoePurposes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoePurposes in body.
     */
    @GetMapping("")
    public List<ShoePurpose> getAllShoePurposes() {
        log.debug("REST request to get all ShoePurposes");
        return shoePurposeRepository.findAll();
    }

    /**
     * {@code GET  /shoe-purposes/:id} : get the "id" shoePurpose.
     *
     * @param id the id of the shoePurpose to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoePurpose, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoePurpose> getShoePurpose(@PathVariable("id") Integer id) {
        log.debug("REST request to get ShoePurpose : {}", id);
        Optional<ShoePurpose> shoePurpose = shoePurposeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoePurpose);
    }

    /**
     * {@code DELETE  /shoe-purposes/:id} : delete the "id" shoePurpose.
     *
     * @param id the id of the shoePurpose to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoePurpose(@PathVariable("id") Integer id) {
        log.debug("REST request to delete ShoePurpose : {}", id);
        shoePurposeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
