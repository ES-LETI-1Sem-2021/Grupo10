package pt.iscte.iul;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

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
     * @param frame            The frame to attach the info.
     * @param userGitInfo    String array with[git_owner, git_repo, git_token]
     * @param userTrelloInfo String array with [trello_name, trello_key, trello_token]
     * @param flag             flag == 1 if it is needed to scale the window size, any number otherwise.
     * @throws IOException throws exception
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
    public static void saveData(String[] userGitInfo, String[] userTrelloInfo, String filename) {
        DataSaver.save(userGitInfo, userTrelloInfo, filename);
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
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     */
    public static void homeScreen(JFrame frame, GitHubAPI gitHubAPI, TrelloAPI trelloAPI, String boardID) throws IOException {
        GitHubAPI.Date dataInicio = gitHubAPI.getStartTime();
        String readme = gitHubAPI.getFile("master", "/README.md");

        JEditorPane editorPane = new JEditorPane();
        JScrollPane scrollerLeft = new JScrollPane();

        //Label com a data de inicio do trabalho
        JLabel labelData = new JLabel("Project's start date: " + dataInicio.toString());
        labelData.setBounds(10, 5, 250, 30);
        editorPane.add(labelData);

        //Label com o nome do projeto (nome do repo)
        JLabel labelProjName = new JLabel("Project's name: " + trelloAPI.getBoard(boardID).getName());
        labelProjName.setBounds(260, 5, 300, 30);
        editorPane.add(labelProjName);

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
                addHoursInfo(frame,"",boardID,trelloAPI);
                addSprintDatesTable(trelloAPI, frame, boardID);
                addHoursByCeremony(trelloAPI, frame, boardID);
                frame.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Function that adds a pie chart to the ui as well as a table with the same information.
     * This function, by calling the getTotalHoursByUser severely increases the processing time.
     *
     * @param frame the frame to display the table
     * @param sprintName the name of the sprint blank if total.
     * @param boardID   the board id.
     * @param trelloAPI the trello instance.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addHoursInfo(JFrame frame, String sprintName, String boardID, TrelloAPI trelloAPI) throws IOException {
        ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers = trelloAPI.getTotalHoursByUser(boardID,"", sprintName);

        // Pie Charts
        DefaultPieDataset<String> dataset= new DefaultPieDataset<>();
        DefaultPieDataset<String> spentHoursDataset= new DefaultPieDataset<>();

        hoursPerUsers.forEach(e->{
            dataset.setValue(e.getUser(),e.getEstimatedHours());
            spentHoursDataset.setValue(e.getUser(),e.getEstimatedHours());
        });
        JFreeChart chart = ChartFactory.createPieChart(
                "Hours Estimated by user " + sprintName,
                dataset, true,true,false);

        JFreeChart spentHoursChart = ChartFactory.createPieChart(
                "Hours Spent by user " + sprintName,
                dataset, true,true,false);
        ChartPanel cp = new ChartPanel(chart);
        cp.setBounds(((frame.getWidth() / 2) - 30), 0, 300, 250);
        cp.setVisible(true);

        ChartPanel spentCP = new ChartPanel(spentHoursChart);
        spentCP.setBounds((frame.getWidth() - 320), 0, 300, 250);
        spentCP.setVisible(true);

        frame.add(cp);
        frame.add(spentCP);
        frame.setVisible(true);

        new JElements(frame, hoursPerUsers);
    }

    /**
     * Function that adds a table with the start and end date of each sprint
     *
     * @param trelloAPI the trello instance
     * @param frame the frame to display the table
     * @param boardID   the board id.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addSprintDatesTable(TrelloAPI trelloAPI, JFrame frame, String boardID) throws IOException {

        int numberOfSprints = trelloAPI.getListsThatContain(boardID, "Done - Sprint").size();

        String[][] dates = new String[numberOfSprints + 1][3];
        String[] names = {"Sprint", "Start Date", "End Date"};
        dates[0] = names;

        for (int i = 0; i < numberOfSprints; i++) {
            String[] datesOfSprint = trelloAPI.getSprintDates(boardID, i+1);
            dates[i + 1] = new String[]{String.valueOf(i + 1), datesOfSprint[0], datesOfSprint[1]};
        }

        JTable table = new JTable(dates, names);
        table.setBounds(((frame.getWidth() / 2) + 95), (frame.getHeight() - 150), 400, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);

        frame.add(table);
    }

    /**
     * Function that adds a table with the hours spent by the team and by member in ceremonies
     *
     * @param trelloAPI the trello instance
     * @param frame the frame to display the table
     * @param boardID   the board id.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addHoursByCeremony(TrelloAPI trelloAPI, JFrame frame, String boardID) throws IOException {
        ArrayList<TrelloAPI.HoursPerUser> hoursPerUser = trelloAPI.getTotalHoursByUser(boardID,"Ceremonies", "");

        String[][] content = new String[hoursPerUser.size() + 2][3];
        String[] names = {"Members", "Nº Ceremonies", "Total Hours"};
        content[0] = names;

        for (int i = 0; i != hoursPerUser.size(); i++) {
            content[i + 1] = new String[]{hoursPerUser.get(i).getUser(),
                    String.valueOf(trelloAPI.getTotalNumberOfCeremonies(boardID)),
                    String.valueOf(hoursPerUser.get(i).getSpentHours())};
        }

        content[hoursPerUser.size() + 1] = new String[]{"Total",
                String.valueOf(trelloAPI.getTotalNumberOfCeremonies(boardID)),
                        String.valueOf(trelloAPI.getTotalHoursCeremony(boardID))};

        JTable table = new JTable(content, names);
        table.setBounds(((frame.getWidth() / 2) + 150), ((frame.getHeight() / 2) + 70), 300, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.black);
        table.setShowGrid(true);

        frame.add(table);

    }
}
