package pt.iscte.iul;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Just the main class.
 *
 * @author Rodrigo Guerreiro.
 */
public class Main {

    /**
     * Main function to start the application.
     * Sees if the data file existe and if so reads the data.
     * Shows a pop-up window with a yes/no question.
     * If answer == yes loads the app with the saved data.
     * If answer == no proceed with the standard load.
     * If there is no data file also does the standard load.
     * If flag==1 then it will scale the main frame.
     *
     * @param args nothing.
     * @throws IOException throws exception
     * @author Rodrigo Guerreiro
     */
    public static void main(String[] args) throws IOException {
        var frame = new JFrame();
        var data = "data/user_data.txt";
        var dataFile = new File(data);
        if (DataSaver.exists(dataFile) && dataFile.length() != 0) {
            var opc = HomeUI.pop();
            if (opc == JOptionPane.YES_OPTION) {
                var trello = DataReader.getUserTrelloData(data);
                var git = DataReader.getUserGitData(data);

                HomeUI.showFrame(frame);
                Action.doAction(frame, git, trello, 1);
            }
        } else {
            new HomeUI();
        }
    }
}
