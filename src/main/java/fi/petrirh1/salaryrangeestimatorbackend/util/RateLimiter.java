package fi.petrirh1.salaryrangeestimatorbackend.util;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
public class RateLimiter {
    private final int maxRequests;
    private final Duration duration;
    private final Queue<Instant> requestTimestamps = new LinkedList<>();

    // Synchronized to be thread-safe in a multi-threaded environment
    public synchronized boolean allowRequest() {
        Instant now = Instant.now();

        // Remove timestamps outside the current window
        while (!requestTimestamps.isEmpty() &&
                requestTimestamps.peek().isBefore(now.minus(duration))) {
            requestTimestamps.poll();
        }

        // If under the limit, allow and record this request
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.add(now);
            return true;
        } else {
            // Limit exceeded
            return false;
        }
    }
}
