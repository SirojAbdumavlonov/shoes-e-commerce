package com.shoes.web.rest;

import com.shoes.domain.Shoes;
import com.shoes.repository.ShoesRepository;
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
 * REST controller for managing {@link com.shoes.domain.Shoes}.
 */
@RestController
@RequestMapping("/api/shoes")
@Transactional
public class ShoesResource {

    private final Logger log = LoggerFactory.getLogger(ShoesResource.class);

    private static final String ENTITY_NAME = "shoes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoesRepository shoesRepository;

    public ShoesResource(ShoesRepository shoesRepository) {
        this.shoesRepository = shoesRepository;
    }

    /**
     * {@code POST  /shoes} : Create a new shoes.
     *
     * @param shoes the shoes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoes, or with status {@code 400 (Bad Request)} if the shoes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Shoes> createShoes(@RequestBody Shoes shoes) throws URISyntaxException {
        log.debug("REST request to save Shoes : {}", shoes);
        if (shoes.getId() != null) {
            throw new BadRequestAlertException("A new shoes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoes = shoesRepository.save(shoes);
        return ResponseEntity.created(new URI("/api/shoes/" + shoes.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoes.getId().toString()))
            .body(shoes);
    }

    /**
     * {@code PUT  /shoes/:id} : Updates an existing shoes.
     *
     * @param id the id of the shoes to save.
     * @param shoes the shoes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoes,
     * or with status {@code 400 (Bad Request)} if the shoes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Shoes> updateShoes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Shoes shoes)
        throws URISyntaxException {
        log.debug("REST request to update Shoes : {}, {}", id, shoes);
        if (shoes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoes = shoesRepository.save(shoes);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoes.getId().toString()))
            .body(shoes);
    }

    /**
     * {@code PATCH  /shoes/:id} : Partial updates given fields of an existing shoes, field will ignore if it is null
     *
     * @param id the id of the shoes to save.
     * @param shoes the shoes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoes,
     * or with status {@code 400 (Bad Request)} if the shoes is not valid,
     * or with status {@code 404 (Not Found)} if the shoes is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Shoes> partialUpdateShoes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Shoes shoes)
        throws URISyntaxException {
        log.debug("REST request to partial update Shoes partially : {}, {}", id, shoes);
        if (shoes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Shoes> result = shoesRepository
            .findById(shoes.getId())
            .map(existingShoes -> {
                if (shoes.getDescription() != null) {
                    existingShoes.setDescription(shoes.getDescription());
                }

                return existingShoes;
            })
            .map(shoesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoes.getId().toString())
        );
    }

    /**
     * {@code GET  /shoes} : get all the shoes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoes in body.
     */
    @GetMapping("")
    public List<Shoes> getAllShoes() {
        log.debug("REST request to get all Shoes");
        return shoesRepository.findAll();
    }

    /**
     * {@code GET  /shoes/:id} : get the "id" shoes.
     *
     * @param id the id of the shoes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shoes> getShoes(@PathVariable("id") Long id) {
        log.debug("REST request to get Shoes : {}", id);
        Optional<Shoes> shoes = shoesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoes);
    }

    /**
     * {@code DELETE  /shoes/:id} : delete the "id" shoes.
     *
     * @param id the id of the shoes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoes(@PathVariable("id") Long id) {
        log.debug("REST request to delete Shoes : {}", id);
        shoesRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
