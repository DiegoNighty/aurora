package com.schoolroyale.aurora.time;

import java.time.Duration;
import java.time.Instant;

public class Expirable<V> {

    private final Instant expirationDate;
    private final V value;

    public Expirable(V value, Duration duration) {
        this.value = value;
        this.expirationDate = Time.when(duration);
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(Time.now());
    }

    public Duration getTimeLeft() {
        return Time.between(expirationDate);
    }

    public static <V> Expirable<V> of(V value, Duration duration) {
        return new Expirable<>(value, duration);
    }

}
