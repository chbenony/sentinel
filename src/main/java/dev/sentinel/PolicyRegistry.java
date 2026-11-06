package dev.sentinel;

import java.util.Optional;

public interface PolicyRegistry {
    Optional<Policy> find(PolicyKey key);
}
