package dev.sentinel;

import java.time.Duration;
import java.util.function.LongSupplier;

public class TokenBucket {
    private final int capacity;
    private final double refillRate;
    private final LongSupplier clock;
    private double currentTokens;
    private long lastRefillTime;

    public TokenBucket(int capacity, double refillRate, LongSupplier clock) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (refillRate <= 0 || !Double.isFinite(refillRate)){
            throw new IllegalArgumentException("refillRate must be positive and finite");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock cannot be null");
        }

        this.capacity = capacity;
        this.refillRate = refillRate;
        this.clock = clock;
        this.currentTokens = capacity;
        this.lastRefillTime = clock.getAsLong();
    }

    public Decision tryConsume(int cost) {
        if (cost <= 0) {
            throw new IllegalArgumentException("invalid cost");
        }

        if (cost > capacity) {
            throw new IllegalArgumentException("cost is greater than capacity");
        }

        long now = clock.getAsLong();

        long elapsed = now - lastRefillTime;
        double tokensToAdd = (elapsed / 1e9) * refillRate;
        currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
        lastRefillTime = now;

        if (currentTokens >= cost) {
            currentTokens -= cost;
            return new Decision(true, (int) currentTokens, Duration.ZERO);
        } else {
            double missing = cost - currentTokens;
            double seconds = missing / refillRate;
            double nanos = seconds * 1e9;

            Duration remaining = Duration.ofNanos((long) Math.ceil(nanos));
            return new Decision(false, (int) currentTokens, remaining);
        }
    }
}
