package se.meltastudio.cms.dto;

import java.util.List;

public class WorkTaskDTO {
    private Long id;
    private String description;
    private Long statusId;

    private String statusName;
    private Long templateId;

    private List<WorkTaskArticleDTO> articles;

    private Long workOrderId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }



    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<WorkTaskArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<WorkTaskArticleDTO> articles) {
        this.articles = articles;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }
}