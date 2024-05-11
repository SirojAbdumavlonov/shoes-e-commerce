package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CartItems.
 */
@Entity
@Table(name = "cart_items")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CartItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "cartItems" }, allowSetters = true)
    private Cart cart;

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

    public CartItems id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public CartItems quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public CartItems cart(Cart cart) {
        this.setCart(cart);
        return this;
    }

    public Colors getColors() {
        return this.colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }

    public CartItems colors(Colors colors) {
        this.setColors(colors);
        return this;
    }

    public ShoeVariants getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants = shoeVariants;
    }

    public CartItems shoeVariants(ShoeVariants shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Sizes getSizes() {
        return this.sizes;
    }

    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    public CartItems sizes(Sizes sizes) {
        this.setSizes(sizes);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartItems)) {
            return false;
        }
        return getId() != null && getId().equals(((CartItems) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItems{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            "}";
    }
}
