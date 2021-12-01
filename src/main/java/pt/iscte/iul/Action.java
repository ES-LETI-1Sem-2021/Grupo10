package pt.iscte.iul;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.swing.BorderFactory.createEmptyBorder;

/**
 * Class where all the magic happens.
 *
 * @author Rodrigo Guerreiro.
 */
public class Action {
    /**
     * Function that does everything.
     * Connects the UI's to the api's.
     *
     * @param frame          The frame to attach the info.
     * @param userGitInfo    String array with[git_owner, git_repo, git_token]
     * @param userTrelloInfo String array with [trello_name, trello_key, trello_token]
     * @param flag           flag == 1 if it is needed to scale the window size, any number otherwise.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     */
    public static void doAction(JFrame frame, String[] userGitInfo, String[] userTrelloInfo, int flag) throws IOException {
        var gitApi = new GitHubAPI(userGitInfo[0], userGitInfo[1], userGitInfo[2]);
        var trelloAPI = new TrelloAPI(userTrelloInfo[0], userTrelloInfo[1], userTrelloInfo[2]);

        clearFrame(frame);
        //Adiciona os menus
        new Menus(frame, gitApi, trelloAPI);

        // aumenta o tamanho do ecra apenas usado a primeira vez que esta função é executada
        if (flag == 1) {
            frame.setLocation(0, 0);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screenSize.width, screenSize.height - 100);
        }
        //if (dataInicio == null) throw new AssertionError();
        homeScreen(frame, gitApi, trelloAPI);
    }

    /**
     * Function that calls another function in order to save the user data in a .txt file
     *
     * @param userGitInfo    info needed for git api.
     * @param userTrelloInfo info needed for trello api.
     * @throws IOException   throws exception
     * @author Rodrigo Guerreiro
     */
    public static void saveData(String[] userGitInfo, String[] userTrelloInfo, String filename) throws IOException {
        DataSaver.save(userGitInfo, userTrelloInfo, filename);
    }

    /**
     * Function that converts a give string (in markdown) to the same string formatted in html.
     *
     * @param markdown the string in markdown
     * @return the string 'markdown' converted for html
     */

    public static String convertMarkdownToHTML(String markdown) {
        var parser = Parser.builder().build();
        var document = parser.parse(markdown);
        var htmlRenderer = HtmlRenderer.builder().build();
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
     * Method that clear the content of a file.
     *
     * @param filename Input filename.
     * @throws IOException throws exception.
     * @author Rodrigo Guerreiro
     */
    public static void clearTheFile(String filename) throws IOException {
        var fwOb = new FileWriter(filename, false);
        var pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    /**
     * Function that shows the default information on the screen.
     *
     * @param frame     the frame to show the info.
     * @param gitHubAPI the github api instance.
     * @param trelloAPI the trello api instance.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     */
    public static void homeScreen(JFrame frame, GitHubAPI gitHubAPI, TrelloAPI trelloAPI) throws IOException {
        var dataInicio = gitHubAPI.getStartTime();
        var readme = gitHubAPI.getFile("master", "/README.md");

        var editorPane = new JEditorPane();
        var scrollerLeft = new JScrollPane();

        //Label com a data de inicio do trabalho
        var labelData = new JLabel("Project's start date: " + dataInicio.toString());
        labelData.setBounds(10, 5, 250, 30);
        editorPane.add(labelData);
        labelData.setForeground(new Color(68, 114, 196));

        //Label com o nome do projeto (nome do repo)
        var labelProjName = new JLabel("Project's name: " + trelloAPI.getBoardInfo().getName());
        labelProjName.setBounds(260, 5, 300, 30);
        editorPane.add(labelProjName);
        labelProjName.setForeground(new Color(68, 114, 196));

        //Print do readme no ecrã
        editorPane.setContentType("text/html");
        editorPane.setText("<br></br><br></br>" + convertMarkdownToHTML(readme) + "<br></br><br></br><br></br>");
        editorPane.setEditable(false);
        editorPane.setVisible(true);
        editorPane.setBounds(100, 100, 500, 600);

        frame.add(editorPane);
        frame.setVisible(true);

        scrollerLeft.setViewportView(editorPane);
        scrollerLeft.setBounds(15, 0, ((frame.getWidth() - 100) / 2), frame.getHeight());
        scrollerLeft.setBorder(createEmptyBorder());
        frame.add(scrollerLeft);
        frame.setVisible(true);

        // Threading
        SwingUtilities.invokeLater(() -> {
            try {
                JElements.addHoursInfo(frame, "", trelloAPI);
                JElements.addSprintDatesTable(trelloAPI, frame);
                JElements.addHoursByCeremony(trelloAPI, frame);
                frame.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
