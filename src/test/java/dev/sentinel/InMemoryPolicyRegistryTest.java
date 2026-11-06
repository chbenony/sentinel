package dev.sentinel;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPolicyRegistryTest {
    @Test
    void knownKeyReturnsPolicy(){

        PolicyKey policyKey = new PolicyKey("app_1", "res_1");
        Policy policy = new Policy(2, 2.0);


        InMemoryPolicyRegistry policyRegistry = new InMemoryPolicyRegistry(Map.of(policyKey, policy));
        Optional<Policy> actual = policyRegistry.find(policyKey);
        Optional<Policy> expected = Optional.of(policy);

        assertEquals(expected, actual);

    }

    @Test
    void unknownKeyReturnsEmpty() {
        PolicyKey key = new PolicyKey("app_1", "res_1");
        Policy policy = new Policy(1, 1.0);

        InMemoryPolicyRegistry registry = new InMemoryPolicyRegistry(Map.of(key, policy));
        Optional<Policy> expected = Optional.empty();
        Optional<Policy> actual = registry.find(new PolicyKey("rando", "res_1"));

        assertEquals(expected, actual);
    }

    @Test
    void registryIgnoresLaterChangesToCallersMap(){
        Map<PolicyKey, Policy> policies = new HashMap<>();

        PolicyKey policyKey = new PolicyKey("app_1", "res_1");
        Policy policy = new Policy(2, 2.0);

        policies.put(policyKey, policy);
        InMemoryPolicyRegistry registry = new InMemoryPolicyRegistry(policies);
        policies.remove(policyKey);

        Optional<Policy> actual = registry.find(policyKey);
        Optional<Policy> expected = Optional.of(policy);

        assertEquals(expected, actual);
    }
}
