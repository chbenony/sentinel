package dev.sentinel;

public record CheckRequest(String principal, String application, String resource, int cost) {
}
