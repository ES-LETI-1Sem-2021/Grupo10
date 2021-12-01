package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * The basic 'Home Screen' where the user inputs his information.
 * Also has pop-ups!
 *
 * @author Rodrigo Guerreiro.
 */

public class HomeUI implements ActionListener {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 650;
    private final JFrame frame;
    private final ImageIcon iconGit = new ImageIcon("images/github-logo.png");
    private final ImageIcon iconTrello = new ImageIcon("images/trello-logo.jpg");
    private final JLabel labelGitLogo;
    private final JLabel labelGitOwner;
    private final JLabel labelGitRepo;
    private final JLabel labelTrelloLogo;
    private final JLabel labelTrelloKey;
    private final JLabel labelTrelloBoardName;
    private final JTextField gitToken;
    private final JTextField trelloToken;
    private final JTextField gitOwner;
    private final JTextField trelloBoardName;
    private final JTextField gitRepo;
    private final JTextField trelloKey;

    /**
     * Constructor method that initialize the variables in order for the frame to be created.
     *
     * @author Rodrigo Guerreiro
     */


    public HomeUI() {
        this.frame = new JFrame();

        this.labelGitLogo = new JLabel("Please insert your Github token in the box below:");
        this.labelGitOwner = new JLabel("Please insert the repository owner in the box below:");
        this.labelGitRepo = new JLabel("Please the repository name in the box below:");

        this.labelTrelloLogo = new JLabel("Please insert your Trello token in the box below:");
        this.labelTrelloBoardName = new JLabel("Please insert your Trello's board name in the box below:");

        this.labelTrelloKey = new JLabel("Please insert your Trello key in the box below:");

        this.gitToken = new JTextField();
        this.gitOwner = new JTextField();
        this.gitRepo = new JTextField();

        this.trelloToken = new JTextField();
        this.trelloBoardName = new JTextField();
        this.trelloKey = new JTextField();

        var searchButton = new JButton("Search");

        showFrame(frame);
        addImages();
        addInboxes();
        addLabels();

        searchButton.setBounds(SCREEN_WIDTH / 2 - 57, SCREEN_HEIGHT - 100, 150, 50);
        searchButton.addActionListener(this);
        this.frame.add(searchButton);

        this.frame.setVisible(true);
    }

    /**
     * Function that add the labels on the screen to show the user where to put each credential.
     *
     * @author Rodrigo Guerreiro
     */

    private void addLabels() {
        this.labelGitOwner.setBounds(100, 350, 330, 50);
        this.labelGitRepo.setBounds(100, 435, 330, 50);

        this.frame.add(this.labelGitOwner);
        this.frame.add(this.labelGitRepo);

        this.labelTrelloKey.setBounds(SCREEN_WIDTH - 400, 350, 330, 50);
        this.labelTrelloBoardName.setBounds(SCREEN_WIDTH - 400, 435, 370, 50);

        this.frame.add(labelTrelloKey);
        this.frame.add(labelTrelloBoardName);

    }

    /**
     * Displays a pop-up window on the screen.
     *
     * @return opc the value (int) of the option chosen by the user
     * @author Rodrigo Guerreiro
     */
    public static int pop() {
        return JOptionPane.showConfirmDialog(null, "Would you like to load with the saved information's?",
                "Warning?", JOptionPane.YES_NO_OPTION);
    }

    /**
     * Function that add the input boxes to the frame for the user input his credentials.
     *
     * @author Rodrigo Guerreiro
     */
    private void addInboxes() {
        this.gitToken.setBounds(100, 315, 330, 30);
        this.trelloToken.setBounds(SCREEN_WIDTH - 400, 315, 300, 30);

        this.gitOwner.setBounds(100, 400, 330, 30);
        this.trelloKey.setBounds(SCREEN_WIDTH - 400, 400, 300, 30);

        this.gitRepo.setBounds(100, 485, 330, 30);
        this.trelloBoardName.setBounds(SCREEN_WIDTH - 400, 485, 300, 30);


        this.frame.add(trelloKey);
        this.frame.add(trelloBoardName);
        this.frame.add(trelloToken);
        this.frame.add(gitOwner);
        this.frame.add(gitRepo);
        this.frame.add(gitToken);
    }

    /**
     * Displays a frame on the screen.
     *
     * @param frame receives a frame and displays it on the screen
     * @author Rodrigo Guerreiro
     */
    public static void showFrame(JFrame frame) {
        frame.setTitle("DashboardScrum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);
    }

    /**
     * Function that displays on the frame the two images (git and trello).
     *
     * @author Rodrigo Guerreiro
     */

    private void addImages() {
        this.labelGitLogo.setIcon(iconGit);
        this.labelGitLogo.setHorizontalTextPosition(JLabel.CENTER);
        this.labelGitLogo.setVerticalTextPosition(JLabel.BOTTOM);
        this.labelGitLogo.setIconTextGap(10);
        this.labelGitLogo.setBackground(Color.WHITE);
        this.labelGitLogo.setOpaque(false);
        this.labelGitLogo.setBounds(100, 100, 330, 200);

        this.labelTrelloLogo.setIcon(iconTrello);
        this.labelTrelloLogo.setHorizontalTextPosition(JLabel.CENTER);
        this.labelTrelloLogo.setVerticalTextPosition(JLabel.BOTTOM);
        this.labelTrelloLogo.setIconTextGap(-75);
        this.labelTrelloLogo.setBackground(Color.WHITE);
        this.labelTrelloLogo.setOpaque(false);
        this.labelTrelloLogo.setBounds(SCREEN_WIDTH - 400, 100, 330, 200);

        this.frame.add(labelTrelloLogo);
        this.frame.add(labelGitLogo);
    }

    /**
     * {@link Action} performed when the search button is pressed.
     * Creates a pop-up window with a yes/no question and based on the answer
     * saves the data and calls {@link Action#doAction(JFrame, String[], String[], int)}
     * or only calls {@link Action#doAction(JFrame, String[], String[], int)}
     * userGitInfo[gitOwner, gitRepo, gitToken]
     * userTrelloInfo[trelloUser, trelloKey, trelloToken]
     *
     * @author Rodrigo Guerreiro
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        var userGitInfo = new String[]{this.gitOwner.getText(), this.gitRepo.getText(), this.gitToken.getText()};
        var userTrelloInfo = new String[]{this.trelloBoardName.getText(), this.trelloKey.getText(), this.trelloToken.getText()};

        var i = 0;
        i = allFieldsFull(userGitInfo, userTrelloInfo, i);

        var opc = JOptionPane.showConfirmDialog(null, "Would you like to save your data?",
                "Warning?", JOptionPane.YES_NO_OPTION);

        if (opc == JOptionPane.YES_OPTION && i == 0) {
            try {
                Action.saveData(userGitInfo, userTrelloInfo, "data/user_data.txt");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Action.doAction(this.frame, userGitInfo, userTrelloInfo, 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else if (opc == JOptionPane.NO_OPTION && i == 0) {
            try {
                Action.doAction(this.frame, userGitInfo, userTrelloInfo, 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Checks if all fields are full or not.
     *
     * @param userGitInfo    user info
     * @param userTrelloInfo user info
     * @param i              a value
     * @return the value 1 if is empty, 0 otherwise.
     */
    private int allFieldsFull(String[] userGitInfo, String[] userTrelloInfo, int i) {
        for (var s : userGitInfo) {
            if (s.isEmpty()) {
                i = 1;
                break;
            }
        }

        for (var s1 : userTrelloInfo) {
            if (s1.isEmpty()) {
                i = 1;
                break;
            }
        }

        return i;
    }
}
