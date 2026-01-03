package se.meltastudio.cms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import se.meltastudio.cms.model.Booking;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private String title;
    private String mechanicName; // 🔹 Endast mekanikerns namn
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private String categoryColor;

    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.title = booking.getTitle();
        this.mechanicName = booking.getMechanic().getUsername(); // 🔹 Endast namnet
        this.startTime = booking.getStartTime();
        this.endTime = booking.getEndTime();
        this.categoryColor = booking.getCategoryColor();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }
}
