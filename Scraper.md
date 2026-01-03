Vi ska bygga en scraper som proof of concept mot car.info.
Den ska:
har tydlig rate limit (t.ex. 1 request/sek),
har cache,
har timeout + retry,
har selectors i config (så den inte blir hårdkodad mot en HTML som ändras),
och är utbytbar (interface).

Nedan får du en komplett, körbar Spring Boot–implementation med Jsoup som HTML-klient. Den innehåller fulla klasser och lämnar bara själva CSS-selectors/field-mappningen som “konfigurerbar” (du fyller i när du tittat på HTML-strukturen).

Maven dependency
<dependency>
  <groupId>org.jsoup</groupId>
  <artifactId>jsoup</artifactId>
  <version>1.17.2</version>
</dependency>

Kod: Scraper-klient med rate limit + cache + parser
1) DTO (återanvänd din tidigare om du vill)
package com.meltacars.integration.carinfo.dto;

import java.time.LocalDate;

public class CarInfoVehicleDto {

    private String registrationNumber;
    private String vin;

    private String make;
    private String model;
    private String variant;

    private Integer modelYear;
    private String fuelType;
    private String transmission;

    private Integer enginePowerKw;
    private Integer enginePowerHp;

    private Integer curbWeightKg;
    private LocalDate firstRegistrationDate;

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public Integer getEnginePowerKw() { return enginePowerKw; }
    public void setEnginePowerKw(Integer enginePowerKw) { this.enginePowerKw = enginePowerKw; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }

    public Integer getCurbWeightKg() { return curbWeightKg; }
    public void setCurbWeightKg(Integer curbWeightKg) { this.curbWeightKg = curbWeightKg; }

    public LocalDate getFirstRegistrationDate() { return firstRegistrationDate; }
    public void setFirstRegistrationDate(LocalDate firstRegistrationDate) { this.firstRegistrationDate = firstRegistrationDate; }
}

2) Interface: CarInfoClient
package com.meltacars.integration.carinfo;

import com.meltacars.integration.carinfo.dto.CarInfoVehicleDto;

public interface CarInfoClient {
    CarInfoVehicleDto getVehicleByRegistrationNumber(String registrationNumber);
}

3) Properties: CarInfoScraperProperties
package com.meltacars.integration.carinfo.scraper;

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

4) TTL-cache: SimpleTtlCache
package com.meltacars.integration.common;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTtlCache<K, V> {

    private static final class Entry<V> {
        private final V value;
        private final Instant expiresAt;
        private Entry(V value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private final ConcurrentHashMap<K, Entry<V>> store = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Clock clock;

    public SimpleTtlCache(Duration ttl) {
        this(ttl, Clock.systemUTC());
    }

    public SimpleTtlCache(Duration ttl, Clock clock) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("TTL must be > 0");
        }
        this.ttl = ttl;
        this.clock = clock;
    }

    public Optional<V> get(K key) {
        Entry<V> entry = store.get(key);
        if (entry == null) return Optional.empty();

        if (Instant.now(clock).isAfter(entry.expiresAt)) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(entry.value);
    }

    public void put(K key, V value) {
        store.put(key, new Entry<>(value, Instant.now(clock).plus(ttl)));
    }

    public void invalidate(K key) {
        store.remove(key);
    }

    public void clear() {
        store.clear();
    }
}

5) Rate limiter: PoliteRateLimiter
package com.meltacars.integration.common;

import java.time.Duration;

public class PoliteRateLimiter {

    private final long minDelayMillis;
    private long nextAllowedAtMillis;

    public PoliteRateLimiter(Duration minDelayBetweenRequests) {
        if (minDelayBetweenRequests == null || minDelayBetweenRequests.isNegative()) {
            throw new IllegalArgumentException("minDelayBetweenRequests must be >= 0");
        }
        this.minDelayMillis = minDelayBetweenRequests.toMillis();
        this.nextAllowedAtMillis = 0L;
    }

    /**
     * Blocks the current thread until a request is allowed.
     * Intended for backend PoC usage.
     */
    public synchronized void acquire() {
        long now = System.currentTimeMillis();
        long waitMillis = Math.max(0L, nextAllowedAtMillis - now);

        if (waitMillis > 0L) {
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while rate limiting", e);
            }
        }

        long after = System.currentTimeMillis();
        nextAllowedAtMillis = after + minDelayMillis;
    }
}

6) Exception: CarInfoScraperException
package com.meltacars.integration.carinfo.scraper;

public class CarInfoScraperException extends RuntimeException {

    private final String code;

    public CarInfoScraperException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CarInfoScraperException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

7) HTML fetcher: JsoupHtmlFetcher
package com.meltacars.integration.carinfo.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Duration;

public class JsoupHtmlFetcher {

    private final Duration timeout;
    private final String userAgent;

    public JsoupHtmlFetcher(Duration timeout, String userAgent) {
        this.timeout = timeout;
        this.userAgent = userAgent;
    }

