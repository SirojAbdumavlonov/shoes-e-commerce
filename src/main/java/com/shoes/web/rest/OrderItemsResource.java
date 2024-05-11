package com.shoes.web.rest;

import com.shoes.domain.OrderItems;
import com.shoes.repository.OrderItemsRepository;
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
 * REST controller for managing {@link com.shoes.domain.OrderItems}.
 */
@RestController
@RequestMapping("/api/order-items")
@Transactional
public class OrderItemsResource {

    private final Logger log = LoggerFactory.getLogger(OrderItemsResource.class);

    private static final String ENTITY_NAME = "orderItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemsRepository orderItemsRepository;

    public OrderItemsResource(OrderItemsRepository orderItemsRepository) {
        this.orderItemsRepository = orderItemsRepository;
    }

    /**
     * {@code POST  /order-items} : Create a new orderItems.
     *
     * @param orderItems the orderItems to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItems, or with status {@code 400 (Bad Request)} if the orderItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderItems> createOrderItems(@RequestBody OrderItems orderItems) throws URISyntaxException {
        log.debug("REST request to save OrderItems : {}", orderItems);
        if (orderItems.getId() != null) {
            throw new BadRequestAlertException("A new orderItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderItems = orderItemsRepository.save(orderItems);
        return ResponseEntity.created(new URI("/api/order-items/" + orderItems.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, orderItems.getId().toString()))
            .body(orderItems);
    }

    /**
     * {@code PUT  /order-items/:id} : Updates an existing orderItems.
     *
     * @param id the id of the orderItems to save.
     * @param orderItems the orderItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItems,
     * or with status {@code 400 (Bad Request)} if the orderItems is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItems> updateOrderItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItems orderItems
    ) throws URISyntaxException {
        log.debug("REST request to update OrderItems : {}, {}", id, orderItems);
        if (orderItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderItems = orderItemsRepository.save(orderItems);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItems.getId().toString()))
            .body(orderItems);
    }

    /**
     * {@code PATCH  /order-items/:id} : Partial updates given fields of an existing orderItems, field will ignore if it is null
     *
     * @param id the id of the orderItems to save.
     * @param orderItems the orderItems to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItems,
     * or with status {@code 400 (Bad Request)} if the orderItems is not valid,
     * or with status {@code 404 (Not Found)} if the orderItems is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItems couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItems> partialUpdateOrderItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItems orderItems
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderItems partially : {}, {}", id, orderItems);
        if (orderItems.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItems.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItems> result = orderItemsRepository
            .findById(orderItems.getId())
            .map(existingOrderItems -> {
                if (orderItems.getQuantity() != null) {
                    existingOrderItems.setQuantity(orderItems.getQuantity());
                }
                if (orderItems.getPrice() != null) {
                    existingOrderItems.setPrice(orderItems.getPrice());
                }

                return existingOrderItems;
            })
            .map(orderItemsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItems.getId().toString())
        );
    }

    /**
     * {@code GET  /order-items} : get all the orderItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItems in body.
     */
    @GetMapping("")
    public List<OrderItems> getAllOrderItems() {
        log.debug("REST request to get all OrderItems");
        return orderItemsRepository.findAll();
    }

    /**
     * {@code GET  /order-items/:id} : get the "id" orderItems.
     *
     * @param id the id of the orderItems to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItems, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItems> getOrderItems(@PathVariable("id") Long id) {
        log.debug("REST request to get OrderItems : {}", id);
        Optional<OrderItems> orderItems = orderItemsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderItems);
    }

    /**
     * {@code DELETE  /order-items/:id} : delete the "id" orderItems.
     *
     * @param id the id of the orderItems to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItems(@PathVariable("id") Long id) {
        log.debug("REST request to delete OrderItems : {}", id);
        orderItemsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
