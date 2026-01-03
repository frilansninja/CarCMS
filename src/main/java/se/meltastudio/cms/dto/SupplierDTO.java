package se.meltastudio.cms.dto;

import java.util.List;

public class SupplierDTO {
    private Long id;
    private String name;
    private String contactEmail;

    public List<ArticleDTO> getArticles() {
        return articles;
    }

    private List<ArticleDTO> articles;


    private String contactPhone;
    private String address;

    public SupplierDTO() {}
    public SupplierDTO(Long id, String name, String contactEmail, String contactPhone, String address, List<ArticleDTO> articles) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
        this.articles = articles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setArticles(List<ArticleDTO> articles) {
        this.articles = articles;
    }
}
