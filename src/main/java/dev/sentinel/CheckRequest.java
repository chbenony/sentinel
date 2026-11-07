package dev.sentinel;

public record CheckRequest(String principal, String application, String resource, int cost) {
    public CheckRequest{
        if (principal == null || principal.isBlank()) {
            throw new IllegalArgumentException("principal is required");
        }
        if (application == null || application.isBlank()){
            throw new IllegalArgumentException("application is required");
        }
        if (resource == null || resource.isBlank()){
            throw new IllegalArgumentException("resource is required");
        }
        if (cost < 1) {
            throw new IllegalArgumentException("cost is required");
        }
    }
}
