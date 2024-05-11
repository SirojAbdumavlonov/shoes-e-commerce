package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoes.domain.enumeration.CategoryName;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private CategoryName name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "category", "brand", "collection", "shoePurpose", "shoeVariants" }, allowSetters = true)
    private Set<Shoes> shoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryName getName() {
        return this.name;
    }

    public Category name(CategoryName name) {
        this.setName(name);
        return this;
    }

    public void setName(CategoryName name) {
        this.name = name;
    }

    public Set<Shoes> getShoes() {
        return this.shoes;
    }

    public void setShoes(Set<Shoes> shoes) {
        if (this.shoes != null) {
            this.shoes.forEach(i -> i.setCategory(null));
        }
        if (shoes != null) {
            shoes.forEach(i -> i.setCategory(this));
        }
        this.shoes = shoes;
    }

    public Category shoes(Set<Shoes> shoes) {
        this.setShoes(shoes);
        return this;
    }

    public Category addShoes(Shoes shoes) {
        this.shoes.add(shoes);
        shoes.setCategory(this);
        return this;
    }

    public Category removeShoes(Shoes shoes) {
        this.shoes.remove(shoes);
        shoes.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return getId() != null && getId().equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
