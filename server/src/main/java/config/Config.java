package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Config {
    public final List<Path> paths;

    public Config(Path path) {
        try {
            ServerDirectoriesSeeker serverDirectoriesSeeker = new ServerDirectoriesSeeker();
            Files.walkFileTree(path, serverDirectoriesSeeker);
            serverDirectoriesSeeker.directories.remove(path);
            this.paths = serverDirectoriesSeeker.directories;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
