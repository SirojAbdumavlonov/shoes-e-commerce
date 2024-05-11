package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoes.domain.enumeration.Status;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShoeVariantSizes.
 */
@Entity
@Table(name = "shoe_variant_sizes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoeVariantSizes implements Serializable {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoeVariantSizes", "cartItems", "orderItems", "shoeVariants" }, allowSetters = true)
    private Sizes sizes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "shoes", "sales", "sizes", "shoeVariantColors", "shoeVariantSizes", "cartItems", "orderItems" },
        allowSetters = true
    )
    private ShoeVariants shoeVariants;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoeVariantSizes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ShoeVariantSizes quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Status getStatus() {
        return this.status;
    }

    public ShoeVariantSizes status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Sizes getSizes() {
        return this.sizes;
    }

    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    public ShoeVariantSizes sizes(Sizes sizes) {
        this.setSizes(sizes);
        return this;
    }

    public ShoeVariants getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants = shoeVariants;
    }

    public ShoeVariantSizes shoeVariants(ShoeVariants shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoeVariantSizes)) {
            return false;
        }
        return getId() != null && getId().equals(((ShoeVariantSizes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoeVariantSizes{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
