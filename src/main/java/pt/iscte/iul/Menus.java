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
 * @author Rodrigo Guerreiro.
 */

public class Menus implements ActionListener {
    private final Map<TrelloAPI.List, JMenuItem> mapa_lists;
    private final Map<GitHubAPI.Collaborators, JMenuItem> mapa_cols;
    private final JFrame frame;
    private final GitHubAPI.Collaborators[] cols;
    private final JMenuBar mb;
    private final String boardID;
    private final String[] user_trello_info;
    private final String[] user_git_info;


    /**
     * Constructor method to inicialize the variables in order to create the menus.
     *
     * @author Rodrigo Guerreiro
     * @param frame The frame where the menus will be attached on
     * @param cols  The list of collaborators
     * @param user_trello_info String array with [trello_name, trello_key, trello_token]
     * @param user_git_info String array with[git_owner, git_repo, git_token]
     */

    public Menus(JFrame frame, GitHubAPI.Collaborators[] cols, String boardId, String[] user_trello_info, String[] user_git_info){
        this.mapa_cols = new HashMap<>();
        this.mapa_lists = new HashMap<>();
        this.frame = frame;
        this.cols = cols;
        this.boardID= boardId;
        this.mb = new JMenuBar();
        this.user_trello_info=user_trello_info;
        this.user_git_info = user_git_info;
        gitMenus();
        listsMenus();

        this.frame.setJMenuBar(mb);
    }

    /**
     * Function that creates the menus, regarding the collaborators and menu bars and attaches it to the frame.
     * Uses a map to store the different menus (one for each collaborator).
     *
     * @author Rodrigo Guerreiro
     */
    public void gitMenus(){
        JMenu colabs = new JMenu("Collaborators");

        for (GitHubAPI.Collaborators col : this.cols) {

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

    private void listsMenus(){
        JMenu listas = new JMenu("Listas");
        TrelloAPI tapi = new TrelloAPI(this.user_trello_info[0], this.user_trello_info[1], this.user_trello_info[2]);
        TrelloAPI.List[] Lists=null;

        try {
            Lists = tapi.getBoardLists(boardID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Lists != null) {
            for(TrelloAPI.List l : Lists){
                JMenuItem item = new JMenuItem(l.getName());
                mapa_lists.put(l, item);

                // TODO CRIAR UM SUBMENU EM CADA LISTA COM OS CARTOES TODOS (VER JAMBOARD PAG 16)
                //TODO PASSAR LOGO OS OBJETOS (API's) CRIADOS ???

                item.addActionListener(this);
                listas.add(item);
            }

        mb.add(listas);
        }
    }


    /**
     *
     * Performs an action based on which menu item was clicked.
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

        //action for the lists
        for (Map.Entry<TrelloAPI.List, JMenuItem> l : mapa_lists.entrySet()) {
            //Action.clearFrame(this.frame);
            if(e.getSource().equals(l.getValue())){
                System.out.println(l.getKey().getName());
            }
        }
    }

}
