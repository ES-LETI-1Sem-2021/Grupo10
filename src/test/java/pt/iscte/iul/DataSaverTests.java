package pt.iscte.iul;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class DataSaverTests {
    @Test
    public void TestSave(){
        String[] str = {"str_line1","str_line2","str_line3"};
        DataSaver.save(str,str,"test_files/data_saver_tests.txt");

        Assertions.assertArrayEquals(str,DataReader.getUserGitData("test_files/data_saver_tests.txt"));
        Assertions.assertArrayEquals(str, DataReader.getUserTrelloData("test_files/data_saver_tests.txt"));
    }

    @Test
    public void TestExists(){
    Assertions.assertTrue(DataSaver.exists(new File("test_files/data_saver_tests.txt")));
    }
}
