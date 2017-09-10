package config;

import java.nio.file.Path;

public class Config {
    public final String username;
    public final Path directory;

    public Config(String username, Path directory) {
        this.username = username;
        this.directory = directory;
    }
}
