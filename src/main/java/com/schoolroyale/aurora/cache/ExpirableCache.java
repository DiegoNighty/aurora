package com.schoolroyale.aurora.cache;

import com.schoolroyale.aurora.time.Expirable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class ExpirableCache<K, V> implements Cache<K, V> {

    private final Map<K, Expirable<V>> cache;
    private final Duration expiration;

    public static <K, V> ExpirableCache<K, V> of(Map<K, Expirable<V>> cache, Duration expiration) {
        return new ExpirableCache<>(cache, expiration);
    }

    public ExpirableCache(Map<K, Expirable<V>> cache, Duration expiration) {
        this.cache = cache;
        this.expiration = expiration;
    }

    @Override
    public Optional<V> get(K key) {
        Expirable<V> value = cache.get(key);

        if (value == null) {
            return Optional.empty();
        }

        if (value.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }

        return Optional.of(value.getValue());
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, Expirable.of(value, expiration));
    }

    @Override
    public Optional<Expirable<V>> getExpirable(K key) {
        return Optional.of(cache.get(key));
    }

    @Override
    public Map<K, Expirable<V>> asMap() {
        return cache;
    }
}
