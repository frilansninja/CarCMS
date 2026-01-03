package se.meltastudio.cms.integration.common;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTtlCache<K, V> {

    private static final class Entry<V> {
        private final V value;
        private final Instant expiresAt;
        private Entry(V value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private final ConcurrentHashMap<K, Entry<V>> store = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Clock clock;

    public SimpleTtlCache(Duration ttl) {
        this(ttl, Clock.systemUTC());
    }

    public SimpleTtlCache(Duration ttl, Clock clock) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("TTL must be > 0");
        }
        this.ttl = ttl;
        this.clock = clock;
    }

    public Optional<V> get(K key) {
        Entry<V> entry = store.get(key);
        if (entry == null) return Optional.empty();

        if (Instant.now(clock).isAfter(entry.expiresAt)) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(entry.value);
    }

    public void put(K key, V value) {
        store.put(key, new Entry<>(value, Instant.now(clock).plus(ttl)));
    }

    public void invalidate(K key) {
        store.remove(key);
    }

    public void clear() {
        store.clear();
    }
}
