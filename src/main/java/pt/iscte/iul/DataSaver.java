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
     * Function to see if the file exists (if not, it creates the file).
     * Calls the write function to write the data in the file.
     *
     * @param userGitInfo    an array with data to save in a .txt file regarding GitHub API.
     * @param userTrelloInfo an array with data to save in a .txt file regarding Trello API.
     * @param filename       the name of the file where the info is saved.
     * @author Rodrigo Guerreiro
     */
    public static void save(String[] userGitInfo, String[] userTrelloInfo, String filename) throws IOException {
        var file = new File(filename);
        write(userGitInfo, userTrelloInfo, file);
    }

    /**
     * Function that writes the user data received in the parameter in a file that is also passed in the parameters
     *
     * @param userGitInfo    an array with data to save in a .txt file regarding GitHub API.
     * @param userTrelloInfo an array with data to save in a .txt file regarding Trello API.
     * @param file           the file where the data is going to be written.
     * @throws IOException if it can't write in the file.
     * @author Rodrigo Guerreiro
     */
    private static void write(String[] userGitInfo, String[] userTrelloInfo, File file) throws IOException {
        var fileWriter = new FileWriter(file);
        for (var g : userGitInfo) {
            fileWriter.write(Encoding.encode(g) + "\n");
        }

        for (var t : userTrelloInfo) {
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
