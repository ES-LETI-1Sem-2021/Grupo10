package pt.iscte.iul;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


/**
 * Classe where all the magic happens.
 *
 * @author Rodrigo Guerreiro.
 */
public class Action {
    /**
     * Function that does everything.
     * Connects the UI's to the api's.
     *
     * @param frame            The frame to attach the info.
     * @param userGitInfo    String array with[git_owner, git_repo, git_token]
     * @param userTrelloInfo String array with [trello_name, trello_key, trello_token]
     * @param flag             flag == 1 if it is needed to scale the window size, any number otherwise.
     * @throws IOException
     * @author Rodrigo Guerreiro
     */
    public static void doAction(JFrame frame, String[] userGitInfo, String[] userTrelloInfo, int flag) throws IOException {
        var gitApi = new GitHubAPI(userGitInfo[0], userGitInfo[1], userGitInfo[2]);
        var trelloAPI = new TrelloAPI(userTrelloInfo[0], userTrelloInfo[1], userTrelloInfo[2]);

        String boardID = null;
        TrelloAPI.Board[] boards = trelloAPI.getBoards();
        for (TrelloAPI.Board b : boards) {
            if (b.getName().equals(userTrelloInfo[0])) {
                boardID = b.getId();
            }
        }

        clearFrame(frame);
        //Adiciona os menus
        new Menus(frame, gitApi, trelloAPI, boardID);

        // aumenta o tamanho do ecra apenas usado a primeira vez que esta função é executada
        if (flag == 1) {
            frame.setLocation(0, 0);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screenSize.width, screenSize.height - 100);
        }
        //if (dataInicio == null) throw new AssertionError();
        homeScreen(frame, gitApi, trelloAPI, boardID);
    }

    /**
     * Function that calls another function in order to save the user data in a .txt file
     *
     * @param userGitInfo    info needed for git api.
     * @param userTrelloInfo info needed for trello api.
     * @author Rodrigo Guerreiro
     */
    public static void saveData(String[] userGitInfo, String[] userTrelloInfo) {
        DataSaver.save(userGitInfo, userTrelloInfo, "data/user_data.txt");
    }

    /**
     * Function that converts a give string (in markdown) to the same string formatted in html.
     *
     * @param markdown the string in markdown
     * @return the string 'markdown' converted for html
     */

    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    /**
     * Function that clear every thing that is on the frame at the moment
     *
     * @param frame the JFrame that the project is based on
     * @author Rodrigo Guerreiro
     */

    public static void clearFrame(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Function that shows the default information on the screen.
     *
     * @param frame the frame to show the info.
     * @param gitHubAPI  the github api instance.
     * @param trelloAPI  the trello api instance.
     * @throws IOException
     * @author Rodrigo Guerreiro
     */
    public static void homeScreen(JFrame frame, GitHubAPI gitHubAPI, TrelloAPI trelloAPI, String boardID) throws IOException {
        GitHubAPI.Date dataInicio = gitHubAPI.getStartTime();
        String readme = gitHubAPI.getFile("master", "/README.md");

        //Label com a data de inicio do trabalho
        JLabel labelData = new JLabel("Project's start date: " + dataInicio.toString());
        labelData.setBounds(100, 50, 250, 30);
        frame.add(labelData);

        //TODO arranjar forma de meter o nome do projeto

        //Label com o nome do projeto (nome do repo)
        JLabel labelProjName = new JLabel("Project's name: " + trelloAPI.getBoard(boardID).getName());
        labelProjName.setBounds(400, 50, 300, 30);
        frame.add(labelProjName);

        //Print do readme no ecrã
        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(convertMarkdownToHTML(readme));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(100, 100, 500, 600);

        frame.add(edt);
        frame.setVisible(true);
    }
}
