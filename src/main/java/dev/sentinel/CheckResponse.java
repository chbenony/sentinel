package dev.sentinel;

public record CheckResponse(boolean allowed, int remaining, long retryAfterMillis) {

    static CheckResponse from(Decision decision){
        long retryAfterMillis = Math.ceilDiv(decision.retryAfter().toNanos(), 1_000_000);
        return new CheckResponse(decision.allowed(), decision.remaining(), retryAfterMillis);
    }
}
