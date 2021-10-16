package pt.iscte.iul;

import javax.swing.*;
import java.io.File;

public class Main {

    /**
     * Main function to start the application.
     * Sees if the data file existe and if so reads the data.
     * Shows a pop-up window with a yes/no question.
     * If answer == yes loads the app with the saved data.
     * If answer == no proceed with the standard load.
     * If there is no data file also does the standard load.
     *
     * @param args nothing.
     * @author Rodrigo Guerreiro
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        if (DataSaver.exists(new File("data/user_data.txt"))){
            String[] trello = DataReader.getUserTrelloData();
            String[] git = DataReader.getUserGitData();
            int opc = HomeUI.pop();
            if (opc ==JOptionPane.YES_OPTION){
                HomeUI.show_frame(frame);
                Action.do_action(frame, git, trello);
            }else if(opc == JOptionPane.NO_OPTION){
                new HomeUI();

            }

        }else
        new HomeUI();
    }
}
