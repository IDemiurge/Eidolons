package main.swing.generic.components.misc;

import main.swing.generic.components.G_Component;

import javax.swing.*;

public class G_ToolBar extends G_Component {

    private final JToolBar component;
    String[] commands = new String[]{"add", "remove"};


    public G_ToolBar(int orientation, G_Component[] items) {
        component = new JToolBar("my toolbar");


        //component.setLayout(new FlowLayout());
        for (String command : commands) {

            JButton button = new JButton(command);
            button.setActionCommand(command);
            //button.addActionListener(listener);
            //sync with Menu???
            component.add(button);
        }
        component.setOrientation(JToolBar.VERTICAL);

    }
}
