package pt.iscte.iul;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.BorderFactory.createEmptyBorder;

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
     * Constructor method that initializes all the needed variables.
     *
     * @param card  The card to be shown.
     * @param frame The frame where the card will be shown.
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
     * Method that adds everything to the frame.
     *
     * @author Rodrigo Guerreiro
     */
    private void showCardInfo() {
        JEditorPane edt = new JEditorPane();

        this.dateLabel.setText("This card was ended at: " + this.dueDate);
        this.dateLabel.setBounds(130, 5, 350, 30);
        this.dateLabel.setVisible(true);
        this.dateLabel.setForeground(new Color(68, 114, 196));
        edt.add(dateLabel);
        //this.frame.add(dateLabel);

        JScrollPane scrollerLeft = new JScrollPane();

        edt.setContentType("text/html");
        edt.setText("<br></br><br></br>" + Action.convertMarkdownToHTML(card.getDescription()) + "<br></br><br></br>");
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(75, 100, 500, 600);

        scrollerLeft.setViewportView(edt);

        scrollerLeft.setBounds(15, 0, ((frame.getWidth() - 100) / 2), frame.getHeight());
        scrollerLeft.setBorder(createEmptyBorder());

        frame.add(scrollerLeft);
        frame.setVisible(true);

        //this.frame.add(edt);
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