    public Document get(String url) {
        try {
            int timeoutMillis = Math.toIntExact(Math.max(1000L, timeout.toMillis()));
            return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(timeoutMillis)
                    .followRedirects(true)
                    .get();
        } catch (IOException e) {
            throw new CarInfoScraperException("FETCH_FAILED", "Failed to fetch url: " + url, e);
        }
    }
}

8) Parser: CarInfoHtmlParser (selectors/field-map på ett ställe)
package com.meltacars.integration.carinfo.scraper;

import com.meltacars.integration.carinfo.dto.CarInfoVehicleDto;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CarInfoHtmlParser {

    /**
     * Configure these selectors after inspecting the HTML.
     * Keep all scraping assumptions here, so the rest of the system stays stable.
     */
    public static class Selectors {
        private String make;
        private String model;
        private String variant;
        private String vin;
        private String modelYear;
        private String fuelType;
        private String transmission;
        private String enginePowerKw;
        private String curbWeightKg;
        private String firstRegistrationDate;

        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getVariant() { return variant; }
        public void setVariant(String variant) { this.variant = variant; }

        public String getVin() { return vin; }
        public void setVin(String vin) { this.vin = vin; }

        public String getModelYear() { return modelYear; }
        public void setModelYear(String modelYear) { this.modelYear = modelYear; }

        public String getFuelType() { return fuelType; }
        public void setFuelType(String fuelType) { this.fuelType = fuelType; }

        public String getTransmission() { return transmission; }
        public void setTransmission(String transmission) { this.transmission = transmission; }

        public String getEnginePowerKw() { return enginePowerKw; }
        public void setEnginePowerKw(String enginePowerKw) { this.enginePowerKw = enginePowerKw; }

        public String getCurbWeightKg() { return curbWeightKg; }
        public void setCurbWeightKg(String curbWeightKg) { this.curbWeightKg = curbWeightKg; }

        public String getFirstRegistrationDate() { return firstRegistrationDate; }
        public void setFirstRegistrationDate(String firstRegistrationDate) { this.firstRegistrationDate = firstRegistrationDate; }
    }

    private final Selectors selectors;

    public CarInfoHtmlParser(Selectors selectors) {
        this.selectors = selectors;
    }

    public CarInfoVehicleDto parse(String reg, Document doc) {
        // Add minimal validation so you notice breakage early.
        if (doc == null) {
            throw new CarInfoScraperException("PARSER_ERROR", "Document is null");
        }

        CarInfoVehicleDto dto = new CarInfoVehicleDto();
        dto.setRegistrationNumber(reg);

        dto.setMake(textBySelector(doc, selectors.getMake()));
        dto.setModel(textBySelector(doc, selectors.getModel()));
        dto.setVariant(textBySelector(doc, selectors.getVariant()));
        dto.setVin(textBySelector(doc, selectors.getVin()));

        dto.setModelYear(parseIntSafe(textBySelector(doc, selectors.getModelYear())));
        dto.setFuelType(textBySelector(doc, selectors.getFuelType()));
        dto.setTransmission(textBySelector(doc, selectors.getTransmission()));

        Integer powerKw = parseIntSafe(extractDigits(textBySelector(doc, selectors.getEnginePowerKw())));
        dto.setEnginePowerKw(powerKw);
        if (powerKw != null) {
            dto.setEnginePowerHp((int) Math.round(powerKw * 1.34102209)); // rough conversion
        }

        dto.setCurbWeightKg(parseIntSafe(extractDigits(textBySelector(doc, selectors.getCurbWeightKg()))));
        dto.setFirstRegistrationDate(parseDateSafe(textBySelector(doc, selectors.getFirstRegistrationDate())));

        return dto;
    }

    private static String textBySelector(Document doc, String cssSelector) {
        if (cssSelector == null || cssSelector.trim().isEmpty()) return null;
        Element el = doc.selectFirst(cssSelector);
        if (el == null) return null;
        String t = el.text();
        return (t == null || t.isBlank()) ? null : t.trim();
    }

    private static Integer parseIntSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String extractDigits(String s) {
        if (s == null) return null;
        String digits = s.replaceAll("[^0-9]", "");
        return digits.isBlank() ? null : digits;
    }

    private static LocalDate parseDateSafe(String s) {
        if (s == null || s.isBlank()) return null;

        // Adjust to what the page uses (e.g., yyyy-MM-dd or dd.MM.yyyy).
        // Keep it robust by trying a couple of formats.
        DateTimeFormatter[] formats = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        };

        for (DateTimeFormatter f : formats) {
            try {
                return LocalDate.parse(s.trim(), f);
            } catch (Exception ignored) { }
        }
        return null;
    }
}

9) Scraper client: CarInfoScraperClient
package com.meltacars.integration.carinfo.scraper;

