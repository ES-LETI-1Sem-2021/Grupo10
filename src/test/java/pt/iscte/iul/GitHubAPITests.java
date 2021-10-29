package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GitHubAPITests {
    private GitHubAPI api;

    @BeforeEach
    public void once() throws IOException {
        this.api = new GitHubAPI(
                "Roguezilla",
                "ES-LETI-1Sem-2021-Grupo10",
                FileUtils.readFileToString(new File("token.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void numberOfCollaborators() throws IOException {
        Assertions.assertEquals(4, api.getCollaborators().length);
    }

    @Test
    public void readMe() throws IOException {
        // may be changed in the future
        String readme = """
                # Aplicação para monitorização e avaliação de progresso de projetos de software que seguem a abordagem SCRUM
                                
                ------------------------------------------
                # Projeto desenvolvido por:
                  - Duarte Casaleiro, nº 92697
                  - Miguel Romana, nº 92688
                  - Oleksandr Kobelyuk, nº 92402
                  - Rodrigo Guerreiro, nº 92388
                """;
        Assertions.assertEquals(readme, this.api.getFile("master","/README.md"));
    }

    @Test
    public void noFileTest() throws IOException {
        Assertions.assertEquals("404: Not Found", this.api.getFile("master","/a/README.md"));
    }

    @Test
    public void dateTest() throws IOException {
        var date = this.api.getStartTime();
        Assertions.assertEquals("2021-10-08", date.getFormatted());
        Assertions.assertEquals("2021", date.getYear());
        Assertions.assertEquals("10", date.getMonth());
        Assertions.assertEquals("08", date.getDay());
    }
}
