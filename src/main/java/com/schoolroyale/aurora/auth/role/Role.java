package com.schoolroyale.aurora.auth.role;

import java.time.Duration;
import java.util.Comparator;

public enum Role {

    ROLE_USER(1, 6, Duration.ofMinutes(10)),
    ROLE_ADMIN(100, 1000, Duration.ofMinutes(5));

    private final int priority;
    private final int maxRequests;
    private final Duration refillDuration;

    Role(int priority, int maxRequests, Duration refillDuration) {
        this.priority = priority;
        this.maxRequests = maxRequests;
        this.refillDuration = refillDuration;
    }

    public int maxRequests() {
        return maxRequests;
    }

    public Duration refillDuration() {
        return refillDuration;
    }

    public int priority() {
        return priority;
    }

    public static Comparator<Role> comparator() {
        return Comparator.comparingInt(Role::priority);
    }

}
