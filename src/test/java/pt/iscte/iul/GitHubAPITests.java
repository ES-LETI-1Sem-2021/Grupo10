package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GitHubAPITests {
    private GitHubAPI api;

    @Before
    public void once() throws IOException {
        this.api = new GitHubAPI(
                "Roguezilla",
                "ES-LETI-1Sem-2021-Grupo10",
                FileUtils.readFileToString(new File("token.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void numberOfCollaborators() throws IOException {
        Assert.assertEquals(4, api.getCollaborators().length);
    }
}
