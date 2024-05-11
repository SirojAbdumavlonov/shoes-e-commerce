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
 * A Sales.
 */
@Entity
@Table(name = "sales")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sales implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "new_price")
    private Integer newPrice;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sales")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "shoes", "sales", "sizes", "shoeVariantColors", "shoeVariantSizes", "cartItems", "orderItems" },
        allowSetters = true
    )
    private Set<ShoeVariants> shoeVariants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sales id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Sales startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Sales endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getDiscountPercentage() {
        return this.discountPercentage;
    }

    public Sales discountPercentage(Integer discountPercentage) {
        this.setDiscountPercentage(discountPercentage);
        return this;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getNewPrice() {
        return this.newPrice;
    }

    public Sales newPrice(Integer newPrice) {
        this.setNewPrice(newPrice);
        return this;
    }

    public void setNewPrice(Integer newPrice) {
        this.newPrice = newPrice;
    }

    public Set<ShoeVariants> getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(Set<ShoeVariants> shoeVariants) {
        if (this.shoeVariants != null) {
            this.shoeVariants.forEach(i -> i.setSales(null));
        }
        if (shoeVariants != null) {
            shoeVariants.forEach(i -> i.setSales(this));
        }
        this.shoeVariants = shoeVariants;
    }

    public Sales shoeVariants(Set<ShoeVariants> shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Sales addShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.add(shoeVariants);
        shoeVariants.setSales(this);
        return this;
    }

    public Sales removeShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.remove(shoeVariants);
        shoeVariants.setSales(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sales)) {
            return false;
        }
        return getId() != null && getId().equals(((Sales) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sales{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", discountPercentage=" + getDiscountPercentage() +
            ", newPrice=" + getNewPrice() +
            "}";
    }
}
