package pt.iscte.iul;

import com.mashape.unirest.http.exceptions.UnirestException;
import javax.swing.*;
import java.awt.*;


public class Action {

    /**
     *
     * @param  user_git_info String array with[git_owner, git_repo, git_token,]
     *
     * @param user_trello_info String array with [trello_user, trello_key, trello_token]
     *
     *
     */

    public static void do_action(JFrame frame, String[] user_git_info, String[] user_trello_info){

        try {
            TrelloApi.get_info(user_git_info,user_trello_info);
        } catch (UnirestException ex){
            ex.printStackTrace();
        }

        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        frame.setLocation(0,0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height-100);

        for (String s : user_git_info) {
            System.out.println(s);
        }
        for(String s : user_trello_info){
            System.out.println(s);
        }

    }
}
