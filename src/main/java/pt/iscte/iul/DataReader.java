package pt.iscte.iul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Class to Read and decode the user information stored on a .txt file
 *
 * @author Rodrigo Guerreiro.
 */
public class DataReader {

    /**
     * This function reads the data saved in the .txt file regarding the GitHub api.
     * And returns an array with that data.
     *
     * @param data the name of the file to read the info.
     * @return user_git_info. An array with the data saved in the .txt file, regarding the GitHUb api.
     * @author Rodrigo Guerreiro
     */
    public static String[] getUserGitData(String data) {
        String[] userGitInfo = {"", "", ""};
        try {
            File file = new File(data);
            Scanner sc = new Scanner(file);
            userGitInfo[0] = Encoding.decode(sc.nextLine());
            userGitInfo[1] = Encoding.decode(sc.nextLine());
            userGitInfo[2] = Encoding.decode(sc.nextLine());
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return userGitInfo;
    }


    /**
     * This function reads the data saved in the .txt file regarding the Trello api.
     * And returns an array with that data.
     *
     * @param data the name of the file to read the info.
     * @return user_trello_info. An array with the data saved in the .txt file, regarding the Trello api.
     * @throws FileNotFoundException throws exception
     * @author Rodrigo Guerreiro
     */
    public static String[] getUserTrelloData(String data) throws FileNotFoundException {
        String[] userTrelloInfo = {"", "", ""};

        File file = new File(data);
        Scanner sc = new Scanner(file);

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


