package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoes.domain.enumeration.ShoePurposeType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShoePurpose.
 */
@Entity
@Table(name = "shoe_purpose")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoePurpose implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ShoePurposeType type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoePurpose")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "category", "brand", "collection", "shoePurpose", "shoeVariants" }, allowSetters = true)
    private Set<Shoes> shoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getId() {
        return this.id;
    }

    public ShoePurpose id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ShoePurposeType getType() {
        return this.type;
    }

    public ShoePurpose type(ShoePurposeType type) {
        this.setType(type);
        return this;
    }

    public void setType(ShoePurposeType type) {
        this.type = type;
    }

    public Set<Shoes> getShoes() {
        return this.shoes;
    }

    public void setShoes(Set<Shoes> shoes) {
        if (this.shoes != null) {
            this.shoes.forEach(i -> i.setShoePurpose(null));
        }
        if (shoes != null) {
            shoes.forEach(i -> i.setShoePurpose(this));
        }
        this.shoes = shoes;
    }

    public ShoePurpose shoes(Set<Shoes> shoes) {
        this.setShoes(shoes);
        return this;
    }

    public ShoePurpose addShoes(Shoes shoes) {
        this.shoes.add(shoes);
        shoes.setShoePurpose(this);
        return this;
    }

    public ShoePurpose removeShoes(Shoes shoes) {
        this.shoes.remove(shoes);
        shoes.setShoePurpose(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoePurpose)) {
            return false;
        }
        return getId() != null && getId().equals(((ShoePurpose) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoePurpose{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
