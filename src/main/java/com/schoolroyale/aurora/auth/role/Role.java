package com.schoolroyale.aurora.auth.role;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.Comparator;

@Document("api-role")
public record Role(String name, int maxRequests, int priority, Duration refillDuration) {

    public static final Comparator<Role> BY_PRIORITY = Comparator.comparingInt(Role::priority);

}
