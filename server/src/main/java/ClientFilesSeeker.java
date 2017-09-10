import csv.CsvReader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientFilesSeeker {

    static List<Path> directories;

    //not static to force creation of new instance to avoid concurrency
    Map<String, String> seek(String username){

        final Map<String, String> map = new HashMap<>();

        directories.forEach(path -> {
            CsvReader csvReader = new CsvReader(path);
            Map<String, String> files = csvReader.getFiles(username);
            map.putAll(files);
        });

        return map;
    }
}
