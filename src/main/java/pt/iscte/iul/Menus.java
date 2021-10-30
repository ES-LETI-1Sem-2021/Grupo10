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

public  class Menus implements ActionListener {
    private static Map<GitHubAPI.Collaborators, JMenuItem> mapa;
    private final JFrame frame;
    private final GitHubAPI.Collaborators[] cols;


    public Menus(JFrame frame, GitHubAPI.Collaborators[] cols){
        mapa = new HashMap<>();
        this.frame = frame;
        this.cols = cols;
        addMenus();
    }
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
