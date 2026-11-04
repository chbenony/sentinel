package dev.sentinel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.LongSupplier;

class TokenBucketTest {

    private long now = 0;

    @Test
    void newBucketAllowsFirstRequest() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);
        Decision decision = tokenBucket.tryConsume(1);

        assertTrue(decision.allowed());
        assertEquals(9, decision.remaining());
    }

    @Test
    void deniesWhenEmpty() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);

        for (int i = 0; i < 10; i++) {
            assertTrue(tokenBucket.tryConsume(1).allowed());
        }

        Decision decision = tokenBucket.tryConsume(1);
        assertFalse(decision.allowed());
        assertEquals(0, decision.remaining());
        assertEquals(Duration.ofMillis(500), decision.retryAfter());
    }

    @Test
    void refillsOverTime() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);

        for (int i = 0; i < 10; i++) {
            assertTrue(tokenBucket.tryConsume(1).allowed());
        }

        now += 500_000_000;
        Decision decision = tokenBucket.tryConsume(1);
        assertTrue(decision.allowed());
        assertEquals(0, decision.remaining());
    }

    @Test
    void neverExceedsCapacity() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);

        now += 100_000_000_000L;
        Decision decision = tokenBucket.tryConsume(1);

        assertTrue(decision.allowed());
        assertEquals(9, decision.remaining());
    }

    @Test
    void rejectsNegativeCost() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);
        assertThrows(IllegalArgumentException.class, () -> tokenBucket.tryConsume(-5));
    }

    @Test
    void rejectsCostGreaterThanCapacity() {
        LongSupplier clock = () -> now;

        TokenBucket tokenBucket = new TokenBucket(10, 2.0, clock);
        assertThrows(IllegalArgumentException.class, () -> tokenBucket.tryConsume(30));
    }

    @Test
    void rejectsZeroCapacity() {
        LongSupplier clock = () -> now;
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(0, 2.0, clock));
    }

    @Test
    void rejectsNegativeRate() {
        LongSupplier clock = () -> now;
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(10, -2.0, clock));

    }

    @Test
    void rejectsNANRate() {
        LongSupplier clock = () -> now;
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(10, Double.NaN, clock));
    }

    @Test
    void rejectsNullClock() {
        assertThrows(IllegalArgumentException.class, () -> new TokenBucket(10, 2.0, null));
    }
}
