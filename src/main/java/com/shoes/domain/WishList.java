package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WishList.
 */
@Entity
@Table(name = "wish_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private Instant createdAt;

    @JsonIgnoreProperties(value = { "cart", "customerDetails", "wishList", "orders" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wishList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "wishList" }, allowSetters = true)
    private Set<WishListItems> wishListItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WishList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public WishList createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public WishList customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Set<WishListItems> getWishListItems() {
        return this.wishListItems;
    }

    public void setWishListItems(Set<WishListItems> wishListItems) {
        if (this.wishListItems != null) {
            this.wishListItems.forEach(i -> i.setWishList(null));
        }
        if (wishListItems != null) {
            wishListItems.forEach(i -> i.setWishList(this));
        }
        this.wishListItems = wishListItems;
    }

    public WishList wishListItems(Set<WishListItems> wishListItems) {
        this.setWishListItems(wishListItems);
        return this;
    }

    public WishList addWishListItems(WishListItems wishListItems) {
        this.wishListItems.add(wishListItems);
        wishListItems.setWishList(this);
        return this;
    }

    public WishList removeWishListItems(WishListItems wishListItems) {
        this.wishListItems.remove(wishListItems);
        wishListItems.setWishList(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WishList)) {
            return false;
        }
        return getId() != null && getId().equals(((WishList) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishList{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
