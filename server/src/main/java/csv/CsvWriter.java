package csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {

    final static String FILENAME = "files.csv";
    static final String [] FILE_HEADER = {"username", "clientFile", "serverFile"};

    private final Path path;
    private final CSVPrinter csvPrinter;

    public CsvWriter(Path path) {
        this.path = path.resolve(FILENAME);
        try {
            csvPrinter = getCsvPrinter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRecord(String username, String clientFilename, String serverFilename) {
        List<String> values = new ArrayList<>();
        values.add(username);
        values.add(clientFilename);
        values.add(serverFilename);
        try {
            csvPrinter.printRecord(values);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CSVPrinter getCsvPrinter() throws IOException {
        CSVFormat csvFileFormat = getCsvFileFormat();
        FileWriter fileWriter = new FileWriter(path.toFile(), true);
        return new CSVPrinter(fileWriter, csvFileFormat);
    }

    static CSVFormat getCsvFileFormat() {
        return CSVFormat.DEFAULT.withHeader(FILE_HEADER).withRecordSeparator(System.lineSeparator());
    }

}
