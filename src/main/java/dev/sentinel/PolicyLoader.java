package dev.sentinel;

import tools.jackson.dataformat.yaml.YAMLMapper;

import java.nio.file.Path;

public class PolicyLoader {
    private final Path filePath;
    private final YAMLMapper yamlMapper;

    public PolicyLoader(Path filePath, YAMLMapper yamlMapper) {
        this.filePath = filePath;
        this.yamlMapper = yamlMapper;
    }

    public PolicyConfig load() {
        return yamlMapper.readValue(filePath, PolicyConfig.class);
    }
}
