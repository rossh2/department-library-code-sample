package library.service;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

@Service
public class FileService {

    private static final String BASE_LIBRARY_PATH = "src/main/resources/static/base_library.tsv";

    public Reader getBaseLibraryReader() {
        try {
            return new FileReader(BASE_LIBRARY_PATH);
        } catch (FileNotFoundException e) {
            return new StringReader("");
        }
    }
}
