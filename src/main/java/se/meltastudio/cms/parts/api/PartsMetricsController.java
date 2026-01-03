package se.meltastudio.cms.parts.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.meltastudio.cms.parts.service.SupplierMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for viewing spare parts metrics.
 * Intended for admin/monitoring purposes.
 */
@RestController
@RequestMapping("/api/parts/metrics")
public class PartsMetricsController {

    private final SupplierMetrics metrics;

    public PartsMetricsController(SupplierMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Get current metrics summary.
     * Includes cache statistics and supplier performance.
     */
    @GetMapping
    public ResponseEntity<?> getMetrics(@AuthenticationPrincipal UserDetails userDetails) {
        // Log metrics to console/logs
        metrics.logMetricsSummary();

        // Return metrics as JSON
        Map<String, Object> response = new HashMap<>();
        response.put("cache", Map.of(
                "hitRate", String.format("%.2f%%", metrics.getCacheHitRate())
        ));
        response.put("message", "Metrics logged to console. See application logs for details.");

        return ResponseEntity.ok(response);
    }
}
