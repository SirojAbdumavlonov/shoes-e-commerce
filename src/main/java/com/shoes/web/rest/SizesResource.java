package com.shoes.web.rest;

import com.shoes.domain.Sizes;
import com.shoes.repository.SizesRepository;
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
 * REST controller for managing {@link com.shoes.domain.Sizes}.
 */
@RestController
@RequestMapping("/api/sizes")
@Transactional
public class SizesResource {

    private final Logger log = LoggerFactory.getLogger(SizesResource.class);

    private static final String ENTITY_NAME = "sizes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SizesRepository sizesRepository;

    public SizesResource(SizesRepository sizesRepository) {
        this.sizesRepository = sizesRepository;
    }

    /**
     * {@code POST  /sizes} : Create a new sizes.
     *
     * @param sizes the sizes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sizes, or with status {@code 400 (Bad Request)} if the sizes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Sizes> createSizes(@RequestBody Sizes sizes) throws URISyntaxException {
        log.debug("REST request to save Sizes : {}", sizes);
        if (sizes.getId() != null) {
            throw new BadRequestAlertException("A new sizes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sizes = sizesRepository.save(sizes);
        return ResponseEntity.created(new URI("/api/sizes/" + sizes.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sizes.getId().toString()))
            .body(sizes);
    }

    /**
     * {@code PUT  /sizes/:id} : Updates an existing sizes.
     *
     * @param id the id of the sizes to save.
     * @param sizes the sizes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sizes,
     * or with status {@code 400 (Bad Request)} if the sizes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sizes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sizes> updateSizes(@PathVariable(value = "id", required = false) final Integer id, @RequestBody Sizes sizes)
        throws URISyntaxException {
        log.debug("REST request to update Sizes : {}, {}", id, sizes);
        if (sizes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sizes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sizesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sizes = sizesRepository.save(sizes);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sizes.getId().toString()))
            .body(sizes);
    }

    /**
     * {@code PATCH  /sizes/:id} : Partial updates given fields of an existing sizes, field will ignore if it is null
     *
     * @param id the id of the sizes to save.
     * @param sizes the sizes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sizes,
     * or with status {@code 400 (Bad Request)} if the sizes is not valid,
     * or with status {@code 404 (Not Found)} if the sizes is not found,
     * or with status {@code 500 (Internal Server Error)} if the sizes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sizes> partialUpdateSizes(
        @PathVariable(value = "id", required = false) final Integer id,
        @RequestBody Sizes sizes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sizes partially : {}, {}", id, sizes);
        if (sizes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sizes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sizesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sizes> result = sizesRepository
            .findById(sizes.getId())
            .map(existingSizes -> {
                if (sizes.getSizeInNumbers() != null) {
                    existingSizes.setSizeInNumbers(sizes.getSizeInNumbers());
                }
                if (sizes.getSizeInLetters() != null) {
                    existingSizes.setSizeInLetters(sizes.getSizeInLetters());
                }

                return existingSizes;
            })
            .map(sizesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sizes.getId().toString())
        );
    }

    /**
     * {@code GET  /sizes} : get all the sizes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sizes in body.
     */
    @GetMapping("")
    public List<Sizes> getAllSizes() {
        log.debug("REST request to get all Sizes");
        return sizesRepository.findAll();
    }

    /**
     * {@code GET  /sizes/:id} : get the "id" sizes.
     *
     * @param id the id of the sizes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sizes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sizes> getSizes(@PathVariable("id") Integer id) {
        log.debug("REST request to get Sizes : {}", id);
        Optional<Sizes> sizes = sizesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sizes);
    }

    /**
     * {@code DELETE  /sizes/:id} : delete the "id" sizes.
     *
     * @param id the id of the sizes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSizes(@PathVariable("id") Integer id) {
        log.debug("REST request to delete Sizes : {}", id);
        sizesRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
