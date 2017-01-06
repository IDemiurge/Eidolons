package main.system.net.chat;

import main.swing.generic.components.G_Panel;

import javax.swing.*;

public class ChatBox extends G_Panel {
    JTextPane CHAT;

    public ChatBox() {
        super();
        CHAT = new JTextPane();
        JScrollPane scr = new JScrollPane(CHAT);

        add(scr, "pos 0 0 100% 100%");
        // scr.setViewport(null )
        CHAT.setEditable(false);
        // scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        // scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private static String formatInput(String input) {
        // String s = "<html><i>" + input + "</i></html>";
        return input;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    public synchronized void appendToChat(String input) {
        input = formatInput(input);
        // //getOrCreate html doc, ensure ENDL
        // CHAT.setDocument(null)

        CHAT.setText(CHAT.getText() + "\n" + input);
    }

}
