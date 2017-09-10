package csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CsvReader {

    private final Path path;

    public CsvReader(Path path) {
        this.path = path.resolve(CsvWriter.FILENAME);
    }

    public Map<String, String> getFiles(String username){
        Map<String, String> files;

        if(Files.exists(path)){
            files = seekUserFiles(username);
        } else {
            files = Collections.emptyMap();
        }

        return files;
    }

    private Map<String, String> seekUserFiles(String username) {
        try {
            CSVFormat csvFileFormat = CsvWriter.getCsvFileFormat();
            CSVParser parser = new CSVParser(new FileReader(path.toFile()), csvFileFormat);
            List<CSVRecord> records = parser.getRecords();

            final Map<String, String> map = new HashMap<>();

            records.stream()
                    .filter(byUsername(username))
                    .forEach(putInto(map));

            return map;

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private Consumer<CSVRecord> putInto(Map<String, String> map) {
        return record->{
            String clientFile = record.get("clientFile");
            String serverFile = record.get("serverFile");
            map.put(serverFile, clientFile);
        };
    }

    private Predicate<CSVRecord> byUsername(String username) {
        return record->record.get("username").equals(username);
    }

}
