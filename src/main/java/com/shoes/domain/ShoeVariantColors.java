package com.shoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShoeVariantColors.
 */
@Entity
@Table(name = "shoe_variant_colors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoeVariantColors implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "price")
    private Float price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "shoes", "sales", "sizes", "shoeVariantColors", "shoeVariantSizes", "cartItems", "orderItems" },
        allowSetters = true
    )
    private ShoeVariants shoeVariants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shoeVariantColors", "cartItems", "orderItems" }, allowSetters = true)
    private Colors colors;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoeVariantColors id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrice() {
        return this.price;
    }

    public ShoeVariantColors price(Float price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ShoeVariantColors imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ShoeVariants getShoeVariants() {
        return this.shoeVariants;
    }

    public void setShoeVariants(ShoeVariants shoeVariants) {
        this.shoeVariants = shoeVariants;
    }

    public ShoeVariantColors shoeVariants(ShoeVariants shoeVariants) {
        this.setShoeVariants(shoeVariants);
        return this;
    }

    public Colors getColors() {
        return this.colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }

    public ShoeVariantColors colors(Colors colors) {
        this.setColors(colors);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoeVariantColors)) {
            return false;
        }
        return getId() != null && getId().equals(((ShoeVariantColors) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoeVariantColors{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
