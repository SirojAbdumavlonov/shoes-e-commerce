package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Colors.
 */
@Entity
@Table(name = "colors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Colors implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "color_name")
    private String colorName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "colors")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shoeVariants", "colors" }, allowSetters = true)
    private Set<ShoeVariantColors> shoeVariantColors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "colors")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cart", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<CartItems> cartItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "colors")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<OrderItems> orderItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Colors id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColorName() {
        return this.colorName;
    }

    public Colors colorName(String colorName) {
        this.setColorName(colorName);
        return this;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Set<ShoeVariantColors> getShoeVariantColors() {
        return this.shoeVariantColors;
    }

    public void setShoeVariantColors(Set<ShoeVariantColors> shoeVariantColors) {
        if (this.shoeVariantColors != null) {
            this.shoeVariantColors.forEach(i -> i.setColors(null));
        }
        if (shoeVariantColors != null) {
            shoeVariantColors.forEach(i -> i.setColors(this));
        }
        this.shoeVariantColors = shoeVariantColors;
    }

    public Colors shoeVariantColors(Set<ShoeVariantColors> shoeVariantColors) {
        this.setShoeVariantColors(shoeVariantColors);
        return this;
    }

    public Colors addShoeVariantColors(ShoeVariantColors shoeVariantColors) {
        this.shoeVariantColors.add(shoeVariantColors);
        shoeVariantColors.setColors(this);
        return this;
    }

    public Colors removeShoeVariantColors(ShoeVariantColors shoeVariantColors) {
        this.shoeVariantColors.remove(shoeVariantColors);
        shoeVariantColors.setColors(null);
        return this;
    }

    public Set<CartItems> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setColors(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setColors(this));
        }
        this.cartItems = cartItems;
    }

    public Colors cartItems(Set<CartItems> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public Colors addCartItems(CartItems cartItems) {
        this.cartItems.add(cartItems);
        cartItems.setColors(this);
        return this;
    }

    public Colors removeCartItems(CartItems cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setColors(null);
        return this;
    }

    public Set<OrderItems> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItems> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setColors(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setColors(this));
        }
        this.orderItems = orderItems;
    }

    public Colors orderItems(Set<OrderItems> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public Colors addOrderItems(OrderItems orderItems) {
        this.orderItems.add(orderItems);
        orderItems.setColors(this);
        return this;
    }

    public Colors removeOrderItems(OrderItems orderItems) {
        this.orderItems.remove(orderItems);
        orderItems.setColors(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Colors)) {
            return false;
        }
        return getId() != null && getId().equals(((Colors) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Colors{" +
            "id=" + getId() +
            ", colorName='" + getColorName() + "'" +
            "}";
    }
}
