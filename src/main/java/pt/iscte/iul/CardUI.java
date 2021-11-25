package pt.iscte.iul;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

/**
 * Class to show the card's information on the UI.
 *
 * @author Rodrigo Guerreiro
 */
public class CardUI {
    private final TrelloAPI.Card card;
    private final JFrame frame;
    private final String dueDate;
    private final JLabel dateLabel = new JLabel();
    private final TrelloAPI trelloAPI;
    private final String boardID;


    /**
     * Constructor method thar initializes all the needed variable
     *
     * @param card  The card to be showed
     * @param frame The frame were the card will be shown.
     * @author Rodrigo Guerreiro
     */
    public CardUI(@NotNull TrelloAPI.Card card, JFrame frame, TrelloAPI trelloAPI, String boardID) throws  IOException{
        this.card = card;
        this.frame = frame;
        this.dueDate = card.getDueDate();
        this.trelloAPI = trelloAPI;
        this.boardID = boardID;
        Action.clearFrame(frame);

        showCardInfo();
    }

    /**
     * Function that adds everything to the frame.
     *
     * @author Rodrigo Guerreiro
     */
    private void showCardInfo() throws IOException {
        this.dateLabel.setText("This card was ended at: " + this.dueDate);
        this.dateLabel.setBounds(75, 10, 350, 10);
        this.dateLabel.setVisible(true);
        this.frame.add(dateLabel);

        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(Action.convertMarkdownToHTML(card.getDescription()));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(75, 100, 500, 600);


        if (this.card.getName().contains("Sprint Retrospective")) {
            String splitedString = this.card.getName().split(" - ")[1];

             Action.addHoursInfo(frame, splitedString, this.boardID , trelloAPI);
        }

        this.frame.add(edt);
    }
}
