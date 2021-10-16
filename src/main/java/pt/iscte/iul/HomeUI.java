package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class HomeUI implements ActionListener {

    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 650;
    private final JFrame frame;
    private final ImageIcon icon_git = new ImageIcon("images/github-logo.png");
    private final ImageIcon icon_trello = new ImageIcon("images/trello-logo.jpg");
    private final JLabel label_git_logo;
    private final JLabel label_git_owner;
    private final JLabel label_git_repo;
    private final JLabel label_trello_logo;
    private final JLabel label_trello_key;
    private final JLabel label_trello_user;
    private final JTextField git_token;
    private final JTextField trello_token;
    private final JTextField git_owner;
    private final JTextField trello_user;
    private final JTextField git_repo;
    private final JTextField trello_key;
    private final JButton search_button;


    public HomeUI() {
       this.frame = new JFrame();

       this.label_git_logo = new JLabel("Please insert your Github token in the box below:");
       this.label_git_owner = new JLabel("Please insert the repository owner in the box below:");
       this.label_git_repo = new JLabel("Please the repository name in the box below:");

       this.label_trello_logo = new JLabel("Please insert your Trello token in the box below:");
       this.label_trello_user = new JLabel("Please insert your Trello's board name  in the box below:");
       this.label_trello_key = new JLabel("Please insert your Trello key in the box below:");

       this.git_token = new JTextField();
       this.git_owner = new JTextField();
       this.git_repo = new JTextField();

       this.trello_token = new JTextField();
       this.trello_user = new JTextField();
       this.trello_key = new JTextField();

       this.search_button = new JButton("Search");

       show_frame(frame);
       add_images();
       add_inboxes();
       add_labels();

       this.search_button.setBounds(SCREEN_WIDTH/2-57, SCREEN_HEIGHT - 100, 150,50);
       this.search_button.addActionListener(this);
       this.frame.add(this.search_button);

       this.frame.setVisible(true);
    }

    private void add_labels() {
        this.label_git_owner.setBounds(100, 350, 330, 50);
        this.label_git_repo.setBounds(100, 435, 330, 50);

        this.frame.add(this.label_git_owner);
        this.frame.add(this.label_git_repo);

        this.label_trello_key.setBounds(SCREEN_WIDTH-400, 350, 330, 50);
        this.label_trello_user.setBounds(SCREEN_WIDTH-400, 435, 330, 50);

        this.frame.add(label_trello_key);
        this.frame.add(label_trello_user);

    }

    /**
     * Displays a pop-up window on the screen.
     *
     * @return opc the value (int) of the option chosen by the user
     * @author Rodrigo Guerreiro
     */

    public static int pop(){
        return JOptionPane.showConfirmDialog(null, "Would you like to load with the saved information's?",
                "Warning?", JOptionPane.YES_NO_OPTION);
    }

    private void add_inboxes() {

        this.git_token.setBounds(100,315, 330,30);
        this.trello_token.setBounds(SCREEN_WIDTH-400,315, 300,30);

        this.git_owner.setBounds(100,400, 330,30);
        this.trello_key.setBounds(SCREEN_WIDTH-400,400, 300,30);

        this.git_repo.setBounds(100,485, 330,30);
        this.trello_user.setBounds(SCREEN_WIDTH-400,485, 300,30);


        this.frame.add(trello_key);
        this.frame.add(trello_user);
        this.frame.add(trello_token);
        this.frame.add(git_owner);
        this.frame.add(git_repo);
        this.frame.add(git_token);


    }
    /***
    *Displays a frame on the screen.
    *
    * @param frame receives a frame and displays it on the screen
    * @author Rodrigo Guerreiro
     */
    public static void show_frame(JFrame frame){

        frame.setTitle("DashboardScrum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

    }

    private void add_images(){

        this.label_git_logo.setIcon(icon_git);
        this.label_git_logo.setHorizontalTextPosition(JLabel.CENTER);
        this.label_git_logo.setVerticalTextPosition(JLabel.BOTTOM);
        this.label_git_logo.setIconTextGap(10);
        this.label_git_logo.setBackground(Color.WHITE);
        this.label_git_logo.setOpaque(false);
        this.label_git_logo.setBounds(100,100,330, 200);

        this.label_trello_logo.setIcon(icon_trello);
        this.label_trello_logo.setHorizontalTextPosition(JLabel.CENTER);
        this.label_trello_logo.setVerticalTextPosition(JLabel.BOTTOM);
        this.label_trello_logo.setIconTextGap(-75);
        this.label_trello_logo.setBackground(Color.WHITE);
        this.label_trello_logo.setOpaque(false);
        this.label_trello_logo.setBounds(SCREEN_WIDTH-400,100,330, 200);

        this.frame.add(label_trello_logo);
        this.frame.add(label_git_logo);
    }

    /***
     *
     * Action preformed when the search button is pressed.
     * Creates a pop-up window with a yes/no question and based on the answer
     * saves the data and calls do_action or only calls do_action
     *
     *
     * user_git_info[git_owner, git_repo, git_token,]
     * user_trello_info[trello_user, trello_key, trello_token]
     *
     * @author Rodrigo Guerreiro
     *
     ***/

    @Override
    public void actionPerformed(ActionEvent e) {

        String[] user_git_info = {this.git_owner.getText(), this.git_repo.getText(), this.git_token.getText()};
        String[] user_trello_info = {this.trello_user.getText(), this.trello_key.getText(), this.trello_token.getText()};

        int i = 0;

        for(String s: user_git_info){
            if(s.isEmpty()){
                i = 1;
                break;
            }
        }

        for(String s1: user_trello_info){
            if(s1.isEmpty()){
                i = 1;
                break;
            }
        }

        int opc = JOptionPane.showConfirmDialog(null, "Would you like to save your data?",
                "Warning?", JOptionPane.YES_NO_OPTION);

        if(opc == JOptionPane.YES_OPTION && i == 0){
            System.out.println("YESSS");
            Action.save_data(user_git_info, user_trello_info);
            Action.do_action(this.frame, user_git_info, user_trello_info);

        }else if (opc == JOptionPane.NO_OPTION && i == 0 ){
            System.out.println("NOOO");
            Action.do_action(this.frame, user_git_info, user_trello_info);
        }
    }
}
