package dev.sentinel;

import java.util.Map;
import java.util.Optional;

public class InMemoryPolicyRegistry implements PolicyRegistry{
    private final Map<PolicyKey, Policy> policies;

    public InMemoryPolicyRegistry(Map<PolicyKey, Policy> policies) {
        this.policies = Map.copyOf(policies);
    }

    @Override
    public Optional<Policy> find(PolicyKey key) {
        Policy existingPolicy = policies.get(key);
        return Optional.ofNullable(existingPolicy);
    }
}
