import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    private final Socket socket;
    private final OutputStream writer;
    private final InputStream reader;

    public ServerHandler(String host, int port) {
        this.socket = createSocket(host, port);
        this.writer = getOutputStream();
        this.reader = getInputStream();
    }

    public void sendClientData(Client client) {
        try {
            final String clientJson = Client.get(client);
            writer.write(clientJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request waitForRequest(){
        String response = waitForServerResponse();
        return Request.get(response);
    }

    public void closeConnection(){
        try {
            LOG.debug("Closing connection.");
            socket.close();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private String waitForServerResponse(){
        byte[] buffer = new byte[8*1024]; //TODO resolve hardcoded value
        try {
            reader.read(buffer);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return new String(buffer).trim();
    }

    private Socket createSocket(String host, int port) {
        try {
            LOG.info("Connecting to " + host + ":" + port);
            Socket socket = new Socket(host, port);
            LOG.info("Connected.");
            return socket;
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }
}
