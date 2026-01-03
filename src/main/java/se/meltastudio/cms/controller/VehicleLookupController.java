package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.integration.carinfo.CarInfoClient;
import se.meltastudio.cms.integration.carinfo.dto.CarInfoVehicleDto;
import se.meltastudio.cms.integration.carinfo.scraper.CarInfoScraperException;

@RestController
@RequestMapping("/api/vehicles/lookup")
public class VehicleLookupController {

    private final CarInfoClient carInfoClient;

    public VehicleLookupController(CarInfoClient carInfoClient) {
        this.carInfoClient = carInfoClient;
    }

    /**
     * Lookup vehicle information by registration number from car.info.
     * This endpoint uses web scraping with rate limiting and caching.
     *
     * @param registrationNumber the registration number to lookup
     * @return vehicle information DTO
     */
    @GetMapping("/{registrationNumber}")
    public ResponseEntity<?> lookupVehicle(@PathVariable String registrationNumber) {
        try {
            CarInfoVehicleDto vehicleInfo = carInfoClient.getVehicleByRegistrationNumber(registrationNumber);
            return ResponseEntity.ok(vehicleInfo);
        } catch (CarInfoScraperException e) {
            return ResponseEntity
                    .status(getHttpStatus(e.getCode()))
                    .body(new ErrorResponse(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UNKNOWN_ERROR", "An unexpected error occurred"));
        }
    }

    private HttpStatus getHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "INVALID_INPUT" -> HttpStatus.BAD_REQUEST;
            case "CONFIG_ERROR" -> HttpStatus.INTERNAL_SERVER_ERROR;
            case "NETWORK_ERROR" -> HttpStatus.BAD_GATEWAY;
            case "SCRAPE_ERROR" -> HttpStatus.BAD_GATEWAY;
            case "INTERRUPTED" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private record ErrorResponse(String code, String message) {}
}
