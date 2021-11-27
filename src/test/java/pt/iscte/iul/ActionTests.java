package pt.iscte.iul;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class ActionTests {

    @Test
    public void saveDataTest() throws FileNotFoundException {

        String[] str = {"str_line1", "str_line2", "str_line3"};
        Action.saveData(str,str,"test_files/data_saver_tests.txt" );

        Assertions.assertArrayEquals(str, DataReader.getUserGitData("test_files/data_saver_tests.txt"));
        Assertions.assertArrayEquals(str, DataReader.getUserTrelloData("test_files/data_saver_tests.txt"));
    }

    @Test
    public void convertMarkdownToHTMLTest(){
        String mdText = """
                # Críticas positivas:
                - Estimativa da duração do sprint
                - Organização do trabalho a fazer
                """;
        Assertions.assertEquals(Action.convertMarkdownToHTML(mdText), """
                <h1>Críticas positivas:</h1>
                <ul>
                <li>Estimativa da duração do sprint</li>
                <li>Organização do trabalho a fazer</li>
                </ul>
                """);
    }
}
