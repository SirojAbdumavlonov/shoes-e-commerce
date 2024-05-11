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
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private Instant createdAt;

    @JsonIgnoreProperties(value = { "customer", "cartItems" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
    private Cart cart;

    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
    private CustomerDetails customerDetails;

    @JsonIgnoreProperties(value = { "customer", "wishListItems" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
    private WishList wishList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer", "orderItems" }, allowSetters = true)
    private Set<Orders> orders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public Customer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public Customer password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Customer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        if (this.cart != null) {
            this.cart.setCustomer(null);
        }
        if (cart != null) {
            cart.setCustomer(this);
        }
        this.cart = cart;
    }

    public Customer cart(Cart cart) {
        this.setCart(cart);
        return this;
    }

    public CustomerDetails getCustomerDetails() {
        return this.customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        if (this.customerDetails != null) {
            this.customerDetails.setCustomer(null);
        }
        if (customerDetails != null) {
            customerDetails.setCustomer(this);
        }
        this.customerDetails = customerDetails;
    }

    public Customer customerDetails(CustomerDetails customerDetails) {
        this.setCustomerDetails(customerDetails);
        return this;
    }

    public WishList getWishList() {
        return this.wishList;
    }

    public void setWishList(WishList wishList) {
        if (this.wishList != null) {
            this.wishList.setCustomer(null);
        }
        if (wishList != null) {
            wishList.setCustomer(this);
        }
        this.wishList = wishList;
    }

    public Customer wishList(WishList wishList) {
        this.setWishList(wishList);
        return this;
    }

    public Set<Orders> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Orders> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setCustomer(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setCustomer(this));
        }
        this.orders = orders;
    }

    public Customer orders(Set<Orders> orders) {
        this.setOrders(orders);
        return this;
    }

    public Customer addOrders(Orders orders) {
        this.orders.add(orders);
        orders.setCustomer(this);
        return this;
    }

    public Customer removeOrders(Orders orders) {
        this.orders.remove(orders);
        orders.setCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
