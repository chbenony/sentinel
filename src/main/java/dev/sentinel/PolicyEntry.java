package dev.sentinel;

public record PolicyEntry(String application, String resource, int capacity, double refillRate) {
}
