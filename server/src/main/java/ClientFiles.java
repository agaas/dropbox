import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ClientFiles {

    private final String username;
    private Map<String, String> files; //<client filename, server filename>

    public ClientFiles(String username) {
        this.username = username;
        refresh();
    }

    Collection<String> getUserFiles(){
        return files.values();
    }

    Set<String> getFilesOnServer(){
        return files.keySet();
    }

    void refresh(){
        this.files = new ClientFilesSeeker().seek(username);
    }

    String getUserFilename(String serverFilename){
        return files.get(serverFilename);
    }
}
