package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(6, this.api.getBoardLists(boardId).length);
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
        String planningDescription = "#Planeamento realizado\n" +
                "- Identificação do Product Backlog;\n" +
                "- Definida a duração do Sprint (3 semanas);\n" +
                "- Definição de Sprint Backlog;\n" +
                "- Definidas as datas das Sprint Retrospectives e Sprint Reviews (30 de outubro);\n" +
                "- Discussão sobre o design e arquitetura do trabalho.\n" +
                "\n" +
                "`Iniciado às 16:44 do dia 9 de outubro`";
        Assertions.assertEquals(planningDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Planning", 1));

        String reviewDescription = "Todos os objetivos (Goals) propostos no Sprint Planning foram implementados com sucesso. Daqui saiu a versão 0.1 do trabalho.\n" +
                "## Este Sprint teve como resultados:\n" +
                "- Uma GUI funcional, onde é possivel observar:\n" +
                "> - O ficheiro ([README.md](https://github.com/Roguezilla/ES-LETI-1Sem-2021-Grupo10#readme))\n" +
                "> - Os colaboradores (acedendo ainda às suas páginas do GitHub)\n" +
                "> - Nome do projeto e sua data de início\n" +
                "- Datas de ínicio e fim dos sprints";
        Assertions.assertEquals(reviewDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Review", 1));

        String retrospectiveDescription = "# Críticas positivas:\n" +
                "- Estimativa da duração do sprint\n" +
                "- Organização do trabalho a fazer\n" +
                " \n" +
                "\n" +
                "# Críticas negativas:\n" +
                "- Estimativa da duração das tarefas\n" +
                "- Desequilíbrio na distribuição das tarefas\n" +
                "(demasiados cartões para a GUI e poucos para a API do Trello)\n" +
                "\n" +
                "# A melhorar:\n" +
                "- Estimar melhor a duração de cada tarefa\n" +
                "- Distribuir melhor o trabalho\n" +
                "\n";
        Assertions.assertEquals(retrospectiveDescription,
                this.api.getCeremonyDescription("614df1d076293f6b763c1c9c", "Retrospective", 1));
    }

}
