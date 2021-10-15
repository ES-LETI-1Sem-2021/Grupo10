package pt.iscte.iul;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataSaver {

    public static void save(String[] user_git_info, String[] user_trello_info) {

        try{
        File file = new File("data/user_data.txt");
        if(file.exists()){
            System.out.println("yay");
            write(user_git_info,user_trello_info,file);
        }else {
            System.out.println("nay");
            file.createNewFile();
            write(user_git_info,user_trello_info,file);
        }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void write(String[] user_git_info, String[] user_trello_info, File file ){
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (String g : user_git_info)
                fileWriter.write(g + "\n");


            for (String t : user_trello_info)
                fileWriter.write(t + "\n");

            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
