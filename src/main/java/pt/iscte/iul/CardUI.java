package pt.iscte.iul;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

/**
 * Class to show the card's information on the UI.
 *
 * @author Rodrigo Guerreiro
 */
public class CardUI extends Thread {
    private final TrelloAPI.Card card;
    private final JFrame frame;
    private final String dueDate;
    private final JLabel dateLabel = new JLabel();
    private final TrelloAPI trelloAPI;

    /**
     * Constructor method thar initializes all the needed variable
     *
     * @param card  The card to be showed
     * @param frame The frame were the card will be shown.
     * @author Rodrigo Guerreiro
     */
    public CardUI(@NotNull TrelloAPI.Card card, JFrame frame, TrelloAPI trelloAPI) throws IOException {
        this.card = card;
        this.frame = frame;
        this.dueDate = card.getDueDate();
        this.trelloAPI = trelloAPI;
        Action.clearFrame(frame);

        showCardInfo();
    }

    /**
     * Function that adds everything to the frame.
     *
     * @author Rodrigo Guerreiro
     */
    private void showCardInfo() {
        JEditorPane edt = new JEditorPane();

        this.dateLabel.setText("This card was ended at: " + this.dueDate);
        this.dateLabel.setBounds(75, 10, 350, 10);
        this.dateLabel.setVisible(true);
        this.frame.add(dateLabel);

        JScrollPane scrollerLeft = new JScrollPane();

        edt.setContentType("text/html");
        edt.setText(Action.convertMarkdownToHTML(card.getDescription()));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(75, 100, 500, 600);

        this.frame.add(edt);
    }

    public void run() {
        if (this.card.getName().contains("Sprint Retrospective")) {
            String splitString = this.card.getName().split(" - ")[1];
            try {
                JElements.addHoursInfo(frame, splitString, trelloAPI);
                new JElements(frame, trelloAPI.getTotalHoursByUser(splitString, ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JElements.addHoursInfo(frame, card.getName(), trelloAPI);
                new JElements(frame, trelloAPI.getTotalHoursByUser(card.getName(), ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.frame.repaint();
    }

}
