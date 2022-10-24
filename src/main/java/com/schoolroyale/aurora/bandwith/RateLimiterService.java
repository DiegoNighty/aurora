package com.schoolroyale.aurora.bandwith;

import com.schoolroyale.aurora.auth.role.Role;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public boolean consume(String username, Role role) {
        var bucket = findBucket(username, role);

        return bucket.tryConsume(1);
    }

    public Bucket findBucket(String user, Role role) {
        return cache.computeIfAbsent(user, key -> newBucket(role));
    }

    private Bucket newBucket(Role role) {
        return Bucket.builder()
                .addLimit(
                        Bandwidth.classic(
                                role.maxRequests(),
                                Refill.intervally(
                                        role.maxRequests(),
                                        role.refillDuration()
                                )
                        )
                ).build();
    }

}
