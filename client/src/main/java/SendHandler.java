import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SendHandler extends RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SendHandler.class);

    @Override
    protected void handleImpl(Request request) {
        OutputStream outputStream = getOutputStream(socket);
        InputStream inputStream = getInputStream(request);
        int size = getFileSize(request);
        sendFile(inputStream, outputStream, size);
    }

    private int getFileSize(Request request) {
        Path file = FILES.stream()
                .filter(path -> path.getFileName().toString().equals(request.file.name))
                .findAny().orElseThrow(fileNotFoundException(request));

        return getFileSize(file);
    }

    private int getFileSize(Path file) {
        //TODO handle file size better
        try {
            return (int) Files.size(file);
        } catch (IOException e) {
            return Integer.MAX_VALUE;
        }
    }

    private InputStream getInputStream(Request request) {
        Path file = getFile(request);
        return getInputStream(file);
    }

    private void sendFile(InputStream inputStream, OutputStream outputStream, int size) {
        LOG.debug("Sending file...");
        byte[] bytes = new byte[size];
        try {
            while (inputStream.read(bytes) > 0) {
                outputStream.write(bytes);
            }
        } catch (IOException e){
            throw new ClientException(e);
        }
    }

    private InputStream getInputStream(Path file) {
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private Path getFile(Request request) {
        return FILES.stream()
                .filter(p->p.getFileName().toString().equals(request.file.name))
                .findAny().get();//.orElseThrow(fileNotFoundException(request));
    }

    private Supplier<ClientException> fileNotFoundException(Request request) {
        return ()->new ClientException("File not found in list: " + request.file);
    }
}
