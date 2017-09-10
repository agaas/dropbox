import config.Arguments;
import config.Config;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final int PORT = 9001;

    public static void main(String[] args){

        LOG.info("Server application start.");

        Config config = getConfig(args);

        ClientFilesSeeker.directories = config.paths;

        final List<FileJob> fileJobs = getFileJobsList(config.paths);
        final JobController jobController = new JobController(fileJobs);
        ClientHandler.JOB_CONTROLLER = jobController;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(jobController,0,50, TimeUnit.MILLISECONDS);

        try {
            final ServerSocket serverSocket = new ServerSocket(PORT);

            //TODO better solution than while(true)
            while (true){
                LOG.debug("Waiting for connection...");
                Socket clientSocket = waitForConnection(serverSocket);
                LOG.debug("Accepted connection: " + clientSocket.toString());
                final ClientHandler clientHandler = new ClientHandler(clientSocket);
                LOG.debug("Dispatching new client handler.");
                Executors.newSingleThreadExecutor().submit(clientHandler);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(1);
        }
    }

    private static List<FileJob> getFileJobsList(List<Path> paths) {
        List<FileJob> fileJobs = new ArrayList<>();

        for(int i=0;i<paths.size();++i){
            fileJobs.add(new FileJob(paths.get(i), PORT+i+1)); //TODO solve ports
        }

        return fileJobs;
    }

    private static Socket waitForConnection(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw new ServerException(e);
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

        return new Config(Paths.get(arguments.path));
    }

    private static void parseArguments(String[] args, CmdLineParser parser) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            throw new ServerException(e);
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