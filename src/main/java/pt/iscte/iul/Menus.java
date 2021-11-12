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

/**
 * Classe that creates the menus to be added onto the frame.
 *
 * @author Rodrigo Guerreiro.
 */

public class Menus implements ActionListener {

    private final JFrame frame;
    private final String boardID;
    private final JMenuBar mb;
    private final GitHubAPI gapi;
    private final TrelloAPI tapi;
    private final List<itemCard<TrelloAPI.Card>> arrayCards= new ArrayList<>();
    private final List<itemCard<GitHubAPI.Collaborators>> arraycolabs= new ArrayList<>();


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
     *
     * @throws IOException exception.
     * @author Rodrigo Guerreiro
     */
    public void gitMenus() throws IOException{
        JMenu colabs = new JMenu("Collaborators");
        GitHubAPI.Collaborators[] cols = gapi.getCollaborators();
        JMenuItem item;

        for (GitHubAPI.Collaborators col : cols) {
            item = getjMenuItem(col);

            item.addActionListener(this);
            colabs.add(item);
            arraycolabs.add(new itemCard<>(col,item));
            //mapa_cols.put(col, item);

        }
        mb.add(colabs);
    }

    /**
     * Creates an item associated with a collaborator.
     *
     * @param col the collaborator
     * @return the item associated with the collaborator
     */
    @NotNull
    private JMenuItem getjMenuItem(GitHubAPI.Collaborators col) {
        JMenuItem item;
        if(col.getName() == null){
            item = new JMenuItem(col.getLogin());
        }
        else{
            item = new JMenuItem(col.getName());
        }
        return item;
    }

    /**
     * Um record que associa um objeto a um menuItem.
     *
     * @param object the object to associate to the menu item.
     * @param item the item to associate to the object.
     * @param <T> The type of object to associate to an item.
     */

    private record itemCard<T>(T object, JMenuItem item) {

        /**
         * Getter for the menuItem.
         *
         * @return item --> The menu item to be returned.
         */
        public JMenuItem getItem() {
            return item;
        }

        /**
         *
         * @return object --> The object to be returned.
         */
        public T getObject() {
            return object;
        }
    }


    /**
     *
     * Function that creates a menu item for each list in the board.
     * Also creates a submenu on each item, regarding the cards on that list.
     *
     * @throws IOException exception.
     * @author Rodrigo Guerreiro
     *
     */

    private void listsMenus() throws IOException{
        JMenu listas = new JMenu("Listas");

        TrelloAPI.List[] Lists;
        TrelloAPI.Card[] cards;

        Lists = tapi.getBoardLists(boardID);

        if (Lists != null) {
            for(TrelloAPI.List l : Lists){
                JMenu listMenu = new JMenu(l.getName());
                cards = tapi.getListCards(l.getId());

                for (TrelloAPI.Card c : cards) {
                    JMenuItem item2 = new JMenuItem(c.getName());
                    arrayCards.add(new itemCard<>(c,item2));
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
        this.arraycolabs.forEach(collaboratorsitem ->{
            if(e.getSource()==collaboratorsitem.getItem()){
                try {
                    Desktop.getDesktop().browse(new URL(collaboratorsitem.getObject().getProfile()).toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        //action for the clicks on a card from a list
        this.arrayCards.forEach(carditemCard-> {
            if(e.getSource() == carditemCard.getItem()){
                new CardUI(carditemCard.getObject(), this.frame);
            }
        });

    }

}
