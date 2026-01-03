package se.meltastudio.cms.parts.api;

import java.math.BigDecimal;
import java.util.List;

/**
 * Detailed information about a specific part.
 * Extends PartOffer with additional details like specifications and compatibility.
 */
public class PartDetails extends PartOffer {

    private List<String> specifications;
    private List<String> compatibleVehicles;
    private String manufacturerPartNumber;
    private String weight;
    private String dimensions;
    private List<String> additionalImageUrls;

    public PartDetails() {
        super();
    }

    public List<String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<String> specifications) {
        this.specifications = specifications;
    }

    public List<String> getCompatibleVehicles() {
        return compatibleVehicles;
    }

    public void setCompatibleVehicles(List<String> compatibleVehicles) {
        this.compatibleVehicles = compatibleVehicles;
    }

    public String getManufacturerPartNumber() {
        return manufacturerPartNumber;
    }

    public void setManufacturerPartNumber(String manufacturerPartNumber) {
        this.manufacturerPartNumber = manufacturerPartNumber;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getAdditionalImageUrls() {
        return additionalImageUrls;
    }

    public void setAdditionalImageUrls(List<String> additionalImageUrls) {
        this.additionalImageUrls = additionalImageUrls;
    }
}
