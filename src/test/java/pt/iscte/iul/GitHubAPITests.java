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
        var collaborators = api.getCollaborators();

        Assertions.assertEquals(5, collaborators.length);

        var names = new String[] {"Olek", null, "Duarte", "Miguel", "Rodrigo Guerreiro"};
        for (int i = 0; i < collaborators.length; i++) {
            Assertions.assertEquals(names[i], collaborators[i].getName());
        }
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
                                
                ## Sprint 1:
                ### Este Sprint teve como resultados:
                - Uma GUI funcional, onde é possivel observar:
                > - O ficheiro ([README.md](https://github.com/Roguezilla/ES-LETI-1Sem-2021-Grupo10#readme))
                > - Os colaboradores (acedendo ainda às suas páginas do GitHub)
                > - Nome do projeto e sua data de início
                - Datas de ínicio e fim dos sprints
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
        Assertions.assertEquals("2021-10-08", date.toString());
        Assertions.assertEquals("2021", date.getYear());
        Assertions.assertEquals("10", date.getMonth());
        Assertions.assertEquals("08", date.getDay());
    }

    @Test
    public void branchTest() throws IOException {
        var branches = api.getBranches();

        Assertions.assertEquals(4, branches.length);

        var names = new String[] {"github_api", "master", "trello_api", "ui_test"};
        for (int i = 0; i < branches.length; i++) {
            Assertions.assertEquals(names[i], branches[i].getName());
        }
    }

    @Test
    public void commitTest() throws IOException {
        var commits = api.getCommits("ui_test", "Roguezilla");

        System.out.println("Committer: " + commits.getCommitter());
        System.out.println("Numer of commits: " + commits.getCommitData().size());
        System.out.println("First commit: " + commits.getCommitData().get(0).getMessage());
        System.out.println("First commit date: " + commits.getCommitData().get(0).getDate());
        System.out.println("Lastest commit: " + commits.getCommitData().get(commits.getCommitData().size()-1).getMessage());
        System.out.println("Lastest commit date: " + commits.getCommitData().get(commits.getCommitData().size()-1).getDate());

        Assertions.assertNotEquals(0, commits.getCommitData().size());
        commits.getCommitData().forEach(commitData -> Assertions.assertNotEquals(null, commitData.getMessage()));
    }
}
