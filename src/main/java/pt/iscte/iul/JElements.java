package pt.iscte.iul;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.BorderFactory.createEmptyBorder;

/**
 * Class that creates the tables and graphs to be presented on the frame.
 *
 * @author Rodrigo Guerreiro
 * @author Duarte Casaleiro
 */
public class JElements implements ActionListener {

    private final JFrame frame;
    private final JSpinner spinner;
    private final JButton button;
    private final ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers;
    private JTable table;
    private String[][] data;

    /**
     * Constructor method that initializes the objects and creates a spinner and a button
     * used to calculate the new costs in the tables.
     *
     * @param frame The frame where the graphs and the tables will be presented
     * @param hoursPerUsers An arrayList with {@link TrelloAPI.HoursPerUser}
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public JElements(JFrame frame, ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers) {
        this.frame = frame;
        this.hoursPerUsers = hoursPerUsers;

        //Spinner
        var model = new SpinnerNumberModel(20, 20, 100, 1);
        this.spinner = new JSpinner(model);
        this.spinner.setBounds(((frame.getWidth() - 200)), ((frame.getHeight() / 2) - 65), 100, 40);

        addTable(this.hoursPerUsers, this.frame, this.spinner);

        //Button
        this.button = new JButton("Calculate new Cost");
        this.button.setBounds((frame.getWidth() - 220), ((frame.getHeight() / 2) - 25), 150, 50);
        this.button.setForeground(new Color(68, 114, 196));
        this.button.setVisible(true);
        this.frame.add(button);
        this.button.repaint();
        this.button.addActionListener(this);

        this.frame.add(spinner);

        this.frame.setVisible(true);
        this.frame.repaint();
    }

    /**
     * Creates and adds a table with the hours spent and estimated for each user
     * and in total, as well as the cost.
     *
     * @param hoursPerUsers An arrayList with {@link TrelloAPI.HoursPerUser}
     * @param frame The frame where the graphs and the tables will be presented
     * @param spinner An instance for the spinner in order for the user to be able to change costs.
     */
    public void addTable(ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers, JFrame frame, JSpinner spinner) {
        var totalEstimated = 0.0;
        var totalSpent = 0.0;

        this.data = new String[hoursPerUsers.size() + 2][4];
        var names = new String[]{"Member", "Estimated Hours", "Spent Hours", "Cost (€)"};

        data[0] = names;

        for (int cont = 0; cont != hoursPerUsers.size(); cont++) {
            data[cont + 1] = new String[]{hoursPerUsers.get(cont).getUser(),
                    String.valueOf(hoursPerUsers.get(cont).getEstimatedHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours() * 20)};
            totalEstimated += hoursPerUsers.get(cont).getEstimatedHours();
            totalSpent += hoursPerUsers.get(cont).getSpentHours();
        }

        var totalCost = totalSpent * (int) spinner.getValue();

        data[hoursPerUsers.size() + 1] = new String[]{"Total", String.valueOf(totalEstimated),
                String.valueOf(totalSpent),
                String.valueOf(totalCost)};

        this.table = new JTable(data, names);
        table.setBounds(((frame.getWidth() / 2) - 20), ((frame.getHeight() / 2) - 70), 400, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);

        frame.add(table);
        this.frame.repaint();
    }

