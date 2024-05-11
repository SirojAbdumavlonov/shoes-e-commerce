package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Shoes.
 */
@Entity
@Table(name = "shoes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shoes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoes" }, allowSetters = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoes" }, allowSetters = true)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoes" }, allowSetters = true)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoes" }, allowSetters = true)
    private ShoePurpose shoePurpose;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoes")
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

    public Shoes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Shoes description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Shoes category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Brand getBrand() {
        return this.brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Shoes brand(Brand brand) {
        this.setBrand(brand);
        return this;
    }

    public Collection getCollection() {
        return this.collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Shoes collection(Collection collection) {
        this.setCollection(collection);
        return this;
    }

    public ShoePurpose getShoePurpose() {
        return this.shoePurpose;
    }

    public void setShoePurpose(ShoePurpose shoePurpose) {
        this.shoePurpose = shoePurpose;
    }

    public Shoes shoePurpose(ShoePurpose shoePurpose) {
        this.setShoePurpose(shoePurpose);
        return this;
    }

    public Set<ShoeVariants> getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(Set<ShoeVariants> shoeVariants) {
        if (this.shoeVariants != null) {
            this.shoeVariants.forEach(i -> i.setShoes(null));
        }
        if (shoeVariants != null) {
            shoeVariants.forEach(i -> i.setShoes(this));
        }
        this.shoeVariants = shoeVariants;
    }

    public Shoes shoeVariants(Set<ShoeVariants> shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Shoes addShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.add(shoeVariants);
        shoeVariants.setShoes(this);
        return this;
    }

    public Shoes removeShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants.remove(shoeVariants);
        shoeVariants.setShoes(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shoes)) {
            return false;
        }
        return getId() != null && getId().equals(((Shoes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shoes{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
