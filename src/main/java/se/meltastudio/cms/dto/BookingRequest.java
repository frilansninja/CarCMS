package se.meltastudio.cms.dto;

import java.time.LocalDateTime;

public class BookingRequest {
    private String title;
    private Long mechanicId;  // 🔹 Endast ID, inte hela objektet!
    private String categoryColor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 🔹 Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }

    public String getCategoryColor() { return categoryColor; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
