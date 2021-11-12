package pt.iscte.iul;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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


    /**
     * Constructor method thar initializes all the needed variable
     *
     * @param card The card to be showed
     * @param frame The frame were the card will be shown.
     * @author Rodrigo Guerreiro
     */

    public CardUI(@NotNull TrelloAPI.Card card, JFrame frame) {
    this.card=card;
    this.frame = frame;
    this.dueDate = card.getDueDate();

    Action.clearFrame(frame);

    showCardInfo();


    }

    /**
     * Function that adds everything to the frame.
     * @author Rodrigo Guerreiro
     */

    private void showCardInfo() {
        this.dateLabel.setText("This card was ended at: " + this.dueDate);
        this.dateLabel.setBounds(75,10,350,10);
        this.dateLabel.setVisible(true);
        this.frame.add(dateLabel);

        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(Action.convertMarkdownToHTML(card.getDesc()));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(75,100,500,600);

        this.frame.add(edt);
    }


}
