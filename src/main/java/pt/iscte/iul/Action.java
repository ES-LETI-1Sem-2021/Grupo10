package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


/**
 * Classe where all the magic happens.
 *
 * @author Rodrigo Guerreiro.
 */
public class Action {






    /**
     *
     * Function that does everything.
     * Connects the UI's to the api's.
     *
     * @param frame The frame to attach the info.
     * @param  user_git_info String array with[git_owner, git_repo, git_token]
     * @param user_trello_info String array with [trello_name, trello_key, trello_token]
     * @param flag flag == 1 if it is needed to scale the window size, any number otherwise.
     * @author Rodrigo Guerreiro
     *
     */
    public static void do_action(JFrame frame, String[] user_git_info, String[] user_trello_info, int flag){
        var gitApi = new GitHubAPI(user_git_info[0],user_git_info[1],user_git_info[2]);
        var trelloAPI = new TrelloAPI(user_trello_info[0],user_trello_info[1],user_trello_info[2]);

        String boardID = null;
        try {
            TrelloAPI.Board[] boards = trelloAPI.getBoards();
            for (TrelloAPI.Board b: boards) {
                if(b.getName().equals(user_trello_info[0])){
                    boardID = b.getId();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFrame(frame);
        //Adiciona os menus
        new Menus(frame, gitApi, trelloAPI, boardID);

        // aumenta o tamanho do ecra apenas usado a primeira vez que esta função é executada
        if(flag == 1) {
            frame.setLocation(0, 0);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screenSize.width, screenSize.height - 100);
        }
        //if (dataInicio == null) throw new AssertionError();
        homeScreen(frame, gitApi, trelloAPI);

    }

    /**
     *
     * Function that calls another function in order to save the user data in a .txt file
     *
     * @param user_git_info info needed for git api.
     * @param user_trello_info info needed for trello api.
     * @author Rodrigo Guerreiro
     */

    public static void save_data(String[] user_git_info, String[] user_trello_info) {
        DataSaver.save(user_git_info,user_trello_info, "data/user_data.txt");
    }

    /**
     * Function that converts a give string (in markdown) to the same string formatted in html.
     *
     * @param markdown the string in markdown
     * @return the string 'markdown' converted for html
     */

    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    /**
     * Function that clear every thing that is on the frame at the moment
     *
     * @author Rodrigo Guerreiro
     * @param frame the JFrame that the project is based on
     */

    public static void clearFrame(JFrame frame){
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Function that shows the default information on the screen.
     * @param frame the frame to show the info.
     * @param gapi the github api instance.
     * @param tapi the trello api instance.
     * @author Rodrigo Guerreiro
     */
    public static void homeScreen(JFrame frame, GitHubAPI gapi, TrelloAPI tapi){
        GitHubAPI.Date dataInicio = null;
        String readme="";
        try {
            dataInicio = gapi.getStartTime();
            readme = gapi.getFile("master", "/README.md");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dataInicio_toLabel = dataInicio.getDay() + "-" + dataInicio.getMonth() + "-" + dataInicio.getYear();
        //Label com a data de inicio do trabalho
        JLabel labelData = new JLabel("Project's start date: " + dataInicio_toLabel);
        labelData.setBounds(100,50,250,30);
        frame.add(labelData);

        //TODO arranjar forma de meter o nome do projeto
        //Label com o nome do projeto (nome do repo)
        //JLabel labelProjName = new JLabel("Project's name: " + user_git_info[1]);
        //labelProjName.setBounds(400, 50 , 300, 30);
        //frame.add(labelProjName);


        //Print do readme no ecrã
        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(convertMarkdownToHTML(readme));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(100,100,500,600);

        frame.add(edt);
        frame.setVisible(true);
    }

}
