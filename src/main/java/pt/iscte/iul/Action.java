package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Action {

    /**
     *
     * Function that does everything.
     * Connects the UI's to the api's.
     *
     * @param  user_git_info String array with[git_owner, git_repo, git_token,]
     * @param user_trello_info String array with [trello_user, trello_key, trello_token]
     * @author Rodrigo Guerreiro
     *
     */

    public static void do_action(JFrame frame, String[] user_git_info, String[] user_trello_info){

        var gitApi = new GitHubAPI(user_git_info[0],user_git_info[1],user_git_info[2]);
        String readme= null;
        String dataInicio_toLabel = "";
        try {
            readme = gitApi.getFile("master","/README.md");

            GitHubAPI.Collaborators[] cols = gitApi.getCollaborators();

            GitHubAPI.Date dataInicio = gitApi.getStartTime();
            dataInicio_toLabel = dataInicio.getDay() + "-" + dataInicio.getMonth() + "-" + dataInicio.getYear();


            //username dos colaboradores
            for (GitHubAPI.Collaborators col : cols) System.out.println(col.getName());


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Limpa o ecra
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        // aumenta o tamanho do ecra
        frame.setLocation(0,0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height-100);


        //Label com a data de inicio do trabalho
        JLabel labelData = new JLabel("Project's start date: "+dataInicio_toLabel);
        labelData.setBounds(100,50,250,30);
        frame.add(labelData);


        //Print doo readme no ecrã
        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(convertMarkdownToHTML(readme));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(100,100,500,300);

        frame.add(edt);
        frame.setVisible(true);

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

    //função da net que transforma md para html
    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

}
