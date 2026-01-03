package se.meltastudio.cms.parts.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.meltastudio.cms.integration.common.SimpleTtlCache;
import se.meltastudio.cms.parts.api.PartOffer;

import java.time.Duration;
import java.util.List;

/**
 * Configuration for parts search caching.
 */
@Configuration
public class PartsSearchCacheConfig {

    @Value("${parts.search.cache.ttl:PT30M}")
    private String cacheTtl;

    /**
     * Create a TTL cache for parts search results.
     * Default TTL is 30 minutes, configurable via application.properties.
     */
    @Bean
    public SimpleTtlCache<String, List<PartOffer>> partsSearchCache() {
        Duration ttl = Duration.parse(cacheTtl);
        return new SimpleTtlCache<>(ttl);
    }
}