    /**
     * Function that adds a pie chart to the UI as well as a table with the same information.
     * Calling the {@link TrelloAPI #getTotalHoursByUser} severely increases the processing time.
     *
     * @param frame      the frame to display the table.
     * @param sprintName the name of the sprint (blank if relative to total project).
     * @param trelloAPI  the Trello instance.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addHoursInfo(JFrame frame, String sprintName, TrelloAPI trelloAPI) throws IOException {
        var hoursPerUsers = trelloAPI.getTotalHoursByUser("", sprintName);

        // Pie Charts
        var dataset = new DefaultPieDataset<String>();
        var spentHoursDataset = new DefaultPieDataset<String>();

        hoursPerUsers.forEach(e -> {
            dataset.setValue(e.getUser(), e.getEstimatedHours());
            spentHoursDataset.setValue(e.getUser(), e.getEstimatedHours());
        });
        var chart = ChartFactory.createPieChart(
                "Hours Estimated by user " + sprintName,
                dataset, true, true, false);

        var spentHoursChart = ChartFactory.createPieChart(
                "Hours Spent by user " + sprintName,
                dataset, true, true, false);
        var cp = new ChartPanel(chart);
        cp.setBounds(((frame.getWidth() / 2) - 30), 0, 300, 250);
        cp.setVisible(true);
        chart.getPlot().setBackgroundPaint(Color.white);
        chart.getPlot().setOutlinePaint(Color.white);
        chart.getTitle().setPaint(new Color(68, 114, 196));

        var spentCP = new ChartPanel(spentHoursChart);
        spentCP.setBounds((frame.getWidth() - 320), 0, 300, 250);
        spentCP.setVisible(true);
        spentHoursChart.getPlot().setBackgroundPaint(Color.white);
        spentHoursChart.getPlot().setOutlinePaint(Color.white);
        spentHoursChart.getTitle().setPaint(new Color(68, 114, 196));

        frame.add(cp);
        frame.add(spentCP);
        frame.setVisible(true);

        new JElements(frame, hoursPerUsers);
    }


    /**
     * Function that adds a table with the start and end date of each sprint.
     *
     * @param trelloAPI the Trello instance.
     * @param frame     the frame to display the table.
     * @throws IOException throws exception.
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addSprintDatesTable(TrelloAPI trelloAPI, JFrame frame) throws IOException {

        var numberOfSprints = trelloAPI.queryLists("Done - Sprint").size();

        var dates = new String[numberOfSprints + 1][3];
        var names = new String[]{"Sprint", "Start Date", "End Date"};
        dates[0] = names;

        for (var i = 0; i < numberOfSprints; i++) {
            var datesOfSprint = trelloAPI.getSprintDates(i + 1);
            dates[i + 1] = new String[]{String.valueOf(i + 1), datesOfSprint[0], datesOfSprint[1]};
        }

        var table = new JTable(dates, names);
        table.setBounds(((frame.getWidth() / 2) + 95), (frame.getHeight() - 150), 400, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);

        frame.add(table);
    }


    /**
     * Function that adds a table with the hours spent by the team and by member in ceremonies.
     *
     * @param trelloAPI the Trello instance.
     * @param frame     the frame to display the table.
     * @throws IOException throws exception.
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addHoursByCeremony(TrelloAPI trelloAPI, JFrame frame) throws IOException {
        var hoursPerUser = trelloAPI.getTotalHoursByUser("Ceremonies", "");

        var content = new String[hoursPerUser.size() + 2][3];
        var names = new String[]{"Members", "Nº Ceremonies", "Total Hours"};
        content[0] = names;

        for (var i = 0; i != hoursPerUser.size(); i++) {
            content[i + 1] = new String[]{hoursPerUser.get(i).getUser(),
                    String.valueOf(trelloAPI.getTotalNumberOfCeremonies()),
                    String.valueOf(hoursPerUser.get(i).getSpentHours())};
        }

        content[hoursPerUser.size() + 1] = new String[]{"Total",
                String.valueOf(trelloAPI.getTotalNumberOfCeremonies()),
                String.valueOf(trelloAPI.getTotalCeremonyHours())};

        var table = new JTable(content, names);
        table.setBounds(((frame.getWidth() / 2) + 150), ((frame.getHeight() / 2) + 70), 300, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.black);
        table.setShowGrid(true);

        frame.add(table);
    }

    /**
     * Function that adds a table with all the information regarding the commits
     * by all the users, on all the branches, ordered by:
     * user, branch, date.
     *
     * @param frame The frame to present the table.
     * @param gitHubAPI The instance of the {@link GitHubAPI}.
     * @throws IOException throws exception.
     * @author Rodrigo Guerreiro
     */
    public static void addCommitsTable(JFrame frame, GitHubAPI gitHubAPI) throws IOException{
        var editorPane = new JEditorPane();
        var scroller = new JScrollPane();

        editorPane.setContentType("text/html");
        editorPane.setText("<br></br><br></br>" + gitHubAPI.convert()[1] + "<br></br><br></br><br></br><br></br><br></br><br></br>");
        editorPane.setEditable(false);
        editorPane.setVisible(true);
        editorPane.setBounds(100, 100, 500, 600);
        frame.add(editorPane);

        scroller.setViewportView(editorPane);
        scroller.setBounds(frame.getWidth()/4, 0, ((2*frame.getWidth()) / 3), frame.getHeight());
        scroller.setBorder(createEmptyBorder());

        frame.add(scroller);
        frame.setVisible(true);
    }

    /**
     * Action event to update the value based on the new cost input by the user.
     *
     * @param e The event that occurred.
     * @author Rodrigo Guerreiro
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        var totalMoney = 0;
        var multiplier = (int)spinner.getValue();
        for (var i = 1; i != hoursPerUsers.size() + 1; i++) {
            var obj = this.hoursPerUsers.get(i - 1).getSpentHours() * multiplier;
            this.table.setValueAt(String.valueOf(obj), i, 3);
            totalMoney += obj;
        }

        this.table.setValueAt(String.valueOf(totalMoney), hoursPerUsers.size() + 1, 3);
        this.frame.repaint();
    }
}