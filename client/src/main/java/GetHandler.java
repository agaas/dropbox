import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class GetHandler extends RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GetHandler.class);

    private final Path path;

    public GetHandler() {
        this.path = CONFIG.directory;
    }

    @Override
    protected void handleImpl(Request request) {
        OutputStream outputStream = getOutputStream(request);
        InputStream inputStream = getInputStream(socket);
        receiveFile(outputStream, inputStream, request.file.size);
    }

    private void receiveFile(OutputStream outputStream, InputStream inputStream, int size){
        LOG.debug("Receiving file...");
        byte[] bytes = new byte[size];
        try {
            while (inputStream.read(bytes) > 0) {
                outputStream.write(bytes);
            }
        } catch (IOException e){
            throw new ClientException(e);
        }
    }

    private OutputStream getOutputStream(Request request) {
        Path file = path.resolve(request.file.name);
        try {
            return Files.newOutputStream(file);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }
}
