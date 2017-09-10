import javafx.collections.ListChangeListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandlerJob implements ListChangeListener<Path> {

    private static final String HOST = "localhost";
    private static final int PORT = 9001;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final String username;
    private final List<Path> files;

    private ServerHandler serverHandler;

    public ServerHandlerJob(String username, List<Path> files) {
        this.username = username;
        this.files = files;

        RequestHandler.FILES = files;
        RequestHandler.HOST = HOST;
    }

    public void run() {
        Client client = getClient();

        serverHandler = new ServerHandler(HOST, PORT);
        serverHandler.sendClientData(client);

        Request request; //TODO null object pattern?
        do {
            request = serverHandler.waitForRequest();

            RequestHandler requestHandler = RequestHandlerFactory.get(request);

            Runnable job = getJob(requestHandler, request);

            executorService.submit(job);

        } while(!isEnd(request.method));

        serverHandler.closeConnection();
    }

    private Runnable getJob(RequestHandler requestHandler, Request request){
        return () -> requestHandler.handle(request);
    }

    private boolean isEnd(Method method) {
        return method.equals(Method.END);
    }


    private Client getClient() {
        Client client = new Client();
        client.username = username;
        client.files = getFiles();
        return client;
    }

    private List<File> getFiles(){
        List<File> list = new ArrayList<>();
        files.forEach(path->{
            File file = new File();
            file.name = path.getFileName().toString();
            file.size = getSize(path);
            list.add(file);
        });
        return list;
    }

    private int getSize(Path path) {
        try {
            return (int)Files.size(path);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public void onChanged(Change<? extends Path> c) {
        if(Objects.nonNull(serverHandler)){
            Client client = new Client();
            client.username = username;
            client.files = getFiles();
            serverHandler.sendClientData(client);
        }
    }
}
