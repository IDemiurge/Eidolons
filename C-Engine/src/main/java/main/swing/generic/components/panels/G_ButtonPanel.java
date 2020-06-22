package main.swing.generic.components.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class G_ButtonPanel extends JPanel implements ActionListener {
    public G_ButtonPanel(String[] commands) {
        this(new ArrayList<>(Arrays.asList(commands)));
    }

    public G_ButtonPanel(List<String> commands) {
        int rows;
        int cols;
        if (isHorizontal()) {
            cols = getWrap();
            rows = commands.size() / cols;
        } else {
            rows = getWrap();
            cols = commands.size() / rows;
        }
        int spaceH=0;
        int spaceV=0;
        setLayout(new GridLayout(rows, cols, spaceH, spaceV));
        for (String command : commands) {
            JButton button;
            add (button = new JButton(command));
            button.setActionCommand(command);
            button.addActionListener(this);
        }

    }

    public boolean isHorizontal() {
        return true;
    }

    public int getWrap() {
        return Integer.MAX_VALUE;
    }
}
