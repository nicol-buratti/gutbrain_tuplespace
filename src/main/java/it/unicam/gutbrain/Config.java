package it.unicam.gutbrain;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class Config {
    public static final Map<String, Integer> env;

    static {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = Files.newInputStream(new File("env.yaml").toPath());
            env = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration: " + e.getMessage(), e);
        }
    }
}
