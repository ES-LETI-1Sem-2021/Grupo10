package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
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
                lines.get(1)
        );
    }

    @Test
    public void boardAttributes() throws IOException {
        Assertions.assertEquals("ES-LETI-1Sem-2021-Grupo10", this.api.getBoardInfo().getName());
        Assertions.assertEquals("614df1d076293f6b763c1c9c", this.api.getBoardInfo().getId());
        Assertions.assertEquals(
                "https://trello.com/b/lzp7YmaF/es-leti-1sem-2021-grupo10",
                this.api.getBoardInfo().getUrl()
        );
    }

    @Test
    public void numberOfBoards() throws IOException {
        Assertions.assertEquals(2, this.api.getBoards().length);
    }

    @Test
    public void sprintDates() throws IOException {
        var dates = new String[]{"2021-10-09", "2021-10-30"};
        Assertions.assertArrayEquals(dates, this.api.getSprintDates(1));
    }

    @Test
    public void numberOfLists() throws IOException {
        Assertions.assertEquals(9, this.api.getBoardLists().length);
    }

    @Test
    public void listAttributes() throws IOException {
        var list = this.api.getBoardLists()[0];
        Assertions.assertEquals("Product Backlog", list.getName());
        Assertions.assertEquals("614df1f97143f252bad74c5b", list.getId());
    }

    @Test
    public void cardAttributes() throws IOException {
        var card = this.api.getBoardCards();
        for (var c : card) {
            if (c.getId().equals("6161b8f50e32ff864a928bd6")) {
                Assertions.assertEquals("Sprint Planning - Sprint 1", c.getName());
                Assertions.assertEquals("6161b8f50e32ff864a928bd6", c.getId());
                Assertions.assertEquals("2021-10-09", c.getDueDate());
                break;
            }
        }
    }

    @Test
    public void doneProductBacklog() throws IOException {
        var backlogProducts = new ArrayList<>(
                Arrays.asList("APIs - Sprint 1", "Home UI - Sprint 1", "12. Descrição do projeto - Sprint 1",
                        "1. Identificação do Projeto - Sprint 1", "3. Início do projeto - Sprint 1",
                        "Organização do Trello - Sprint 1", "4. Datas dos Sprints - Sprint 1",
                        "2. Elementos da equipa - Sprint 1")
        );

        Assertions.assertEquals(
                backlogProducts,
                this.api.getDoneProductBacklog(1)
        );
    }

    @Test
    public void sprintCeremonyDescription() throws IOException {
        var planningDescription = """
                # Planeamento realizado
                - Identificação do Product Backlog;
                - Definida a duração do Sprint (3 semanas);
                - Definição de Sprint Backlog;
                - Definidas as datas das Sprint Retrospectives e Sprint Reviews (30 de outubro);
                - Discussão sobre o design e arquitetura do trabalho.
                                
                `Iniciado às 16:44 do dia 9 de outubro`
                                
                @global 3/3
                @duartecasaleiro 3/3
                @mamra2 3/3
                @oleksandrkobelyuk 3/3
                @rfgoo_iscte 3/3""";
        Assertions.assertEquals(
                planningDescription,
                this.api.getCeremonyDescription("Planning", 1)
        );

        var reviewDescription = """
                Todos os objetivos (Goals) propostos no Sprint Planning foram implementados com sucesso. Daqui saiu a versão 0.1 do trabalho.
                ## Este Sprint teve como resultados:
                > - Uma GUI funcional, onde é possivel observar:
                >   - O ficheiro [README.md](https://github.com/Roguezilla/ES-LETI-1Sem-2021-Grupo10#readme);
                >   - Os colaboradores (podendo aceder às suas páginas do GitHub);
                >   - Nome do projeto e sua data de início.
                > - Datas de ínicio e fim dos sprints.
                           
                @global 1/1
                @duartecasaleiro 1/1
                @mamra2 1/1
                @oleksandrkobelyuk 1/1
                @rfgoo_iscte 1/1""";
        Assertions.assertEquals(
                reviewDescription,
                this.api.getCeremonyDescription("Review", 1)
        );

        var retrospectiveDescription = """
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
                                
                @global 1/1
                @duartecasaleiro 1/1
                @mamra2 1/1
                @oleksandrkobelyuk 1/1
                @rfgoo_iscte 1/1""";
        Assertions.assertEquals(
                retrospectiveDescription,
                this.api.getCeremonyDescription("Retrospective", 1)
        );
    }

    @Test
    public void totalNumberOfCeremonies() throws IOException {
        Assertions.assertEquals(14, this.api.getTotalNumberOfCeremonies());
    }

    @Test
    public void totalNumberOfCeremoniesPerSprint() throws IOException {
        Assertions.assertEquals(5, this.api.getTotalNumberOfCeremoniesPerSprint(2));
    }

    @Test
    public void listsThatStartWith() throws IOException {
        var listsOfCeremonies = this.api.queryLists("Ceremonies");

        Assertions.assertEquals(3, listsOfCeremonies.size());
        Assertions.assertEquals("Ceremonies - Sprint 3", listsOfCeremonies.get(0).getName());
        Assertions.assertEquals("Ceremonies - Sprint 2", listsOfCeremonies.get(1).getName());
    }

    @Test
    public void numberOfHoursCeremony() throws IOException {
        Assertions.assertEquals(12.0, this.api.getTotalCeremonyHours());
    }

    @Test
    public void numberOfHoursPerUser() throws IOException {
        var out = this.api.getTotalHoursByUser("Done", "Sprint 1");
        var users = new String[]{"duartecasaleiro", "oleksandrkobelyuk", "rfgoo_iscte", "mamra2"};
        var spent = new Double[]{7.0, 3.0, 14.0, 8.0};
        var estimated = new Double[]{7.0, 3.0, 14.0, 8.0};

        for (int i = 0; i < out.size(); i++) {
            Assertions.assertEquals(users[i], out.get(i).getUser());
            Assertions.assertEquals(spent[i], out.get(i).getSpentHours());
            Assertions.assertEquals(estimated[i], out.get(i).getEstimatedHours());
        }
    }
}
