package com.shoes.web.rest;

import com.shoes.domain.CartItems;
import com.shoes.repository.CartItemsRepository;
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
 * REST controller for managing {@link com.shoes.domain.CartItems}.
 */
@RestController
@RequestMapping("/api/cart-items")
@Transactional
public class CartItemsResource {

    private final Logger log = LoggerFactory.getLogger(CartItemsResource.class);

    private static final String ENTITY_NAME = "cartItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemsRepository cartItemsRepository;

    public CartItemsResource(CartItemsRepository cartItemsRepository) {
        this.cartItemsRepository = cartItemsRepository;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItems.
     *
     * @param cartItems the cartItems to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItems, or with status {@code 400 (Bad Request)} if the cartItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CartItems> createCartItems(@RequestBody CartItems cartItems) throws URISyntaxException {
        log.debug("REST request to save CartItems : {}", cartItems);
        if (cartItems.getId() != null) {
            throw new BadRequestAlertException("A new cartItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cartItems = cartItemsRepository.save(cartItems);
        return ResponseEntity.created(new URI("/api/cart-items/" + cartItems.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cartItems.getId().toString()))
            .body(cartItems);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItems.
     *
     * @param id the id of the cartItems to save.
     * @param cartItems the cartItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItems,
     * or with status {@code 400 (Bad Request)} if the cartItems is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CartItems> updateCartItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItems cartItems
    ) throws URISyntaxException {
        log.debug("REST request to update CartItems : {}, {}", id, cartItems);
        if (cartItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cartItems = cartItemsRepository.save(cartItems);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItems.getId().toString()))
            .body(cartItems);
    }

    /**
     * {@code PATCH  /cart-items/:id} : Partial updates given fields of an existing cartItems, field will ignore if it is null
     *
     * @param id the id of the cartItems to save.
     * @param cartItems the cartItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItems,
     * or with status {@code 400 (Bad Request)} if the cartItems is not valid,
     * or with status {@code 404 (Not Found)} if the cartItems is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CartItems> partialUpdateCartItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItems cartItems
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartItems partially : {}, {}", id, cartItems);
        if (cartItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartItems> result = cartItemsRepository
            .findById(cartItems.getId())
            .map(existingCartItems -> {
                if (cartItems.getQuantity() != null) {
                    existingCartItems.setQuantity(cartItems.getQuantity());
                }

                return existingCartItems;
            })
            .map(cartItemsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItems.getId().toString())
        );
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("")
    public List<CartItems> getAllCartItems() {
        log.debug("REST request to get all CartItems");
        return cartItemsRepository.findAll();
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItems.
     *
     * @param id the id of the cartItems to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItems, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CartItems> getCartItems(@PathVariable("id") Long id) {
        log.debug("REST request to get CartItems : {}", id);
        Optional<CartItems> cartItems = cartItemsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cartItems);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItems.
     *
     * @param id the id of the cartItems to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItems(@PathVariable("id") Long id) {
        log.debug("REST request to delete CartItems : {}", id);
        cartItemsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
