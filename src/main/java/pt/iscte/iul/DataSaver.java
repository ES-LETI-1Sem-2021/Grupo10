package pt.iscte.iul;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class to save and encode (BASE64) the user information on a .txt file.
 *
 * @author Rodrigo Guerreiro.
 */

public class DataSaver {
    /**
     * This function se if the file exists and if not it crates the file.
     * Calls the write function so write the data in the file.
     *
     * @param userGitInfo    an array with data so save in a .txt file regarding GitHub api.
     * @param userTrelloInfo an array with data so save in a .txt file regarding trello api.
     * @param filename       the name of the file to save the info.
     * @author Rodrigo Guerreiro
     */
    public static void save(String[] userGitInfo, String[] userTrelloInfo, String filename) {

        try {
            File file = new File(filename);
            //if (!file.exists()) {
                //file.createNewFile();
            //}
            write(userGitInfo, userTrelloInfo, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function writes the user data received in the parameter in a file that is also passed in the parameters
     *
     * @param userGitInfo    an array with data so save in a .txt file regarding GitHub api.
     * @param userTrelloInfo an array with data so save in a .txt file regarding trello api.
     * @param file           the file where the data is going to be written.
     * @throws IOException if it can't write in the file.
     * @author Rodrigo Guerreiro
     */
    private static void write(String[] userGitInfo, String[] userTrelloInfo, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        for (String g : userGitInfo) {
            fileWriter.write(Encoding.encode(g) + "\n");
        }

        for (String t : userTrelloInfo) {
            fileWriter.write(Encoding.encode(t) + "\n");
        }

        fileWriter.close();
    }

    /**
     * @param file the file we need to know exists.
     * @return True if the file exists.
     * @author Rodrigo Guerreiro
     */
    public static boolean exists(File file) {
        return file.exists();
    }
}
