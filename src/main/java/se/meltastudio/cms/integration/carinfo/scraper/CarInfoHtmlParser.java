package se.meltastudio.cms.integration.carinfo.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import se.meltastudio.cms.integration.carinfo.dto.CarInfoVehicleDto;

@Component
public class CarInfoHtmlParser {

    /**
     * Configuration class for CSS selectors.
     * Makes it easy to adjust selectors without changing the parsing logic.
     */
    public static class Selectors {
        private final String makeSelector;
        private final String modelSelector;
        private final String variantSelector;
        private final String vinSelector;
        private final String modelYearSelector;
        private final String fuelTypeSelector;
        private final String transmissionSelector;
        private final String enginePowerKwSelector;
        private final String curbWeightKgSelector;
        private final String firstRegistrationDateSelector;

        public Selectors(
                String makeSelector,
                String modelSelector,
                String variantSelector,
                String vinSelector,
                String modelYearSelector,
                String fuelTypeSelector,
                String transmissionSelector,
                String enginePowerKwSelector,
                String curbWeightKgSelector,
                String firstRegistrationDateSelector
        ) {
            this.makeSelector = makeSelector;
            this.modelSelector = modelSelector;
            this.variantSelector = variantSelector;
            this.vinSelector = vinSelector;
            this.modelYearSelector = modelYearSelector;
            this.fuelTypeSelector = fuelTypeSelector;
            this.transmissionSelector = transmissionSelector;
            this.enginePowerKwSelector = enginePowerKwSelector;
            this.curbWeightKgSelector = curbWeightKgSelector;
            this.firstRegistrationDateSelector = firstRegistrationDateSelector;
        }

        public String getMakeSelector() { return makeSelector; }
        public String getModelSelector() { return modelSelector; }
        public String getVariantSelector() { return variantSelector; }
        public String getVinSelector() { return vinSelector; }
        public String getModelYearSelector() { return modelYearSelector; }
        public String getFuelTypeSelector() { return fuelTypeSelector; }
        public String getTransmissionSelector() { return transmissionSelector; }
        public String getEnginePowerKwSelector() { return enginePowerKwSelector; }
        public String getCurbWeightKgSelector() { return curbWeightKgSelector; }
        public String getFirstRegistrationDateSelector() { return firstRegistrationDateSelector; }
    }

    /**
     * Parses the HTML document to extract vehicle information.
     *
     * @param doc HTML document from car.info
     * @param selectors CSS selector configuration
     * @param registrationNumber the registration number that was searched
     * @return parsed vehicle DTO
     */
    public CarInfoVehicleDto parse(Document doc, Selectors selectors, String registrationNumber) {
        CarInfoVehicleDto dto = new CarInfoVehicleDto();
        dto.setRegistrationNumber(registrationNumber);

        // Parse make/model/year from main header (often combined like "Volvo V70 2015")
        String mainHeader = selectText(doc, selectors.getMakeSelector());
        if (mainHeader != null) {
            parseMakeModelYear(mainHeader, dto);
        }

        // Parse variant
        String variant = selectText(doc, selectors.getVariantSelector());
        dto.setVariant(variant);

        // Parse VIN - look for element containing "VIN" and extract the value
        String vin = selectAttributeValue(doc, selectors.getVinSelector(), "VIN");
        dto.setVin(vin);

        // Parse fuel type
        String fuelType = selectAttributeValue(doc, selectors.getFuelTypeSelector(), "Bränsle");
        dto.setFuelType(fuelType);

        // Parse transmission
        String transmission = selectAttributeValue(doc, selectors.getTransmissionSelector(), "Växellåda");
        dto.setTransmission(transmission);

        // Parse engine power (kW)
        String enginePowerKwStr = selectAttributeValue(doc, selectors.getEnginePowerKwSelector(), "Effekt");
        Integer enginePowerKw = parseInteger(enginePowerKwStr);
        dto.setEnginePowerKw(enginePowerKw);

        // Convert to HP if we have kW
        if (enginePowerKw != null) {
            dto.setEnginePowerHp((int) Math.round(enginePowerKw * 1.35962));
        }

        // Parse curb weight
        String curbWeightKgStr = selectAttributeValue(doc, selectors.getCurbWeightKgSelector(), "Tjänstevikt");
        dto.setCurbWeightKg(parseInteger(curbWeightKgStr));

        // Parse first registration date
        String firstRegDateStr = selectAttributeValue(doc, selectors.getFirstRegistrationDateSelector(), "Första registrering");
        dto.setFirstRegistrationDate(parseLocalDate(firstRegDateStr));

        return dto;
    }

    /**
     * Parses make, model, and year from a combined string like "Volvo V70 2015".
     * Assumes format: [Make] [Model] [Year] where year is 4 digits at the end.
     */
    private void parseMakeModelYear(String text, CarInfoVehicleDto dto) {
        if (text == null || text.isBlank()) {
            return;
        }

        // Extract year (4 digits, typically at the end)
        String yearPattern = "\\b(19|20)\\d{2}\\b";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(yearPattern);
        java.util.regex.Matcher matcher = pattern.matcher(text);

        Integer year = null;
        if (matcher.find()) {
            year = Integer.parseInt(matcher.group());
            dto.setModelYear(year);
            // Remove year from text for further parsing
            text = text.replaceFirst(yearPattern, "").trim();
        }

        // Split remaining text into words
        String[] parts = text.split("\\s+");

        if (parts.length > 0) {
            // First word is typically the make
            dto.setMake(parts[0]);

            // Remaining words form the model
            if (parts.length > 1) {
                String model = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                dto.setModel(model);
            }
        }
    }

    /**
     * Selects a value from a specifications section.
     * Looks for an element containing the label, then extracts the value.
     * For example: finds "Bränsle: Diesel" and returns "Diesel".
     */
    private String selectAttributeValue(Document doc, String selector, String label) {
        if (selector == null || selector.isBlank()) {
            return null;
        }

        Elements elements = doc.select(selector);
        for (Element element : elements) {
            String text = element.text();
            if (text.contains(label)) {
                // Extract value after the label
                int index = text.indexOf(label);
                String afterLabel = text.substring(index + label.length()).trim();
                // Remove leading colon or other separators
                afterLabel = afterLabel.replaceFirst("^[:\\-\\s]+", "").trim();
                // Take first part before any comma or other separator
                String value = afterLabel.split("[,;]")[0].trim();
                return value.isEmpty() ? null : value;
            }
        }

        return null;
    }

    /**
     * Safely selects text from document using CSS selector.
     * Returns null if element not found.
     */
    private String selectText(Document doc, String selector) {
        if (selector == null || selector.isBlank()) {
            return null;
        }

        Elements elements = doc.select(selector);
        if (elements.isEmpty()) {
            return null;
        }

        String text = elements.first().text().trim();
        return text.isEmpty() ? null : text;
    }

    /**
     * Parses integer from string, extracting only digits.
     * Returns null if parsing fails.
     */
    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            // Extract only digits from the string
            String digits = value.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) {
                return null;
            }
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses LocalDate from string.
     * Supports common date formats: yyyy-MM-dd, yyyy/MM/dd, dd-MM-yyyy, dd/MM/yyyy
     * Returns null if parsing fails.
     */
    private java.time.LocalDate parseLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // Try different common date formats
        String[] patterns = {
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd-MM-yyyy",
            "dd/MM/yyyy",
            "yyyy.MM.dd",
            "dd.MM.yyyy"
        };

        for (String pattern : patterns) {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
                return java.time.LocalDate.parse(value, formatter);
            } catch (Exception e) {
                // Try next pattern
            }
        }

        return null;
    }
}
