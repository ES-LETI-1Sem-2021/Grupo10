package pt.iscte.iul;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataSaver {

    /**
     * This function se if the file exists and if not it crates the file.
     * Calls the write function so write the data in the file.
     *
     * @param user_git_info an array with data so save in a .txt file regarding GitHub api.
     * @param user_trello_info an array with data so save in a .txt file regarding trello api.
     * @throws IOException if it can't create the file.
     * @author Rodrigo Guerreiro
     *
     */



    public static void save(String[] user_git_info, String[] user_trello_info, String filename) {

        try{
        File file = new File(filename);
        if(!file.exists()){
            file.createNewFile();
        }
            write(user_git_info,user_trello_info,file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This function writes the user data received in the parameter in a file that is also passed in the parameters
     *
     * @param user_git_info an array with data so save in a .txt file regarding GitHub api.
     * @param user_trello_info an array with data so save in a .txt file regarding trello api.
     * @param file the file where the data is going to be written.
     * @throws IOException if it can't write in the file.
     * @author Rodrigo Guerreiro
     *
     */

    private static void write(String[] user_git_info, String[] user_trello_info, File file ) throws IOException{

            FileWriter fileWriter = new FileWriter(file);
            for (String g : user_git_info)
                fileWriter.write(Encoding.Encode(g) + "\n");

            for (String t : user_trello_info)
                fileWriter.write(Encoding.Encode(t) + "\n");

            fileWriter.close();

    }

    /**
     *
     * @param file the file we need to know exists.
     * @return True if the file exists.
     * @author Rodrigo Guerreiro
     */


    public static boolean exists(File file){
        return file.exists();
    }

}
