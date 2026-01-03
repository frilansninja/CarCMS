package se.meltastudio.cms.dto;

import java.util.List;

public class WorkTaskTemplateDTO {



    private Long id;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long repairCategoryId;

    private String repairCategoryName;

    private Integer estimatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRepairCategoryName() {
        return repairCategoryName;
    }

    public void setRepairCategoryName(String repairCategoryName) {
        this.repairCategoryName = repairCategoryName;
    }

    private List<RequiredPartDTO> requiredParts;
    public List<RequiredPartDTO> getRequiredParts() {
        return requiredParts;
    }

    public void setRequiredParts(List<RequiredPartDTO> requiredParts) {
        this.requiredParts = requiredParts;
    }



    public Long getRepairCategoryId() {
        return repairCategoryId;
    }

    public void setRepairCategoryId(Long repairCategoryId) {
        this.repairCategoryId = repairCategoryId;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

}
