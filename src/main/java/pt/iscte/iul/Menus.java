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
    private static Map<GitHubAPI.Collaborators, JMenuItem> mapa;
    private final JFrame frame;
    private final GitHubAPI.Collaborators[] cols;


    /**
     * Constructor method to inicialize the variables in order to create the menus.
     *
     * @author Rodrigo Guerreiro
     * @param frame The frame where the menus will be attached on
     * @param cols  The list of collaborators
     */

    public Menus(JFrame frame, GitHubAPI.Collaborators[] cols){
        mapa = new HashMap<>();
        this.frame = frame;
        this.cols = cols;
        addMenus();
    }

    /**
     * Function that creates the menus and menu bars and attaches it to the frame.
     * Uses a map to store the different menus (one for each collaborator).
     *
     * @author Rodrigo Guerreiro
     */
    public void addMenus(){
        JMenuBar mb = new JMenuBar();
        JMenu colabs = new JMenu("Collaborators");

        for (GitHubAPI.Collaborators col : this.cols) {

            JMenuItem item = new JMenuItem(col.getName());
            mapa.put(col, item);

            item.addActionListener(this);
            colabs.add(item);
        }

        mb.add(colabs);
        this.frame.setJMenuBar(mb);
    }

    /**
     *
     * Sees witch menu was clicked and redirects the user to the menu 'owner' GitHub page.
     *
     * @author Rodrigo Guerreiro
     * @param e the events that happens when the user clicks on a collaborator name.
     */


    @Override
    public void actionPerformed(ActionEvent e) {

        for (Map.Entry<GitHubAPI.Collaborators, JMenuItem> entry : mapa.entrySet()) {
            if(e.getSource().equals(entry.getValue())){
                        try {
                            Desktop.getDesktop().browse(new URL(entry.getKey().getProfile()).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                }
            }
        }
    }

}
