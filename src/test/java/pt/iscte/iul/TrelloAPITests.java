package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class TrelloAPITests {
    private TrelloAPI api;

    @Before
    public void once() throws IOException {
        var lines = FileUtils.readLines(new File("tokens.txt"), Charset.defaultCharset());

        this.api = new TrelloAPI(
                "ES-LETI-1Sem-2021-Grupo10",
                lines.get(0),
                lines.get(1));
    }

    @Test
    public void numberOfBoards() throws IOException {
        Assert.assertEquals(2, this.api.getBoards().length);
    }
}
