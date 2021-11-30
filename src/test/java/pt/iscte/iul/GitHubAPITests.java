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

        var names = new String[]{"Olek", null, "Duarte", "Miguel Romana", "Rodrigo Guerreiro"};
        for (int i = 0; i < collaborators.length; i++) {
            Assertions.assertEquals(names[i], collaborators[i].getName());
        }
    }

    @Test
    public void fileTest() throws IOException {
        // may be changed in the future
        String foo = """
                just a placeholder for the data directory because it is necessary to have this directory in order
                for the program to work.
                """;
        Assertions.assertEquals(foo, this.api.getFile("master", "/data/foo.txt"));
    }

    @Test
    public void noFileTest() throws IOException {
        Assertions.assertEquals("404: Not Found", this.api.getFile("master", "/a/README.md"));
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

        var names = new String[]{"github_api", "master", "trello_api", "ui_test"};
        for (int i = 0; i < branches.length; i++) {
            Assertions.assertEquals(names[i], branches[i].getName());
        }
    }

    @Test
    public void commitTest() throws IOException {
        var commits = api.getCommits("ui_test", "Roguezilla");

        Assertions.assertFalse(commits.getCommitList().isEmpty());

        System.out.println("Committer: " + commits.getCommitter());
        System.out.println("Number of commits: " + commits.getCommitList().size());
        System.out.println("First commit: " + commits.getCommitList().get(0));
        System.out.println("First commit date: " + commits.getCommitList().get(0));
        System.out.println("Lastest commit: " + commits.getCommitList().get(commits.getCommitList().size() - 1).message());
        System.out.println("Lastest commit date: " + commits.getCommitList().get(commits.getCommitList().size() - 1).date());

        commits.getCommitList().forEach(commitData -> Assertions.assertNotEquals(null, commitData.message()));
    }

    @Test
    public void tagTest() throws IOException {
        var tags = api.getTags();

        var names = new String[]{"Sprint_1", "DashboardSCRUM-0.2"};
        var dates = new GitHubAPI.Date[]{new GitHubAPI.Date("2021-10-29"), new GitHubAPI.Date("2021-11-20")};
        for (int i = 0; i < tags.size(); i++) {
            Assertions.assertEquals(names[i], tags.get(i).name());
            Assertions.assertEquals(dates[i], tags.get(i).date());
        }
    }
}
