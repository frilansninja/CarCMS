package se.meltastudio.cms.parts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple metrics tracking for supplier operations.
 * Tracks call counts, failures, and cache hits per supplier.
 */
@Component
public class SupplierMetrics {

    private static final Logger log = LoggerFactory.getLogger(SupplierMetrics.class);

    private final ConcurrentHashMap<String, AtomicLong> callCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> failureCounts = new ConcurrentHashMap<>();
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    /**
     * Record a successful supplier API call.
     */
    public void recordSupplierCall(String supplierCode) {
        callCounts.computeIfAbsent(supplierCode, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Record a failed supplier API call.
     */
    public void recordSupplierFailure(String supplierCode) {
        failureCounts.computeIfAbsent(supplierCode, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Record a cache hit.
     */
    public void recordCacheHit() {
        cacheHits.incrementAndGet();
    }

    /**
     * Record a cache miss.
     */
    public void recordCacheMiss() {
        cacheMisses.incrementAndGet();
    }

    /**
     * Get call count for a supplier.
     */
    public long getCallCount(String supplierCode) {
        AtomicLong count = callCounts.get(supplierCode);
        return count != null ? count.get() : 0;
    }

    /**
     * Get failure count for a supplier.
     */
    public long getFailureCount(String supplierCode) {
        AtomicLong count = failureCounts.get(supplierCode);
        return count != null ? count.get() : 0;
    }

    /**
     * Get cache hit rate (0-100%).
     */
    public double getCacheHitRate() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total * 100;
    }

    /**
     * Log current metrics summary.
     */
    public void logMetricsSummary() {
        log.info("=== Supplier Metrics Summary ===");
        log.info("Cache: hits={}, misses={}, hit-rate={:.2f}%",
                cacheHits.get(), cacheMisses.get(), getCacheHitRate());

        callCounts.forEach((supplier, calls) -> {
            long failures = getFailureCount(supplier);
            long successes = calls.get() - failures;
            double failureRate = calls.get() == 0 ? 0.0 : (double) failures / calls.get() * 100;
            log.info("Supplier {}: calls={}, successes={}, failures={}, failure-rate={:.2f}%",
                    supplier, calls.get(), successes, failures, failureRate);
        });
    }
}
