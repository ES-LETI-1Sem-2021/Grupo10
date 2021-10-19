package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import com.github.rjeschke.txtmark.*;


public class Action {

    /**
     *
     * Function that does everything.
     * Connects the UI's to the api's.
     *
     * @param  user_git_info String array with[git_owner, git_repo, git_token,]
     * @param user_trello_info String array with [trello_user, trello_key, trello_token]
     * @author Rodrigo Guerreiro
     *
     */

    public static void do_action(JFrame frame, String[] user_git_info, String[] user_trello_info){

        var gitApi = new GitHubAPI(user_git_info[0],user_git_info[1],user_git_info[2]);
        String readme= null;
        try {
            readme = gitApi.getFile("master","/README.md");
            //gitApi.getCollaborators();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(readme);



        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        frame.setLocation(0,0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height-100);

        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(readme);
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(100,100,500,300);
        frame.add(edt);
        frame.setVisible(true);

        /*
        JFrame jframe = new JFrame();


        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html"); */
        //editorPane.setText(/*txtmark.Processor.process(readme)*/readme);
       /* editorPane.setVisible(true);


        jframe.add(editorPane);
        

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(1000,1000);

        jframe.setVisible(true);
        */

        

        for (String s : user_git_info) {
            System.out.println(s);
        }
        for(String s : user_trello_info){
            System.out.println(s);
        }

    }

    /**
     *
     * Function that calls another function in order to save the user data in a .txt file
     *
     * @param user_git_info info needed for git api.
     * @param user_trello_info info needed for trello api.
     * @author Rodrigo Guerreiro
     */

    public static void save_data(String[] user_git_info, String[] user_trello_info) {
        DataSaver.save(user_git_info,user_trello_info);
    }
}
