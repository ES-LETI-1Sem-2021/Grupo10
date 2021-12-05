package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncodingTests {
    private GitHubAPI api;

    @BeforeEach
    public void once() throws IOException {
        this.api = new GitHubAPI(
                "Roguezilla",
                "ES-LETI-1Sem-2021-Grupo10",
                FileUtils.readFileToString(new File("token.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void readmeEncodeDecode() throws IOException {
        var readme = this.api.getFile("master", "/README.md");
        var encoded = Encoding.encode(readme);
        Assertions.assertEquals(readme, Encoding.decode(encoded));
    }
}
