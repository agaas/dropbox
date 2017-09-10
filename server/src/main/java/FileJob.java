import csv.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;

public class FileJob implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FileJob.class);

    final Path disk;
    private final int port;
    private final CsvWriter csvWriter;

    private boolean isWorking = false;
    private OutputStream clientOutputStream;
    private Request request;
    private String username;

    public FileJob(Path disk, int port) {
        this.disk = disk;
        this.port = port;
        this.csvWriter = new CsvWriter(disk);
    }

    public Boolean isWorking() {
        return isWorking;
    }

    @Override
    public void run() {

        try {
            isWorking = true;

            LOG.debug("Sending request to client. {}", request);
            sendRequest();

            LOG.debug("Waiting for client connection");
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            LOG.debug("Connected.");

            if(request.method.equals(Method.SEND)){
                handleGet(socket);
            } else { //is GET
                handleSend(socket);
            }

            serverSocket.close();
            socket.close();

        } catch (IOException | InterruptedException e) {
            throw new ServerException(e);
        }

        isWorking = false;
    }

    private void handleSend(Socket socket) {
        LOG.debug("Handle send.");
    }

    private void handleGet(Socket socket) throws IOException, InterruptedException {
        InputStream socketInputStream = socket.getInputStream();

        LOG.debug("Generating filename.");
        Path file = getServerFilePath();
        OutputStream fileOutputStream = Files.newOutputStream(file);

        LOG.debug("Receiving file {}", file.toString());
        int fileSize = new Random().nextInt(20);
        LOG.debug("Simulating file size -> sleeping for {} seconds", fileSize);
        Thread.sleep(fileSize*1000);
        receiveFile(socketInputStream, fileOutputStream);
        LOG.debug("Adding record to csv.");
        csvWriter.addRecord(username, request.file.name, file.getFileName().toString());
        LOG.debug("Done.");
        fileOutputStream.close();
    }

    private void receiveFile(InputStream socketInputStream, OutputStream fileOutputStream) throws IOException {
        byte[] bytes = new byte[request.file.size];
        while (socketInputStream.read(bytes) > 0) {
            fileOutputStream.write(bytes);
        }
    }

    private Path getServerFilePath() {
        return disk.resolve(getServerFilename());
    }

    private void sendRequest() throws IOException {
        clientOutputStream.write(getRequestBytes());
    }

    private byte[] getRequestBytes() {
        return Request.get(request).getBytes();
    }

    private String getServerFilename(){
        String userFilename = request.file.name;
        String uuid = UUID.randomUUID().toString();

        return String.format("%s_%s_%s", username, uuid, userFilename);
    }

        void setClientRequest(ClientRequest clientRequest){
        request = clientRequest.request;
        request.port = port;
        username = clientRequest.username;
        clientOutputStream = clientRequest.outputStream;
    }
}
