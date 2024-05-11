package com.shoes.web.rest;

import com.shoes.domain.Colors;
import com.shoes.repository.ColorsRepository;
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
 * REST controller for managing {@link com.shoes.domain.Colors}.
 */
@RestController
@RequestMapping("/api/colors")
@Transactional
public class ColorsResource {

    private final Logger log = LoggerFactory.getLogger(ColorsResource.class);

    private static final String ENTITY_NAME = "colors";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ColorsRepository colorsRepository;

    public ColorsResource(ColorsRepository colorsRepository) {
        this.colorsRepository = colorsRepository;
    }

    /**
     * {@code POST  /colors} : Create a new colors.
     *
     * @param colors the colors to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new colors, or with status {@code 400 (Bad Request)} if the colors has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Colors> createColors(@RequestBody Colors colors) throws URISyntaxException {
        log.debug("REST request to save Colors : {}", colors);
        if (colors.getId() != null) {
            throw new BadRequestAlertException("A new colors cannot already have an ID", ENTITY_NAME, "idexists");
        }
        colors = colorsRepository.save(colors);
        return ResponseEntity.created(new URI("/api/colors/" + colors.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, colors.getId().toString()))
            .body(colors);
    }

    /**
     * {@code PUT  /colors/:id} : Updates an existing colors.
     *
     * @param id the id of the colors to save.
     * @param colors the colors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colors,
     * or with status {@code 400 (Bad Request)} if the colors is not valid,
     * or with status {@code 500 (Internal Server Error)} if the colors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Colors> updateColors(@PathVariable(value = "id", required = false) final Integer id, @RequestBody Colors colors)
        throws URISyntaxException {
        log.debug("REST request to update Colors : {}, {}", id, colors);
        if (colors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        colors = colorsRepository.save(colors);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, colors.getId().toString()))
            .body(colors);
    }

    /**
     * {@code PATCH  /colors/:id} : Partial updates given fields of an existing colors, field will ignore if it is null
     *
     * @param id the id of the colors to save.
     * @param colors the colors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colors,
     * or with status {@code 400 (Bad Request)} if the colors is not valid,
     * or with status {@code 404 (Not Found)} if the colors is not found,
     * or with status {@code 500 (Internal Server Error)} if the colors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Colors> partialUpdateColors(
        @PathVariable(value = "id", required = false) final Integer id,
        @RequestBody Colors colors
    ) throws URISyntaxException {
        log.debug("REST request to partial update Colors partially : {}, {}", id, colors);
        if (colors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Colors> result = colorsRepository
            .findById(colors.getId())
            .map(existingColors -> {
                if (colors.getColorName() != null) {
                    existingColors.setColorName(colors.getColorName());
                }

                return existingColors;
            })
            .map(colorsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, colors.getId().toString())
        );
    }

    /**
     * {@code GET  /colors} : get all the colors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of colors in body.
     */
    @GetMapping("")
    public List<Colors> getAllColors() {
        log.debug("REST request to get all Colors");
        return colorsRepository.findAll();
    }

    /**
     * {@code GET  /colors/:id} : get the "id" colors.
     *
     * @param id the id of the colors to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the colors, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Colors> getColors(@PathVariable("id") Integer id) {
        log.debug("REST request to get Colors : {}", id);
        Optional<Colors> colors = colorsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(colors);
    }

    /**
     * {@code DELETE  /colors/:id} : delete the "id" colors.
     *
     * @param id the id of the colors to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColors(@PathVariable("id") Integer id) {
        log.debug("REST request to delete Colors : {}", id);
        colorsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
