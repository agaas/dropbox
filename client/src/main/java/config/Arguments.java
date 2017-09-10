package config;

import org.kohsuke.args4j.Option;

public class Arguments {

    @Option(name="--user", usage="user name", required = true)
    public String username;
    @Option(name="--path", usage="path with dropbox files", required = true)
    public String path;
}
