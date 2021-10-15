package pt.iscte.iul;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DataReader {

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
        System.out.println(user_trello_info[1]);
        return user_trello_info;
    }

    public static String[] getUserGitData() {
        String[] user_git_info = {"", "", ""};
        int o = 0;
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


