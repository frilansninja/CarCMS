package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.LogManualChange;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogManualChangeRepository extends JpaRepository<LogManualChange, Long> {
    List<LogManualChange> findByChangedAtBefore(LocalDateTime cutoffDate);
}

