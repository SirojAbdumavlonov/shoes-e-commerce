package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Sizes.
 */
@Entity
@Table(name = "sizes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sizes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "size_in_numbers")
    private Float sizeInNumbers;

    @Column(name = "size_in_letters")
    private String sizeInLetters;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sizes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sizes", "shoeVariants" }, allowSetters = true)
    private Set<ShoeVariantSizes> shoeVariantSizes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sizes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cart", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<CartItems> cartItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sizes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<OrderItems> orderItems = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sizes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "shoes", "sales", "sizes", "shoeVariantColors", "shoeVariantSizes", "cartItems", "orderItems" },
        allowSetters = true
    )
    private Set<ShoeVariants> shoeVariants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Sizes id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSizeInNumbers() {
        return this.sizeInNumbers;
    }

    public Sizes sizeInNumbers(Float sizeInNumbers) {
        this.setSizeInNumbers(sizeInNumbers);
        return this;
    }

    public void setSizeInNumbers(Float sizeInNumbers) {
        this.sizeInNumbers = sizeInNumbers;
    }

    public String getSizeInLetters() {
        return this.sizeInLetters;
    }

    public Sizes sizeInLetters(String sizeInLetters) {
        this.setSizeInLetters(sizeInLetters);
        return this;
    }

    public void setSizeInLetters(String sizeInLetters) {
        this.sizeInLetters = sizeInLetters;
    }

    public Set<ShoeVariantSizes> getShoeVariantSizes() {
        return this.shoeVariantSizes;
    }

    public void setShoeVariantSizes(Set<ShoeVariantSizes> shoeVariantSizes) {
        if (this.shoeVariantSizes != null) {
            this.shoeVariantSizes.forEach(i -> i.setSizes(null));
        }
        if (shoeVariantSizes != null) {
            shoeVariantSizes.forEach(i -> i.setSizes(this));
        }
        this.shoeVariantSizes = shoeVariantSizes;
    }

    public Sizes shoeVariantSizes(Set<ShoeVariantSizes> shoeVariantSizes) {
        this.setShoeVariantSizes(shoeVariantSizes);
        return this;
    }

    public Sizes addShoeVariantSizes(ShoeVariantSizes shoeVariantSizes) {
        this.shoeVariantSizes.add(shoeVariantSizes);
        shoeVariantSizes.setSizes(this);
        return this;
    }

    public Sizes removeShoeVariantSizes(ShoeVariantSizes shoeVariantSizes) {
        this.shoeVariantSizes.remove(shoeVariantSizes);
        shoeVariantSizes.setSizes(null);
        return this;
    }

    public Set<CartItems> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setSizes(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setSizes(this));
        }
        this.cartItems = cartItems;
    }

    public Sizes cartItems(Set<CartItems> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public Sizes addCartItems(CartItems cartItems) {
        this.cartItems.add(cartItems);
        cartItems.setSizes(this);
        return this;
    }

    public Sizes removeCartItems(CartItems cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setSizes(null);
        return this;
    }

    public Set<OrderItems> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItems> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setSizes(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setSizes(this));
        }
        this.orderItems = orderItems;
    }

    public Sizes orderItems(Set<OrderItems> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public Sizes addOrderItems(OrderItems orderItems) {
        this.orderItems.add(orderItems);
        orderItems.setSizes(this);
        return this;
    }

    public Sizes removeOrderItems(OrderItems orderItems) {
        this.orderItems.remove(orderItems);
        orderItems.setSizes(null);
        return this;
    }

    public Set<ShoeVariants> getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(Set<ShoeVariants> shoeVariants) {
        if (this.shoeVariants != null) {
            this.shoeVariants.forEach(i -> i.removeSizes(this));
        }
        if (shoeVariants != null) {
            shoeVariants.forEach(i -> i.addSizes(this));
        }
        this.shoeVariants = shoeVariants;
    }

    public Sizes shoeVariants(Set<ShoeVariants> shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Sizes addShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.add(shoeVariants);
        shoeVariants.getSizes().add(this);
        return this;
    }

    public Sizes removeShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.remove(shoeVariants);
        shoeVariants.getSizes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sizes)) {
            return false;
        }
        return getId() != null && getId().equals(((Sizes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sizes{" +
            "id=" + getId() +
            ", sizeInNumbers=" + getSizeInNumbers() +
            ", sizeInLetters='" + getSizeInLetters() + "'" +
            "}";
    }
}
