package com.schoolroyale.aurora.time;

import java.time.Duration;
import java.time.Instant;

public class Time {

    public static Instant now() {
        return Instant.now();
    }

    public static Instant when(Duration duration) {
        return Instant.now().plus(duration);
    }

    public static Duration between(Instant end) {
        return Duration.between(Time.now(), end);
    }

}
