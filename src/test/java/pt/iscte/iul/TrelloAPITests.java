package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class TrelloAPITests {
    private TrelloAPI api;

    @BeforeEach
    public void once() throws IOException {
        var lines = FileUtils.readLines(new File("tokens.txt"), Charset.defaultCharset());

        this.api = new TrelloAPI(
                "ES-LETI-1Sem-2021-Grupo10",
                lines.get(0),
                lines.get(1));
    }

    @Test
    public void boardAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        Assertions.assertEquals("ES-LETI-1Sem-2021-Grupo10", this.api.getBoard(boardId).getName());
        Assertions.assertEquals("614df1d076293f6b763c1c9c", this.api.getBoard(boardId).getId());
        Assertions.assertEquals("https://trello.com/b/lzp7YmaF/es-leti-1sem-2021-grupo10",
                this.api.getBoard(boardId).getUrl());
    }


    @Test
    public void numberOfBoards() throws IOException {
        Assertions.assertEquals(2, this.api.getBoards().length);
    }

    @Test
    public void sprintDates() throws IOException {
        String[] dates = {"2021-10-09", "2021-10-30"};
        Assertions.assertArrayEquals(dates, this.api.getSprintDates("614df1d076293f6b763c1c9c", 1));
    }

    @Test
    public void numberOfLists() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        Assertions.assertEquals(9, this.api.getBoardLists(boardId).length);
    }

    @Test
    public void listAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        TrelloAPI.List list = this.api.getBoardLists(boardId)[0];
        Assertions.assertEquals("Product Backlog", list.getName());
        Assertions.assertEquals("614df1f97143f252bad74c5b", list.getId());
    }

    @Test
    public void cardAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        String Id = "6161b8f50e32ff864a928bd6";
        String cardName = "";
        String cardId = "";
        String cardDue = "";
        TrelloAPI.Card[] card = this.api.getBoardCards(boardId);
        for (TrelloAPI.Card c : card) {
            if (c.getId().equals(Id)) {
                cardName = c.getName();
                cardId = c.getId();
                cardDue = c.getDueDate();
                break;
            }
        }
        Assertions.assertEquals("Sprint Planning - Sprint 1", cardName);
        Assertions.assertEquals("6161b8f50e32ff864a928bd6", cardId);
        Assertions.assertEquals("2021-10-09", cardDue);
    }

    @Test
    public void doneProductBacklog() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        ArrayList<String> backlogProducts = new ArrayList<>(
                Arrays.asList("APIs - Sprint 1", "Home UI - Sprint 1", "12. Descrição do projeto - Sprint 1",
                        "1. Identificação do Projeto - Sprint 1", "3. Início do projeto - Sprint 1",
                        "Organização do Trello - Sprint 1", "4. Datas dos Sprints - Sprint 1",
                        "2. Elementos da equipa - Sprint 1")
        );
        var doneProducts = this.api.getDoneProductBacklog(boardId, 1);
        Assertions.assertEquals(backlogProducts, doneProducts);
    }

    @Test
    //TODO: Beautify the descriptions of the Sprint Ceremonies
    public void sprintCeremonyDescription() throws IOException {
        String planningDescription = """
                #Planeamento realizado
                - Identificação do Product Backlog;
                - Definida a duração do Sprint (3 semanas);
                - Definição de Sprint Backlog;
                - Definidas as datas das Sprint Retrospectives e Sprint Reviews (30 de outubro);
                - Discussão sobre o design e arquitetura do trabalho.

                `Iniciado às 16:44 do dia 9 de outubro`

                @global 3/3""";
        Assertions.assertEquals(planningDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Planning", 1));

        String reviewDescription = """
                Todos os objetivos (Goals) propostos no Sprint Planning foram implementados com sucesso. Daqui saiu a versão 0.1 do trabalho.
                ## Este Sprint teve como resultados:
                - Uma GUI funcional, onde é possivel observar:
                > - O ficheiro ([README.md](https://github.com/Roguezilla/ES-LETI-1Sem-2021-Grupo10#readme))
                > - Os colaboradores (acedendo ainda às suas páginas do GitHub)
                > - Nome do projeto e sua data de início
                - Datas de ínicio e fim dos sprints

                @global 1/1""";
        Assertions.assertEquals(reviewDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Review", 1));

        String retrospectiveDescription = """
                # Críticas positivas:
                - Estimativa da duração do sprint
                - Organização do trabalho a fazer

                # Críticas negativas:
                - Estimativa da duração das tarefas
                - Desequilíbrio na distribuição das tarefas
                (demasiados cartões para a GUI e poucos para a API do Trello)

                # A melhorar:
                - Estimar melhor a duração de cada tarefa
                - Distribuir melhor o trabalho

                @global 1/1""";
        Assertions.assertEquals(retrospectiveDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Retrospective", 1));
    }

    @Test
    public void totalNumberOfCeremonies() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        int totalNumberOfCeremonies = this.api.getTotalNumberOfCeremonies(boardId);
        Assertions.assertEquals(12, totalNumberOfCeremonies);
    }

    @Test
    public void totalNumberOfCeremoniesPerSprint() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        int totalNumberOfCeremonies = this.api.getTotalNumberOfCeremoniesPerSprint(boardId, 2);
        Assertions.assertEquals(5, totalNumberOfCeremonies);
    }

    @Test
    public void listsThatStartWith() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";

        var listsOfCeremonies = this.api.getListsThatStartWith(boardId, "Ceremonies");
        Assertions.assertEquals(3, listsOfCeremonies.size());
        Assertions.assertEquals("Ceremonies - Sprint 3", listsOfCeremonies.get(0).getName());
        Assertions.assertEquals("Ceremonies - Sprint 2", listsOfCeremonies.get(1).getName());
    }

    @Test
    public void numberOfHoursCeremony() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        Assertions.assertEquals(9.25, this.api.getTotalHoursCeremony(boardId));
    }

    @Test
    public void numberOfHoursPerUser() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";

        var out = this.api.getTotalHoursByUser(boardId, "", "Sprint 1");
        String[] users = new String[]{"rfgoo_iscte", "mamra2", "duartecasaleiro", "oleksandrkobelyuk"};
        Double[] spent = new Double[]{14.0, 8.0, 7.0, 3.0};
        Double[] estimated = new Double[]{14.0, 8.0, 7.0, 3.0};

        for (int i = 0; i < out.size(); i++) {
            Assertions.assertEquals(users[i], out.get(i).getUser());
            Assertions.assertEquals(spent[i], out.get(i).getSpentHours());
            Assertions.assertEquals(estimated[i], out.get(i).getEstimatedHours());
        }
    }
}
