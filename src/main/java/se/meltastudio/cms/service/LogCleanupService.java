package se.meltastudio.cms.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.LogManualChange;
import se.meltastudio.cms.repository.LogManualChangeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogCleanupService {
    private final LogManualChangeRepository logRepository;

    public LogCleanupService(LogManualChangeRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Scheduled(cron = "0 0 3 * * *") // Runs daily at 03:00 AM
    public void cleanupOldLogs() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(365);
        List<LogManualChange> oldLogs = logRepository.findByChangedAtBefore(cutoffDate);
        logRepository.deleteAll(oldLogs);
    }
}