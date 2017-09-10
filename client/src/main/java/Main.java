import config.Arguments;
import config.Config;
import files.FilesSeekerJob;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){

        LOG.info("Client application start.");

        try {
            LOG.info("Getting application configuration.");
            final Config config = getConfig(args);
            LOG.info("User: " + config.username);
            LOG.info("Directory: " + config.directory);

            LOG.debug("Starting files seeking job.");
            final FilesSeekerJob filesSeekerJob = new FilesSeekerJob(config.directory);
            final List<Path> list = filesSeekerJob.files;

            RequestHandler.CONFIG = config;

            final FilesListChangeListener filesListChangeListener = new FilesListChangeListener(filesSeekerJob.files);

            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                    filesSeekerJob, 0, 1, TimeUnit.SECONDS
            );

            ServerHandlerJob serverHandlerJob = new ServerHandlerJob(config.username, filesSeekerJob.files);
            filesSeekerJob.files.addListener(serverHandlerJob);
            serverHandlerJob.run();

        } catch (Exception e) {
            if(Objects.nonNull(e.getCause())){
                LOG.error(e.getCause().getMessage());
            } else {
                LOG.error(e.getMessage());
            }
            System.exit(1);
        }
    }

    private static Config getConfig(final String[] args) {

        final Arguments arguments = new Arguments();
        final CmdLineParser parser = new CmdLineParser(arguments);

        if(helpRequest(args, parser)){
            printUsageAndExit(parser);
        } else {
            parseArguments(args, parser);
        }

        return new Config(arguments.username, Paths.get(arguments.path));
    }

    private static void parseArguments(String[] args, CmdLineParser parser) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            throw new ClientException(e);
        }
    }

    private static void printUsageAndExit(CmdLineParser parser) {
        parser.printUsage(System.out);
        System.exit(0);
    }

    private static boolean helpRequest(String[] args, CmdLineParser parser) {

        for(final String string : args){
            if(string.equals("--help") || string.equals("-h")){
                return true;
            }
        }

        return false;
    }
}
