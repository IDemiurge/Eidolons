package main.swing.generic.components.misc;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class G_Button extends JButton implements ActionListener {
    Runnable handler;

    public G_Button(String text, Runnable handler) {
        super(text);
        this.handler = handler;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (handler != null) {
            handler.run();
        }
    }
}
