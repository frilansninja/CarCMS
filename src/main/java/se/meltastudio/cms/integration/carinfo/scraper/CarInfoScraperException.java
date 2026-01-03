package se.meltastudio.cms.integration.carinfo.scraper;

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
