package pt.iscte.iul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataReader {

    /**
     *  This function reads the data saved in the .txt file regarding the trello api.
     *  And returns an array with that data.
     *
     * @return user_trello_info. An array with the data saved in the .txt file, regarding the trello api.
     * @throws FileNotFoundException  if there is no file to read
     * @author Rodrigo Guerreiro
     *
     */

    public static String[] getUserTrelloData() {
        String[] user_trello_info = {"", "", ""};
        try {
            File file = new File("data/user_data.txt");
            Scanner sc = new Scanner(file);
            user_trello_info[0] = sc.nextLine();
            user_trello_info[1] = sc.nextLine();
            user_trello_info[2] = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return user_trello_info;
    }


    /**
     *
     *  This function reads the data saved in the .txt file regarding the GitHub api.
     *  And returns an array with that data.
     *
     * @return user_git_info. An array with the data saved in the .txt file, regarding the GitHub api.
     * @throws FileNotFoundException  if there is no file to read
     * @author Rodrigo Guerreiro
     *
     */
    public static String[] getUserGitData() {
        String[] user_git_info = {"", "", ""};
        try {
            File file = new File("data/user_data.txt");
            Scanner sc = new Scanner(file);

            String foo = sc.nextLine();
            foo = sc.nextLine();
            foo = sc.nextLine();
            user_git_info[0] = sc.nextLine();
            user_git_info[1] = sc.nextLine();
            user_git_info[2] = sc.nextLine();
            sc.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(user_git_info[1]);
        return user_git_info;
    }
}


