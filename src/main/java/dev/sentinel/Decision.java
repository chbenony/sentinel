package dev.sentinel;
import java.time.Duration;

public record Decision(boolean allowed, int remaining, Duration retryAfter) {
}
