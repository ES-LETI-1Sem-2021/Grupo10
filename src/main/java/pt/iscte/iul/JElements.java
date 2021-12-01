package pt.iscte.iul;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class JElements implements ActionListener {

    private final JFrame frame;
    private final JSpinner spinner;
    private final JButton button;
    private final ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers;
    private JTable table;
    private String[][] data;

    public JElements(JFrame frame, ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers) {
        this.frame = frame;
        this.hoursPerUsers = hoursPerUsers;

        //Spinner
        SpinnerModel model = new SpinnerNumberModel(20, 20, 100, 1);
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

    //Table
    public void addTable(ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers, JFrame frame, JSpinner spinner) {
        double totalEstimated = 0;
        double totalSpent = 0;

        this.data = new String[hoursPerUsers.size() + 2][4];
        String[] names = {"Member", "Estimated Hours", "Spent Hours", "Cost (€)"};

        data[0] = names;

        for (int cont = 0; cont != hoursPerUsers.size(); cont++) {
            data[cont + 1] = new String[]{hoursPerUsers.get(cont).getUser(),
                    String.valueOf(hoursPerUsers.get(cont).getEstimatedHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours() * 20)};
            totalEstimated += hoursPerUsers.get(cont).getEstimatedHours();
            totalSpent += hoursPerUsers.get(cont).getSpentHours();
        }

        double totalCost = totalSpent * (int) spinner.getValue();

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
     * Function that adds a pie chart to the ui as well as a table with the same information.
     * This function, by calling the getTotalHoursByUser severely increases the processing time.
     *
     * @param frame      the frame to display the table
     * @param sprintName the name of the sprint blank if total.
     * @param trelloAPI  the trello instance.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addHoursInfo(JFrame frame, String sprintName, TrelloAPI trelloAPI) throws IOException {
        ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers = trelloAPI.getTotalHoursByUser("", sprintName);

        // Pie Charts
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        DefaultPieDataset<String> spentHoursDataset = new DefaultPieDataset<>();

        hoursPerUsers.forEach(e -> {
            dataset.setValue(e.getUser(), e.getEstimatedHours());
            spentHoursDataset.setValue(e.getUser(), e.getEstimatedHours());
        });
        JFreeChart chart = ChartFactory.createPieChart(
                "Hours Estimated by user " + sprintName,
                dataset, true, true, false);

        JFreeChart spentHoursChart = ChartFactory.createPieChart(
                "Hours Spent by user " + sprintName,
                dataset, true, true, false);
        ChartPanel cp = new ChartPanel(chart);
        cp.setBounds(((frame.getWidth() / 2) - 30), 0, 300, 250);
        cp.setVisible(true);
        chart.getPlot().setBackgroundPaint(Color.white);
        chart.getPlot().setOutlinePaint(Color.white);
        chart.getTitle().setPaint(new Color(68, 114, 196));

        ChartPanel spentCP = new ChartPanel(spentHoursChart);
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
     * Function that adds a table with the start and end date of each sprint
     *
     * @param trelloAPI the trello instance
     * @param frame     the frame to display the table
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     * @author Duarte Casaleiro
     */
    public static void addSprintDatesTable(TrelloAPI trelloAPI, JFrame frame) throws IOException {

        int numberOfSprints = trelloAPI.queryLists("Done - Sprint").size();

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
     * Function that adds a table with the hours spent by the team and by member in ceremonies
     *
     * @param trelloAPI the trello instance
     * @param frame     the frame to display the table
     * @throws IOException throws exception
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
