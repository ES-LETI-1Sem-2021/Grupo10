package pt.iscte.iul;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        if (DataSaver.exists(new File("data/user_data.txt"))){

            String[] trello = DataReader.getUserTrelloData();
            String[] git = DataReader.getUserGitData();
            HomeUI.pop();
            HomeUI.show_frame(frame);
            Action.do_action(frame, git, trello);

        }else
        new HomeUI();
    }
}
