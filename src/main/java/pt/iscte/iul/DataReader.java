package pt.iscte.iul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class to read and decode the user information stored on a .txt file.
 *
 * @author Rodrigo Guerreiro.
 */
public class DataReader {

    /**
     * Function that reads the data saved in the .txt file regarding the GitHub API
     * and returns an array with that data.
     *
     * @param data The name of the file to read the info.
     * @return userGitInfo An array with the data saved in the .txt file, regarding the GitHUb API.
     * @throws FileNotFoundException throws exception.
     * @author Rodrigo Guerreiro
     */
    public static String[] getUserGitData(String data) throws FileNotFoundException {
        var userGitInfo = new String[]{"", "", ""};

        var file = new File(data);
        var sc = new Scanner(file);
        userGitInfo[0] = Encoding.decode(sc.nextLine());
        userGitInfo[1] = Encoding.decode(sc.nextLine());
        userGitInfo[2] = Encoding.decode(sc.nextLine());
        sc.close();

        return userGitInfo;
    }


    /**
     * Function that reads the data saved in the .txt file regarding the Trello API
     * and returns an array with that data.
     *
     * @param data The name of the file to read the info.
     * @return userTrelloInfo An array with the data saved in the .txt file, regarding the Trello API.
     * @throws FileNotFoundException throws exception.
     * @author Rodrigo Guerreiro
     */
    public static String[] getUserTrelloData(String data) throws FileNotFoundException {
        var userTrelloInfo = new String[]{"", "", ""};

        var file = new File(data);
        var sc = new Scanner(file);

        sc.nextLine();
        sc.nextLine();
        sc.nextLine();

        userTrelloInfo[0] = Encoding.decode(sc.nextLine());
        userTrelloInfo[1] = Encoding.decode(sc.nextLine());
        userTrelloInfo[2] = Encoding.decode(sc.nextLine());

        sc.close();

        return userTrelloInfo;
    }
}


