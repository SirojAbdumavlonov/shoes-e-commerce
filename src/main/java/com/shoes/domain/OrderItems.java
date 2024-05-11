package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OrderItems.
 */
@Entity
@Table(name = "order_items")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "orderItems" }, allowSetters = true)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoeVariantColors", "cartItems", "orderItems" }, allowSetters = true)
    private Colors colors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "shoes", "sales", "sizes", "shoeVariantColors", "shoeVariantSizes", "cartItems", "orderItems" },
        allowSetters = true
    )
    private ShoeVariants shoeVariants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoeVariantSizes", "cartItems", "orderItems", "shoeVariants" }, allowSetters = true)
    private Sizes sizes;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItems id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderItems quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return this.price;
    }

    public OrderItems price(Long price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Orders getOrders() {
        return this.orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public OrderItems orders(Orders orders) {
        this.setOrders(orders);
        return this;
    }

    public Colors getColors() {
        return this.colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }

    public OrderItems colors(Colors colors) {
        this.setColors(colors);
        return this;
    }

    public ShoeVariants getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants = shoeVariants;
    }

    public OrderItems shoeVariants(ShoeVariants shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Sizes getSizes() {
        return this.sizes;
    }

    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    public OrderItems sizes(Sizes sizes) {
        this.setSizes(sizes);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItems)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItems) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItems{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            "}";
    }
}
