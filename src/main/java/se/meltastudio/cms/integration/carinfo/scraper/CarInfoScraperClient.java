package se.meltastudio.cms.integration.carinfo.scraper;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.meltastudio.cms.integration.carinfo.CarInfoClient;
import se.meltastudio.cms.integration.carinfo.dto.CarInfoVehicleDto;
import se.meltastudio.cms.integration.common.PoliteRateLimiter;
import se.meltastudio.cms.integration.common.SimpleTtlCache;

import java.io.IOException;

@Component
public class CarInfoScraperClient implements CarInfoClient {

    private static final Logger log = LoggerFactory.getLogger(CarInfoScraperClient.class);

    private final CarInfoScraperProperties properties;
    private final JsoupHtmlFetcher fetcher;
    private final CarInfoHtmlParser parser;
    private final CarInfoHtmlParser.Selectors selectors;
    private final SimpleTtlCache<String, CarInfoVehicleDto> cache;
    private final PoliteRateLimiter rateLimiter;

    public CarInfoScraperClient(
            CarInfoScraperProperties properties,
            JsoupHtmlFetcher fetcher,
            CarInfoHtmlParser parser,
            CarInfoHtmlParser.Selectors selectors,
            SimpleTtlCache<String, CarInfoVehicleDto> cache,
            PoliteRateLimiter rateLimiter
    ) {
        this.properties = properties;
        this.fetcher = fetcher;
        this.parser = parser;
        this.selectors = selectors;
        this.cache = cache;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public CarInfoVehicleDto getVehicleByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new CarInfoScraperException("INVALID_INPUT", "Registration number cannot be null or empty");
        }

        // Normalize registration number (remove spaces, uppercase)
        String normalizedRegNo = registrationNumber.replaceAll("\\s+", "").toUpperCase();

        // Check cache first
        return cache.get(normalizedRegNo)
                .orElseGet(() -> fetchAndCache(normalizedRegNo));
    }

    private CarInfoVehicleDto fetchAndCache(String registrationNumber) {
        log.info("Fetching vehicle info for registration number: {}", registrationNumber);

        CarInfoVehicleDto result = fetchWithRetry(registrationNumber);

        // Cache the result
        cache.put(registrationNumber, result);

        return result;
    }

    private CarInfoVehicleDto fetchWithRetry(String registrationNumber) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= properties.getMaxRetries()) {
            try {
                // Rate limiting
                rateLimiter.acquire();

                // Build URL
                String url = buildUrl(registrationNumber);
                log.debug("Fetching URL: {}", url);

                // Fetch HTML
                Document doc = fetcher.fetch(
                        url,
                        (int) properties.getTimeout().toMillis(),
                        properties.getUserAgent()
                );

                // Parse HTML
                CarInfoVehicleDto dto = parser.parse(doc, selectors, registrationNumber);

                log.info("Successfully scraped vehicle info for: {}", registrationNumber);
                return dto;

            } catch (IOException e) {
                lastException = e;
                attempt++;
                log.warn("Attempt {}/{} failed for registration number {}: {}",
                        attempt, properties.getMaxRetries() + 1, registrationNumber, e.getMessage());

                if (attempt <= properties.getMaxRetries()) {
                    // Wait a bit before retrying
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CarInfoScraperException("INTERRUPTED", "Scraper interrupted during retry wait", ie);
                    }
                }
            } catch (Exception e) {
                // Non-retryable errors
                log.error("Non-retryable error scraping registration number {}: {}", registrationNumber, e.getMessage());
                throw new CarInfoScraperException("SCRAPE_ERROR", "Failed to scrape vehicle info", e);
            }
        }

        // All retries exhausted
        log.error("All retry attempts exhausted for registration number: {}", registrationNumber);
        throw new CarInfoScraperException("NETWORK_ERROR", "Failed to fetch vehicle info after retries", lastException);
    }

    private String buildUrl(String registrationNumber) {
        String baseUrl = properties.getBaseUrl();
        String pathTemplate = properties.getLookupPathTemplate();

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new CarInfoScraperException("CONFIG_ERROR", "Base URL not configured");
        }

        if (pathTemplate == null || pathTemplate.isBlank()) {
            throw new CarInfoScraperException("CONFIG_ERROR", "Lookup path template not configured");
        }

        // Replace {reg} placeholder with actual registration number
        String path = pathTemplate.replace("{reg}", registrationNumber);

        return baseUrl + path;
    }
}
