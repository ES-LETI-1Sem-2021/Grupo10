package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncodingTests {
    private GitHubAPI api;

    @Before
    public void once() throws IOException {
        this.api = new GitHubAPI(
                "Roguezilla",
                "ES-LETI-1Sem-2021-Grupo10",
                FileUtils.readFileToString(new File("token.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void readmeEncodeDecode() throws IOException {
        String readme = api.getFile("master", "/README.md");
        String encoded = Encoding.Encode(readme);
        Assert.assertEquals(readme, Encoding.Decode(encoded));
    }
}
