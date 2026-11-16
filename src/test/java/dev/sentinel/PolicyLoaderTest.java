package dev.sentinel;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolicyLoaderTest {
    //tempDir is the temporary directory for testing
    @TempDir
    Path tempDir;

    @Test
    void loadsPolicyEntriesFromYaml() throws IOException {
        String yaml = """
                policies:
                  - application: sift
                    resource: chat
                    capacity: 100
                    refillRate: 10.0
                """;
        Path filePath = tempDir.resolve("policies.yml");
        Files.writeString(filePath, yaml);
        YAMLMapper yamlMapper = YAMLMapper.builder().build();
        PolicyLoader policyLoader = new PolicyLoader(filePath, yamlMapper);
        PolicyConfig config = policyLoader.load();

        assertEquals(1, config.policies().size());
        assertEquals("sift", config.policies().getFirst().application());
        assertEquals("chat", config.policies().getFirst().resource());
        assertEquals(100, config.policies().getFirst().capacity());
        assertEquals(10.0, config.policies().getFirst().refillRate());
    }

}
