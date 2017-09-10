import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    static JobController JOB_CONTROLLER;

    private final InputStream reader;
    private final OutputStream writer;

    public ClientHandler(final Socket socket) {
        try {
            reader = socket.getInputStream();
            writer = socket.getOutputStream();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public void run() {

        LOG.info("Handling new client.");

        handleClient();

        LOG.info("Finished.");
    }

    private void handleClient() {

        do {
            Client client = waitForClientResponse();
            ClientFiles clientFiles = getClientFiles(client);

            List<ClientRequest> requests = new ArrayList<>();

            List<File> filesToGet = getFilesToGet(client, clientFiles);
            filesToGet.forEach(file -> {
                ClientRequest clientRequest = getClientRequest(client, file, Method.SEND);
                requests.add(clientRequest);
            });

            List<File> filesToSend = getFilesToSend(client, clientFiles);
            filesToSend.forEach(file -> {
                ClientRequest clientRequest = getClientRequest(client, file, Method.GET);
                requests.add(clientRequest);
            });

            LOG.info("Requests: {}", requests.toString());

            JOB_CONTROLLER.addRequests(requests);
        } while (true); //TODO add break
    }

    private ClientRequest getClientRequest(Client client, File file, Method method) {
        Request request = new Request();
        request.method = method;
        request.file = file;
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.request = request;
        clientRequest.username = client.username;
        clientRequest.outputStream = writer;
        return clientRequest;
    }

    private List<File> getFilesToSend(Client client, ClientFiles clientFiles) {
        List<File> filesToSend = new ArrayList<>();
        Collection<String> filesOnServer = clientFiles.getFilesOnServer();
        List<String> clientFilenames = client.files.stream().map(file -> file.name).collect(Collectors.toList());

        filesOnServer.forEach(file->{
            String clientFilename = clientFiles.getUserFilename(file);
            if(!clientFilenames.contains(clientFilename)){
                File f = new File();
                f.name = file;
                f.size = 8*1024; //TODO proper file size get
                filesToSend.add(f);
            }
        });

        return filesToSend;
    }

    private List<File> getFilesToGet(Client client, ClientFiles clientFiles) {
        List<File> filesToGet = new ArrayList<>();
        Collection<String> clientFilesOnServer = clientFiles.getUserFiles();

        client.files.forEach(file -> {
            if(!clientFilesOnServer.contains(file.name)){
                filesToGet.add(file);
            }
        });

        return filesToGet;
    }

    private ClientFiles getClientFiles(Client client) {
        return new ClientFiles(client.username);
    }

    private Client waitForClientResponse() {
        byte[] buffer = new byte[8*1024]; //TODO client instance size

        read(buffer);

        return Client.get(new String(buffer));
    }

    private void read(byte[] buffer) {
        try {
            reader.read(buffer);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ServerException(e);
        }
    }
}
