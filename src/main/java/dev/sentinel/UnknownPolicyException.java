package dev.sentinel;

public class UnknownPolicyException extends RuntimeException {
    public UnknownPolicyException(PolicyKey policyKey) {
        super("Unknown policy for key " + policyKey);
    }
}
