package main.system.net.chat;

import main.swing.generic.components.G_Panel;
import main.system.net.socket.ServerConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputBox extends G_Panel implements ActionListener {
    JTextField tf;
    private ChatConnection cch;
    private boolean isGameHost;
    private ChatConnector chatConnector;

    public InputBox(ChatConnection cch) {
        super();
        this.cch = cch;
        tf = new JTextField(40);
        // JScrollPane scr = new JScrollPane(tf);
        add(tf, "id TF, pos 0.03al (100%-TF.h)/2, w 97%!");
        tf.addActionListener(this);
    }

    public InputBox(boolean isGameHost, ChatConnector chatConnector) {
        this(null);
        this.isGameHost = isGameHost;
        this.chatConnector = chatConnector;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String text = tf.getText();
        tf.setText("");

        if (isGameHost) {
            try {
                this.chatConnector.getChathandlers().peek()
                        .handleInputHost(text, ServerConnector.getUser());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            cch.send(text);
        }
    }

}
