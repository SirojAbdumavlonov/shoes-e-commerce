package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoes.domain.enumeration.OrderStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "total_price")
    private Long totalPrice;

    @Column(name = "order_date")
    private Instant orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cart", "customerDetails", "wishList", "orders" }, allowSetters = true)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "colors", "shoeVariants", "sizes" }, allowSetters = true)
    private Set<OrderItems> orderItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Orders id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotalPrice() {
        return this.totalPrice;
    }

    public Orders totalPrice(Long totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Instant getOrderDate() {
        return this.orderDate;
    }

    public Orders orderDate(Instant orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Orders status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Orders customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Set<OrderItems> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItems> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setOrders(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrders(this));
        }
        this.orderItems = orderItems;
    }

    public Orders orderItems(Set<OrderItems> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public Orders addOrderItems(OrderItems orderItems) {
        this.orderItems.add(orderItems);
        orderItems.setOrders(this);
        return this;
    }

    public Orders removeOrderItems(OrderItems orderItems) {
        this.orderItems.remove(orderItems);
        orderItems.setOrders(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Orders)) {
            return false;
        }
        return getId() != null && getId().equals(((Orders) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", totalPrice=" + getTotalPrice() +
            ", orderDate='" + getOrderDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
