package com.capstone.EventEase.Config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

@Configuration
@EnableCaching
public class CacheConfig {



    @Bean
    public CacheManager cacheStrategy() {
        // Explicitly specify Caffeine as the provider
//        CacheManager cacheManager = Caching.getCachingProvider(
//                "com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider"
//        ).getCacheManager();
//
//        MutableConfiguration<String, Object> config =
//                new MutableConfiguration<String, Object>()
//                        .setTypes(String.class, Object.class)
//                        .setStatisticsEnabled(true)
//                        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES));
//
//        cacheManager.createCache("bucket4j-cache", config);

//        MutableConfiguration<String,Object> generalCacheConfig = new MutableConfiguration<String,Object>()
//                .setTypes(String.class,Object.class)
//                .setStatisticsEnabled(true)
//                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR));
//        cacheManager.createCache("general-cache",generalCacheConfig);



        CacheManager cacheManager = Caching.getCachingProvider("com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider"
        ).getCacheManager();

        MutableConfiguration<String,Object> config = new MutableConfiguration<String,Object>()
                .setTypes(String.class,Object.class)
                .setStatisticsEnabled(true)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES));
        cacheManager.createCache("bucket4j-cache",config);


        return cacheManager;
    }
}