package com.rij.amethyst_dev.Configuration;


import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Configuration
@EnableCaching
public class SpringCachingConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // Define multiple cache names
        List<String> cacheNames = List.of("discordRolesCache", "skins",
                "heads", "publicuser", "PagableUsers", "find",
                "planAllPlaytime", "planHeatmapData");

        // Create and add cache configurations for each cache name
        List<Cache> caches = cacheNames.stream()
                .map(ConcurrentMapCache::new)
                .collect(Collectors.toList());

        cacheManager.setCaches(caches);

        return cacheManager;
    }

}

