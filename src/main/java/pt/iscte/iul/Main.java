package pt.iscte.iul;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
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
     * @throws IOException
     * @author Rodrigo Guerreiro
     */
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        String data = "data/user_data.txt";
        File dataFile = new File(data);
        if (DataSaver.exists(dataFile) && dataFile.length() != 0) {
            int opc = HomeUI.pop();
            if (opc == JOptionPane.YES_OPTION) {
                String[] trello = DataReader.getUserTrelloData(data);
                String[] git = DataReader.getUserGitData(data);

                HomeUI.showFrame(frame);
                Action.doAction(frame, git, trello, 1);
            } /*else if (opc == JOptionPane.NO_OPTION) {
                new HomeUI();
            }
            */
        } else {
            new HomeUI();
        }
    }
}