import com.meltacars.integration.carinfo.CarInfoClient;
import com.meltacars.integration.carinfo.dto.CarInfoVehicleDto;
import com.meltacars.integration.common.PoliteRateLimiter;
import com.meltacars.integration.common.SimpleTtlCache;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CarInfoScraperClient implements CarInfoClient {

    private final CarInfoScraperProperties props;
    private final JsoupHtmlFetcher fetcher;
    private final CarInfoHtmlParser parser;
    private final PoliteRateLimiter rateLimiter;
    private final SimpleTtlCache<String, CarInfoVehicleDto> cache;

    public CarInfoScraperClient(
            CarInfoScraperProperties props,
            JsoupHtmlFetcher fetcher,
            CarInfoHtmlParser parser
    ) {
        this.props = props;
        this.fetcher = fetcher;
        this.parser = parser;
        this.rateLimiter = new PoliteRateLimiter(props.getMinDelayBetweenRequests());
        this.cache = new SimpleTtlCache<>(props.getCacheTtl());
    }

    @Override
    public CarInfoVehicleDto getVehicleByRegistrationNumber(String registrationNumber) {
        String reg = normalizeReg(registrationNumber);

        return cache.get(reg).orElseGet(() -> {
            CarInfoVehicleDto dto = fetchAndParse(reg);
            cache.put(reg, dto);
            return dto;
        });
    }

    private CarInfoVehicleDto fetchAndParse(String reg) {
        String url = buildUrl(reg);

        int attempts = Math.max(1, props.getMaxRetries() + 1);
        CarInfoScraperException last = null;

        for (int i = 1; i <= attempts; i++) {
            try {
                rateLimiter.acquire();
                Document doc = fetcher.get(url);
                return parser.parse(reg, doc);
            } catch (CarInfoScraperException e) {
                last = e;
                // simple retry on transient fetch errors
                if (!"FETCH_FAILED".equals(e.getCode()) || i == attempts) {
                    throw e;
                }
            }
        }
        throw last != null ? last : new CarInfoScraperException("SCRAPE_FAILED", "Unknown scraping failure");
    }

    private String buildUrl(String reg) {
        String baseUrl = requireNonBlank(props.getBaseUrl(), "carinfo.scraper.base-url");
        String template = requireNonBlank(props.getLookupPathTemplate(), "carinfo.scraper.lookup-path-template");

        String encodedReg = URLEncoder.encode(reg, StandardCharsets.UTF_8);
        String path = template.replace("{reg}", encodedReg);

        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private static String normalizeReg(String input) {
        if (input == null) throw new IllegalArgumentException("registrationNumber is required");
        return input.trim().replace(" ", "").toUpperCase();
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required configuration: " + name);
        }
        return value.trim();
    }
}

10) Spring config: CarInfoScraperConfig
package com.meltacars.integration.carinfo.scraper;

import com.meltacars.integration.carinfo.CarInfoClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CarInfoScraperProperties.class)
public class CarInfoScraperConfig {

    @Bean
    public JsoupHtmlFetcher jsoupHtmlFetcher(CarInfoScraperProperties props) {
        return new JsoupHtmlFetcher(props.getTimeout(), props.getUserAgent());
    }

    @Bean
    public CarInfoHtmlParser.Selectors carInfoSelectors() {
        CarInfoHtmlParser.Selectors s = new CarInfoHtmlParser.Selectors();

        // TODO: Fill these after inspecting HTML.
        // Examples (placeholders):
        // s.setMake("div.vehicle-header .make");
        // s.setModel("div.vehicle-header .model");
        // s.setVin("table.specs tr:has(th:contains(VIN)) td");
        // etc.

        return s;
    }

    @Bean
    public CarInfoHtmlParser carInfoHtmlParser(CarInfoHtmlParser.Selectors selectors) {
        return new CarInfoHtmlParser(selectors);
    }

    @Bean
    public CarInfoClient carInfoClient(
            CarInfoScraperProperties props,
            JsoupHtmlFetcher fetcher,
            CarInfoHtmlParser parser
    ) {
        return new CarInfoScraperClient(props, fetcher, parser);
    }
}

11) application.yml (PoC)
carinfo:
  scraper:
    base-url: "https://www.car.info"          # placeholder
    lookup-path-template: "/?q={reg}"         # placeholder - byt till rätt path när du sett URL-mönstret
    timeout: "PT8S"
    min-delay-between-requests: "PT0.8S"
    cache-ttl: "PT6H"
    max-retries: 1
    user-agent: "CMS-Bilar-PoC/0.1 (+internal prototype)"

Hur du går vidare (utan att jag behöver gissa HTML)

Ta en sida för ett exempel-regnr i webbläsaren.

Identifiera vilka element som innehåller fälten (make/model/vin/etc).

Sätt CSS-selectors i carInfoSelectors() i CarInfoScraperConfig.


En adress från car.info hämtas enligt "https://www.car.info/sv-se/license-plate/S/<REGNR>"
och det första vi vill leta efter är i header id=main_header . Där i ligger en a href länk med bilmodell och årsmodell.
Nästa intressanta sak ligger i div class="fs-6 top-specifications" -> sprow border-xl-top border-xxl-top ->                        span class="sptitle" med texten "Motor" och raden under den.




