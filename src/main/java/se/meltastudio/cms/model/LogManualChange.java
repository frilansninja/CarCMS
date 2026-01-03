package se.meltastudio.cms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_manual_changes")
@Getter
@Setter
public class LogManualChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityType;
    private Long entityId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private String changedBy;
}