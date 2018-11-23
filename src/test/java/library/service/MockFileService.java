package library.service;

import java.io.Reader;
import java.io.StringReader;

public class MockFileService extends FileService {

    private String baseLibraryString = "";

    @Override
    public Reader getBaseLibraryReader() {
        return new StringReader(baseLibraryString);
    }

    public void setBaseLibraryString(String baseLibraryString) {
        this.baseLibraryString = baseLibraryString;
    }
}
