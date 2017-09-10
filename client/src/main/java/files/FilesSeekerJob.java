package files;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FilesSeekerJob implements Runnable {

    public final ObservableList<Path> files = FXCollections.observableArrayList();
    private final Path directory;

    public FilesSeekerJob(final Path directory) {
        this.directory = directory;
    }

    @Override
    public void run() {
        final FilesSeeker filesSeeker = new FilesSeeker();

        walkFileTree(filesSeeker);

        updateListWithCurrentFiles(filesSeeker.files);
    }

    private void updateListWithCurrentFiles(List<Path> foundFiles) {
        this.files.retainAll(foundFiles);
        foundFiles.removeAll(this.files);
        this.files.addAll(foundFiles);
    }

    private void walkFileTree(FilesSeeker filesSeeker) {
        try {
            Files.walkFileTree(directory, filesSeeker);
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO better error handling
        }
    }
}
