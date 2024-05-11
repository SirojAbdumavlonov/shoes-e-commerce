package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Collection.
 */
@Entity
@Table(name = "collection")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Collection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "collection")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "category", "brand", "collection", "shoePurpose", "shoeVariants" }, allowSetters = true)
    private Set<Shoes> shoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public Collection id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Collection name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Shoes> getShoes() {
        return this.shoes;
    }

    public void setShoes(Set<Shoes> shoes) {
        if (this.shoes != null) {
            this.shoes.forEach(i -> i.setCollection(null));
        }
        if (shoes != null) {
            shoes.forEach(i -> i.setCollection(this));
        }
        this.shoes = shoes;
    }

    public Collection shoes(Set<Shoes> shoes) {
        this.setShoes(shoes);
        return this;
    }

    public Collection addShoes(Shoes shoes) {
        this.shoes.add(shoes);
        shoes.setCollection(this);
        return this;
    }

    public Collection removeShoes(Shoes shoes) {
        this.shoes.remove(shoes);
        shoes.setCollection(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Collection)) {
            return false;
        }
        return getId() != null && getId().equals(((Collection) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Collection{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
