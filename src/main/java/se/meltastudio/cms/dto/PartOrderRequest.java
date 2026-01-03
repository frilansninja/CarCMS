package se.meltastudio.cms.dto;

import lombok.Data;

public class PartOrderRequest {
    private Long supplierId;

    private Long articleId;

    private int quantity;

    private Long workOrderId;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    @Override
    public String toString() {
        return "PartOrderRequest{" +
                "supplierId=" + supplierId +
                ", articleId=" + articleId +
                ", quantity=" + quantity +
                ", workOrderId=" + workOrderId +
                '}';
    }
}
