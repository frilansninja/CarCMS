package se.meltastudio.cms.integration.carinfo.scraper;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.meltastudio.cms.integration.carinfo.dto.CarInfoVehicleDto;
import se.meltastudio.cms.integration.common.PoliteRateLimiter;
import se.meltastudio.cms.integration.common.SimpleTtlCache;

@Configuration
@EnableConfigurationProperties(CarInfoScraperProperties.class)
public class CarInfoScraperConfig {

    @Bean
    public SimpleTtlCache<String, CarInfoVehicleDto> carInfoCache(CarInfoScraperProperties properties) {
        return new SimpleTtlCache<>(properties.getCacheTtl());
    }

    @Bean
    public PoliteRateLimiter carInfoRateLimiter(CarInfoScraperProperties properties) {
        return new PoliteRateLimiter(properties.getMinDelayBetweenRequests());
    }

    @Bean
    public CarInfoHtmlParser.Selectors carInfoSelectors() {
        // Configure CSS selectors for car.info
        // These can be externalized to application.yml if needed in the future
        return new CarInfoHtmlParser.Selectors(
                // Make selector - from main header link text, first part before space
                "header#main_header a.link-dark",
                // Model selector - from main header link text, second part
                "header#main_header a.link-dark",
                // Variant selector - Skip variant to avoid capturing entire spec section
                "",
                // VIN selector - typically in specifications section
                "div.fs-6.top-specifications",
                // Model year selector - from main header or specifications
                "header#main_header a.link-dark",
                // Fuel type selector - from specifications
                "div.fs-6.top-specifications",
                // Transmission selector - from specifications
                "div.fs-6.top-specifications",
                // Engine power kW selector - from specifications
                "div.fs-6.top-specifications",
                // Curb weight selector - from specifications
                "div.fs-6.top-specifications",
                // First registration date selector - from specifications
                "div.fs-6.top-specifications"
        );
    }
}
