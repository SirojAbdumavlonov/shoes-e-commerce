package com.shoes.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.shoes.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.shoes.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.shoes.domain.User.class.getName());
            createCache(cm, com.shoes.domain.Authority.class.getName());
            createCache(cm, com.shoes.domain.User.class.getName() + ".authorities");
            createCache(cm, com.shoes.domain.Customer.class.getName());
            createCache(cm, com.shoes.domain.Customer.class.getName() + ".orders");
            createCache(cm, com.shoes.domain.CustomerDetails.class.getName());
            createCache(cm, com.shoes.domain.Category.class.getName());
            createCache(cm, com.shoes.domain.Category.class.getName() + ".shoes");
            createCache(cm, com.shoes.domain.Brand.class.getName());
            createCache(cm, com.shoes.domain.Brand.class.getName() + ".shoes");
            createCache(cm, com.shoes.domain.Collection.class.getName());
            createCache(cm, com.shoes.domain.Collection.class.getName() + ".shoes");
            createCache(cm, com.shoes.domain.ShoePurpose.class.getName());
            createCache(cm, com.shoes.domain.ShoePurpose.class.getName() + ".shoes");
            createCache(cm, com.shoes.domain.Shoes.class.getName());
            createCache(cm, com.shoes.domain.Shoes.class.getName() + ".shoeVariants");
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName());
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName() + ".sizes");
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName() + ".shoeVariantColors");
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName() + ".shoeVariantSizes");
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName() + ".cartItems");
            createCache(cm, com.shoes.domain.ShoeVariants.class.getName() + ".orderItems");
            createCache(cm, com.shoes.domain.ShoeVariantColors.class.getName());
            createCache(cm, com.shoes.domain.ShoeVariantSizes.class.getName());
            createCache(cm, com.shoes.domain.Sizes.class.getName());
            createCache(cm, com.shoes.domain.Sizes.class.getName() + ".shoeVariantSizes");
            createCache(cm, com.shoes.domain.Sizes.class.getName() + ".cartItems");
            createCache(cm, com.shoes.domain.Sizes.class.getName() + ".orderItems");
            createCache(cm, com.shoes.domain.Sizes.class.getName() + ".shoeVariants");
            createCache(cm, com.shoes.domain.Sales.class.getName());
            createCache(cm, com.shoes.domain.Sales.class.getName() + ".shoeVariants");
            createCache(cm, com.shoes.domain.Colors.class.getName());
            createCache(cm, com.shoes.domain.Colors.class.getName() + ".shoeVariantColors");
            createCache(cm, com.shoes.domain.Colors.class.getName() + ".cartItems");
            createCache(cm, com.shoes.domain.Colors.class.getName() + ".orderItems");
            createCache(cm, com.shoes.domain.WishList.class.getName());
            createCache(cm, com.shoes.domain.WishList.class.getName() + ".wishListItems");
            createCache(cm, com.shoes.domain.WishListItems.class.getName());
            createCache(cm, com.shoes.domain.Cart.class.getName());
            createCache(cm, com.shoes.domain.Cart.class.getName() + ".cartItems");
            createCache(cm, com.shoes.domain.CartItems.class.getName());
            createCache(cm, com.shoes.domain.Orders.class.getName());
            createCache(cm, com.shoes.domain.Orders.class.getName() + ".orderItems");
            createCache(cm, com.shoes.domain.OrderItems.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
