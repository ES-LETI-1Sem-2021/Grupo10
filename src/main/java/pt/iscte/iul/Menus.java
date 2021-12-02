package pt.iscte.iul;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that creates the menus to be added onto the frame.
 *
 * @author Rodrigo Guerreiro.
 */

public class Menus implements ActionListener {
    private final JFrame frame;
    private final JMenuBar menuBar;
    private final GitHubAPI gitHubAPI;
    private final TrelloAPI trelloAPI;
    private JMenuItem[] optionsMenus;
    private JMenuItem commitsTable;
    private final List<ItemCard<TrelloAPI.Card>> arrayCards = new ArrayList<>();
    private final List<ItemCard<GitHubAPI.Collaborators>> arrayColabs = new ArrayList<>();


    /**
     * Constructor method to inicialize the variables in order to create the menus.
     *
     * @param frame     The frame where the menus will be attached on.
     * @param gitHubAPI Instance of GitHub Api.
     * @param trelloAPI Instance of Trello Api.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     */
    public Menus(JFrame frame, GitHubAPI gitHubAPI, TrelloAPI trelloAPI) throws IOException {
        this.frame = frame;
        this.gitHubAPI = gitHubAPI;
        this.trelloAPI = trelloAPI;

        this.menuBar = new JMenuBar();

        optionsMenus();
        gitMenus();
        listsMenus();

        this.frame.setJMenuBar(menuBar);
    }

    /**
     * Creates the option's menu with all of its submenus.
     * Stores in an array with the following content:
     * - optionsMenus[Default_screen, clear_userData, logout]
     *
     * @author Rodrigo Guerreiro
     */
    private void optionsMenus() {
        var options = new JMenu("Options");
        this.optionsMenus = new JMenuItem[4];

        var defaultScreen = new JMenuItem("Home Screen");
        defaultScreen.addActionListener(this);
        options.add(defaultScreen);
        this.optionsMenus[0] = defaultScreen;

        var exportToCsv = new JMenuItem("Export to CSV");
        exportToCsv.addActionListener(this);
        options.add(exportToCsv);
        this.optionsMenus[1] = exportToCsv;

        var clearCache = new JMenuItem("Clear data file");
        clearCache.addActionListener(this);
        options.add(clearCache);
        this.optionsMenus[2] = clearCache;

        var logout = new JMenuItem("Logout");
        logout.addActionListener(this);
        options.add(logout);
        this.optionsMenus[3] = logout;

        menuBar.add(options);
    }

    /**
     * Function that creates the menus, regarding the collaborators and menu bars and attaches it to the frame.
     *
     * @throws IOException exception.
     * @author Rodrigo Guerreiro
     */
    public void gitMenus() throws IOException {
        var colabs = new JMenu("Collaborators");
        for (var col : gitHubAPI.getCollaborators()) {
            var item = getjMenuItem(col);

            item.addActionListener(this);
            colabs.add(item);
            arrayColabs.add(new ItemCard<>(col, item));
        }
        var commits = new JMenu("Commits");
        this.commitsTable = new JMenuItem("Commits Table");
        this.commitsTable.addActionListener(this);
        commits.add(commitsTable);
        
        menuBar.add(commits);
        menuBar.add(colabs);
    }

    /**
     * Creates an item associated with a collaborator.
     *
     * @param col the collaborator.
     * @return the item associated with the collaborator.
     */
    @NotNull
    private JMenuItem getjMenuItem(GitHubAPI.Collaborators col) {
        return col.getName() == null ? new JMenuItem(col.getLogin()) : new JMenuItem(col.getName());
    }

    /**
     * A record that associates an object <T> to a menuItem.
     *
     * @param object the object to associate to the menu item.
     * @param item   the item to associate to the object.
     * @param <T>    The type of object to associate to an item.
     */
    private record ItemCard<T>(T object, JMenuItem item) {
        /**
         * Getter for the menuItem.
         *
         * @return item --> The menu item to be returned.
         */
        public JMenuItem getItem() {
            return item;
        }

        /**
         * @return object --> The object to be returned.
         */
        public T getObject() {
            return object;
        }
    }


    /**
     * Function that creates a menu item for each list in the board.
     * Also creates a submenu on each item, regarding the cards on that list.
     *
     * @throws IOException exception.
     * @author Rodrigo Guerreiro
     */
    private void listsMenus() throws IOException {
        var listas = new JMenu("Listas");

        var lists = trelloAPI.getBoardLists();

        if (lists != null) {
            for (var l : lists) {
                var listMenu = new JMenu(l.getName());
                var cards = trelloAPI.getListCards(l.getId());

                for (var card : cards) {
                    var item2 = new JMenuItem(card.getName());
                    arrayCards.add(new ItemCard<>(card, item2));
                    item2.addActionListener(this);
                    listMenu.add(item2);
                }
                listas.add(listMenu);
            }
            menuBar.add(listas);
        }
    }

    /**
     * Performs an action based on which menu item was clicked.
     * If the user clicks on a collaborator item it will redirect to the GitHub page.
     * If the user clicks on the Lists menus it will show all the lists. Each list is a submenu,
     * On each submenu it will show all the cards from that list.
     *
     * @param e the events that happens when the user clicks on a collaborator name.
     * @author Rodrigo Guerreiro
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //Action performed regarding GitHub
        try{
            gitActionPerformed(e);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        //action for the clicks on a card from a list
        listsActionPerformed(e);

        //Options menus
        for (var opm : optionsMenus) {
            if (e.getSource() == opm) {
                try {
                    optionsActionPerformed(opm);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    /**
     * Method that based on which menu item was clicked performs an action regarding the options menus list.
     *
     * @param opm the JMenuItem that was clicked.
     * @author Rodrigo Guerreiro
     */
    private void optionsActionPerformed(@NotNull JMenuItem opm) throws IOException {
        switch (opm.getText()) {
            case "Home Screen" -> {
                Action.clearFrame(frame);
                Action.homeScreen(this.frame, this.gitHubAPI, this.trelloAPI);
            }
            case "Clear data file" -> Action.clearTheFile("data/user_data.txt");
            case "Logout" -> {
                Action.clearTheFile("data/user_data.txt");
                this.frame.dispose();
                new HomeUI();
            }
            case "Export to CSV" -> {
                System.out.println("Export to csv");
                // TODO add export functions here (TRABALHA OLEK)
            }
            default -> {
            }
        }
    }

    /**
     * Method that based on which menu item was clicked show the trello card on the frame.
     *
     * @param e action event.
     * @author Rodrigo Guerreiro
     */
    private void listsActionPerformed(ActionEvent e) {
        this.arrayCards.stream().filter(carditemCard -> e.getSource() == carditemCard.getItem())
                .forEach(carditemCard -> {
                    try {
                        CardUI thread = new CardUI(carditemCard.getObject(), this.frame, trelloAPI);
                        thread.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
    }

    /**
     * Method that based on which menu item was clicked redirects to the person's GitHub page.
     * If it was the commits tab it shows the commits table.
     *
     * @param e action event.
     * @author Rodrigo Guerreiro
     */
    private void gitActionPerformed(ActionEvent e) throws IOException {
        this.arrayColabs.stream().filter(collaboratorsitem -> e.getSource() == collaboratorsitem.getItem())
                .forEach(collaboratorsitem -> {
                    try {
                        Desktop.getDesktop().browse(new URL(collaboratorsitem.getObject().getProfile()).toURI());
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                });

        if(Objects.equals(e.getActionCommand(), this.commitsTable.getText())){
            Action.clearFrame(this.frame);
            JElements.addCommitsTable(frame, this.gitHubAPI);
        }
    }

}