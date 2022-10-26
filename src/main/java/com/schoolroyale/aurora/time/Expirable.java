package com.schoolroyale.aurora.time;

import java.time.Duration;
import java.util.Date;

public class Expirable<V> {

    private final Date expirationDate;
    private final V value;

    public Expirable(V value, Duration duration) {
        this.value = value;
        this.expirationDate = new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return expirationDate.before(new Date());
    }

    public long getTimeLeft() {
        return expirationDate.getTime() - System.currentTimeMillis();
    }

    public static <V> Expirable<V> of(V value, Duration duration) {
        return new Expirable<>(value, duration);
    }

}
