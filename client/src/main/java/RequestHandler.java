import config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;

public abstract class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    static Config CONFIG;
    static List<Path> FILES;
    static String HOST;

    protected Socket socket;

    public void handle(Request request){
        LOG.debug("Handling request {}", Request.get(request));

        openConnection(request);
        handleImpl(request);
        closeConnection();

        LOG.debug("Request handled successfully.");
    }

    protected abstract void handleImpl(Request request);

    protected void openConnection(Request request) {
        try {
            LOG.debug("Establishing connection " + HOST + ":" + request.port);
            socket = new Socket(HOST, request.port);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    protected void closeConnection(){
        try {
            LOG.debug("Closing connection.");
            socket.close();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    protected OutputStream getOutputStream(Socket socket) {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    protected InputStream getInputStream(Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }
}
