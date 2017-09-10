import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class FilesListChangeListener implements ListChangeListener<Path> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesListChangeListener.class);

    private final ObservableList<Path> files;

    public FilesListChangeListener(final ObservableList<Path> files) {
        this.files = files;
        this.files.addListener(this);
    }

    @Override
    public void onChanged(Change<? extends Path> c) {
        while(c.next()){
            if(c.wasAdded()){
                LOG.info("New files: " + c.getAddedSubList());
            }
            if(c.wasRemoved()){
                LOG.info("Removed files: " + c.getRemoved());
            }
        }
        LOG.debug("Current files: " + files);
    }
}
