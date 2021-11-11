package pt.iscte.iul;

import javax.swing.*;

public class CardUI {
    private final TrelloAPI.Card card;
    private final JFrame frame;

    public CardUI(TrelloAPI.Card card, JFrame frame) {
    this.card=card;
    this.frame = frame;
    Action.clearFrame(frame);

    showCardInfo();

    }

    private void showCardInfo() {
        JEditorPane edt = new JEditorPane();
        edt.setContentType("text/html");
        edt.setText(Action.convertMarkdownToHTML(card.getDesc()));
        edt.setEditable(false);
        edt.setVisible(true);
        edt.setBounds(100,100,500,600);

        frame.add(edt);
    }


}
