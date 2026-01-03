package se.meltastudio.cms.dto;

public class RequiredPartDTO {
    private String description;
    private String partNumber;
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
