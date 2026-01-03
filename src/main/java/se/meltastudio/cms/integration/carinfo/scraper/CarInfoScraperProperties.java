package se.meltastudio.cms.integration.carinfo.scraper;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "carinfo.scraper")
public class CarInfoScraperProperties {

    /**
     * Example (placeholder): https://www.car.info
     */
    private String baseUrl;

    /**
     * Path template for lookup.
     * Keep it configurable. Example: "/sv-se/lookup?reg={reg}" (placeholder)
     */
    private String lookupPathTemplate;

    private Duration timeout = Duration.ofSeconds(8);

    /**
     * Politeness: minimum time between requests.
     */
    private Duration minDelayBetweenRequests = Duration.ofMillis(800);

    /**
     * Cache TTL to avoid repeated hits.
     */
    private Duration cacheTtl = Duration.ofHours(6);

    /**
     * Max retries for transient network failures.
     */
    private int maxRetries = 1;

    private String userAgent = "CMS-Bilar-PoC/0.1 (+internal prototype)";

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getLookupPathTemplate() { return lookupPathTemplate; }
    public void setLookupPathTemplate(String lookupPathTemplate) { this.lookupPathTemplate = lookupPathTemplate; }

    public Duration getTimeout() { return timeout; }
    public void setTimeout(Duration timeout) { this.timeout = timeout; }

    public Duration getMinDelayBetweenRequests() { return minDelayBetweenRequests; }
    public void setMinDelayBetweenRequests(Duration minDelayBetweenRequests) { this.minDelayBetweenRequests = minDelayBetweenRequests; }

    public Duration getCacheTtl() { return cacheTtl; }
    public void setCacheTtl(Duration cacheTtl) { this.cacheTtl = cacheTtl; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
