package com.schoolroyale.aurora.time;

import java.time.Duration;
import java.util.Date;

public class Time {

    public static Date now() {
        return new Date();
    }

    public static Date when(Duration duration) {
        return new Date(now().getTime() + duration.toMillis());
    }

}
