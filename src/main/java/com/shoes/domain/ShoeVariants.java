package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoes.domain.enumeration.Status;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShoeVariants.
 */
@Entity
@Table(name = "shoe_variants")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoeVariants implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category", "brand", "collection", "shoePurpose", "shoeVariants" }, allowSetters = true)
    private Shoes shoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoeVariants" }, allowSetters = true)
    private Sales sales;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_shoe_variants__sizes",
        joinColumns = @JoinColumn(name = "shoe_variants_id"),
        inverseJoinColumns = @JoinColumn(name = "sizes_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shoeVariantSizes", "cartItems", "orderItems", "shoeVariants" }, allowSetters = true)
    private Set<Sizes> sizes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoeVariants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shoeVariants", "colors" }, allowSetters = true)
    private Set<ShoeVariantColors> shoeVariantColors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoeVariants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sizes", "shoeVariants" }, allowSetters = true)
    private Set<ShoeVariantSizes> shoeVariantSizes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoeVariants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cart", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<CartItems> cartItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoeVariants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<OrderItems> orderItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoeVariants id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ShoeVariants quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Status getStatus() {
        return this.status;
    }

    public ShoeVariants status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public ShoeVariants photoUrl(String photoUrl) {
        this.setPhotoUrl(photoUrl);
        return this;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Shoes getShoes() {
        return this.shoes;
    }

    public void setShoes(Shoes shoes) {
        this.shoes = shoes;
    }

    public ShoeVariants shoes(Shoes shoes) {
        this.setShoes(shoes);
        return this;
    }

    public Sales getSales() {
        return this.sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public ShoeVariants sales(Sales sales) {
        this.setSales(sales);
        return this;
    }

    public Set<Sizes> getSizes() {
        return this.sizes;
    }

    public void setSizes(Set<Sizes> sizes) {
        this.sizes = sizes;
    }

    public ShoeVariants sizes(Set<Sizes> sizes) {
        this.setSizes(sizes);
        return this;
    }

    public ShoeVariants addSizes(Sizes sizes) {
        this.sizes.add(sizes);
        return this;
    }

    public ShoeVariants removeSizes(Sizes sizes) {
        this.sizes.remove(sizes);
        return this;
    }

    public Set<ShoeVariantColors> getShoeVariantColors() {
        return this.shoeVariantColors;
    }

    public void setShoeVariantColors(Set<ShoeVariantColors> shoeVariantColors) {
        if (this.shoeVariantColors != null) {
            this.shoeVariantColors.forEach(i -> i.setShoeVariants(null));
        }
        if (shoeVariantColors != null) {
            shoeVariantColors.forEach(i -> i.setShoeVariants(this));
        }
        this.shoeVariantColors = shoeVariantColors;
    }

    public ShoeVariants shoeVariantColors(Set<ShoeVariantColors> shoeVariantColors) {
        this.setShoeVariantColors(shoeVariantColors);
        return this;
    }

    public ShoeVariants addShoeVariantColors(ShoeVariantColors shoeVariantColors) {
        this.shoeVariantColors.add(shoeVariantColors);
        shoeVariantColors.setShoeVariants(this);
        return this;
    }

    public ShoeVariants removeShoeVariantColors(ShoeVariantColors shoeVariantColors) {
        this.shoeVariantColors.remove(shoeVariantColors);
        shoeVariantColors.setShoeVariants(null);
        return this;
    }

    public Set<ShoeVariantSizes> getShoeVariantSizes() {
        return this.shoeVariantSizes;
    }

    public void setShoeVariantSizes(Set<ShoeVariantSizes> shoeVariantSizes) {
        if (this.shoeVariantSizes != null) {
            this.shoeVariantSizes.forEach(i -> i.setShoeVariants(null));
        }
        if (shoeVariantSizes != null) {
            shoeVariantSizes.forEach(i -> i.setShoeVariants(this));
        }
        this.shoeVariantSizes = shoeVariantSizes;
    }

    public ShoeVariants shoeVariantSizes(Set<ShoeVariantSizes> shoeVariantSizes) {
        this.setShoeVariantSizes(shoeVariantSizes);
        return this;
    }

    public ShoeVariants addShoeVariantSizes(ShoeVariantSizes shoeVariantSizes) {
        this.shoeVariantSizes.add(shoeVariantSizes);
        shoeVariantSizes.setShoeVariants(this);
        return this;
    }

    public ShoeVariants removeShoeVariantSizes(ShoeVariantSizes shoeVariantSizes) {
        this.shoeVariantSizes.remove(shoeVariantSizes);
        shoeVariantSizes.setShoeVariants(null);
        return this;
    }

    public Set<CartItems> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setShoeVariants(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setShoeVariants(this));
        }
        this.cartItems = cartItems;
    }

    public ShoeVariants cartItems(Set<CartItems> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public ShoeVariants addCartItems(CartItems cartItems) {
        this.cartItems.add(cartItems);
        cartItems.setShoeVariants(this);
        return this;
    }

    public ShoeVariants removeCartItems(CartItems cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setShoeVariants(null);
        return this;
    }

    public Set<OrderItems> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItems> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setShoeVariants(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setShoeVariants(this));
        }
        this.orderItems = orderItems;
    }

    public ShoeVariants orderItems(Set<OrderItems> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public ShoeVariants addOrderItems(OrderItems orderItems) {
        this.orderItems.add(orderItems);
        orderItems.setShoeVariants(this);
        return this;
    }

    public ShoeVariants removeOrderItems(OrderItems orderItems) {
        this.orderItems.remove(orderItems);
        orderItems.setShoeVariants(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoeVariants)) {
            return false;
        }
        return getId() != null && getId().equals(((ShoeVariants) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoeVariants{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            "}";
    }
}
