package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe that creates the menus to be added onto the frame.
 *
 * @author Rodrigo Guerreiro.
 */

public class Menus implements ActionListener {

    private final Map<TrelloAPI.Card,JMenuItem> mapCards;
    private final Map<GitHubAPI.Collaborators, JMenuItem> mapa_cols;
    private final JFrame frame;
    private final String boardID;
    private final JMenuBar mb;
    private final GitHubAPI gapi;
    private final TrelloAPI tapi;

    /**
     * Constructor method to inicialize the variables in order to create the menus.
     *
     * @author Rodrigo Guerreiro
     * @param frame The frame where the menus will be attached on.
     * @param gapi Instance of GitHub Api.
     * @param tapi Instance of Trello Api.
     * @param boardID The ID of the board to get the lists and cards.
     */

    public Menus(JFrame frame, GitHubAPI gapi, TrelloAPI tapi, String boardID) {

        this.mapa_cols = new HashMap<>();
        this.mapCards = new HashMap<>();

        this.frame = frame;
        this.gapi = gapi;
        this.tapi = tapi;

        this.boardID = boardID;

        this.mb = new JMenuBar();

        try{
            gitMenus();
            listsMenus();
       }catch (IOException e){
           e.printStackTrace();
       }
        this.frame.setJMenuBar(mb);
    }

    /**
     * Function that creates the menus, regarding the collaborators and menu bars and attaches it to the frame.
     * Uses a map to store the different menus (one for each collaborator).
     *
     * @author Rodrigo Guerreiro
     */
    public void gitMenus() throws IOException{
        JMenu colabs = new JMenu("Collaborators");
        GitHubAPI.Collaborators[] cols = gapi.getCollaborators();

        for (GitHubAPI.Collaborators col : cols) {
            JMenuItem item = new JMenuItem(col.getName());
            mapa_cols.put(col, item);

            item.addActionListener(this);
            colabs.add(item);
        }
        mb.add(colabs);
    }

    /**
     *
     * Function that creates a menu item for each list in the board.
     * Also creates a submenu on each item, regarding the cards on that list.
     *
     * @author Rodrigo Guerreiro
     *
     */

    private void listsMenus() throws IOException{
        JMenu listas = new JMenu("Listas");
        //TrelloAPI tapi = new TrelloAPI(this.user_trello_info[0], this.user_trello_info[1], this.user_trello_info[2]);
        TrelloAPI.List[] Lists;
        TrelloAPI.Card[] cards;

        Lists = tapi.getBoardLists(boardID);

        if (Lists != null) {
            for(TrelloAPI.List l : Lists){
                JMenu listMenu = new JMenu(l.getName());

                cards = tapi.getListCards(l.getId());

                for (TrelloAPI.Card c : cards) {
                    JMenuItem item2 = new JMenuItem(c.getName());
                    mapCards.put(c,item2);
                    item2.addActionListener(this);
                    listMenu.add(item2);
                }
                listas.add(listMenu);
            }
        mb.add(listas);
        }
    }

    /**
     *
     * Performs an action based on which menu item was clicked.
     * If the user clicks on a collaborator item it will redirect to the GitHub page.
     * If the user clicks on the Lists menus it will show all the lists. Each list is a submenu,
     * On each submenu it will show all the cards from that list.
     *
     * @author Rodrigo Guerreiro
     * @param e the events that happens when the user clicks on a collaborator name.
     */

    @Override
    public void actionPerformed(ActionEvent e) {

        //redirect for the GitHub page
        for (Map.Entry<GitHubAPI.Collaborators, JMenuItem> entry : mapa_cols.entrySet()) {
            if(e.getSource().equals(entry.getValue())){
                try {
                    Desktop.getDesktop().browse(new URL(entry.getKey().getProfile()).toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }

        //action for the lists based on the card clicked
        for (Map.Entry<TrelloAPI.Card, JMenuItem> c : mapCards.entrySet()) {
            if(e.getSource().equals(c.getValue())){
                new CardUI(c.getKey(), this.frame);
            }
        }
    }

}
