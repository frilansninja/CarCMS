package se.meltastudio.cms.integration.common;

import java.time.Duration;

public class PoliteRateLimiter {

    private final long minDelayMillis;
    private long nextAllowedAtMillis;

    public PoliteRateLimiter(Duration minDelayBetweenRequests) {
        if (minDelayBetweenRequests == null || minDelayBetweenRequests.isNegative()) {
            throw new IllegalArgumentException("minDelayBetweenRequests must be >= 0");
        }
        this.minDelayMillis = minDelayBetweenRequests.toMillis();
        this.nextAllowedAtMillis = 0L;
    }

    /**
     * Blocks the current thread until a request is allowed.
     * Intended for backend PoC usage.
     */
    public synchronized void acquire() {
        long now = System.currentTimeMillis();
        long waitMillis = Math.max(0L, nextAllowedAtMillis - now);

        if (waitMillis > 0L) {
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while rate limiting", e);
            }
        }

        long after = System.currentTimeMillis();
        nextAllowedAtMillis = after + minDelayMillis;
    }
}
