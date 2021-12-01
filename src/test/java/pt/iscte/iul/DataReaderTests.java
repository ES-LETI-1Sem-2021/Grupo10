package pt.iscte.iul;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DataReaderTests {

    @Test
    public void testDataReaderGit() throws IOException {
        var string = new String[]{Encoding.decode("test_line1"), Encoding.decode("test_line2"), Encoding.decode("test_line3")};
        Assertions.assertArrayEquals(string, DataReader.getUserGitData("test_files/data_reader_test.txt"));
    }

    @Test
    public void testDataReaderTrello() throws IOException {
        var string = new String[]{Encoding.decode("test_line4"), Encoding.decode("test_line5"), Encoding.decode("test_line6")};
        Assertions.assertArrayEquals(string, DataReader.getUserTrelloData("test_files/data_reader_test.txt"));
    }
}
