package se.meltastudio.cms.parts.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierMetrics.
 * Verifies metrics tracking and calculations.
 */
class SupplierMetricsTest {

    private SupplierMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new SupplierMetrics();
    }

    @Test
    void recordSupplierCall_ShouldIncrementCallCount() {
        // When
        metrics.recordSupplierCall("SUPPLIER_1");
        metrics.recordSupplierCall("SUPPLIER_1");
        metrics.recordSupplierCall("SUPPLIER_2");

        // Then
        assertEquals(2, metrics.getCallCount("SUPPLIER_1"));
        assertEquals(1, metrics.getCallCount("SUPPLIER_2"));
    }

    @Test
    void recordSupplierFailure_ShouldIncrementFailureCount() {
        // When
        metrics.recordSupplierFailure("SUPPLIER_1");
        metrics.recordSupplierFailure("SUPPLIER_1");

        // Then
        assertEquals(2, metrics.getFailureCount("SUPPLIER_1"));
    }

    @Test
    void getCallCount_UnknownSupplier_ShouldReturnZero() {
        // When
        long count = metrics.getCallCount("UNKNOWN");

        // Then
        assertEquals(0, count);
    }

    @Test
    void recordCacheHit_ShouldIncrementHitCount() {
        // When
        metrics.recordCacheHit();
        metrics.recordCacheHit();

        // Then
        assertEquals(100.0, metrics.getCacheHitRate(), 0.01);
    }

    @Test
    void recordCacheMiss_ShouldDecreaseCacheHitRate() {
        // When
        metrics.recordCacheHit();
        metrics.recordCacheMiss();

        // Then
        assertEquals(50.0, metrics.getCacheHitRate(), 0.01);
    }

    @Test
    void getCacheHitRate_NoRequests_ShouldReturnZero() {
        // When
        double hitRate = metrics.getCacheHitRate();

        // Then
        assertEquals(0.0, hitRate, 0.01);
    }

    @Test
    void getCacheHitRate_AllHits_ShouldReturn100() {
        // When
        metrics.recordCacheHit();
        metrics.recordCacheHit();
        metrics.recordCacheHit();

        // Then
        assertEquals(100.0, metrics.getCacheHitRate(), 0.01);
    }

    @Test
    void getCacheHitRate_AllMisses_ShouldReturnZero() {
        // When
        metrics.recordCacheMiss();
        metrics.recordCacheMiss();
        metrics.recordCacheMiss();

        // Then
        assertEquals(0.0, metrics.getCacheHitRate(), 0.01);
    }

    @Test
    void mixedMetrics_ShouldTrackCorrectly() {
        // When
        metrics.recordSupplierCall("SUPPLIER_1");
        metrics.recordSupplierCall("SUPPLIER_1");
        metrics.recordSupplierFailure("SUPPLIER_1");

        metrics.recordSupplierCall("SUPPLIER_2");
        metrics.recordSupplierFailure("SUPPLIER_2");

        metrics.recordCacheHit();
        metrics.recordCacheHit();
        metrics.recordCacheMiss();

        // Then
        assertEquals(2, metrics.getCallCount("SUPPLIER_1"));
        assertEquals(1, metrics.getFailureCount("SUPPLIER_1"));
        assertEquals(1, metrics.getCallCount("SUPPLIER_2"));
        assertEquals(1, metrics.getFailureCount("SUPPLIER_2"));
        assertEquals(66.67, metrics.getCacheHitRate(), 0.01);
    }

    @Test
    void logMetricsSummary_ShouldNotThrowException() {
        // Given
        metrics.recordSupplierCall("SUPPLIER_1");
        metrics.recordSupplierFailure("SUPPLIER_1");
        metrics.recordCacheHit();

        // When & Then - should not throw
        assertDoesNotThrow(() -> metrics.logMetricsSummary());
    }
}
