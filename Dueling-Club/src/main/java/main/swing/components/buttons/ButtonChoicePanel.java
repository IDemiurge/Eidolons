package main.swing.components.buttons;

import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.graphics.GuiManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

public class ButtonChoicePanel {

    private List<String> listData;
    private G_Panel panel;
    private WAIT_OPERATIONS operation = WAIT_OPERATIONS.OPTION_DIALOG;
    private Dimension SIZE;
    private int wrap = 14;
    private int columnWidth;
    private int columns;
    private JFrame window;

    public ButtonChoicePanel(SELECTION_MODE single, List<String> listData) {
        this.listData = listData;
    }

    private void init() {
        panel = new G_Panel(getPanelVisuals());
        panel.setMigLayout(getArgs());
        int i = 0;
        // G_Panel buttonPanel = new G_Panel("flowy");
        for (String l : listData) {
            String pos = "";
            i++;
            if (i >= wrap) {
                pos = "wrap";
                columns++;
                i = 0;
            }
            panel.add(createButton(l), pos);
            // buttonPanel.add(createButton(l), pos);
        }

        // panel.setVisuals(getPanelVisuals());
        // panel.add(buttonPanel, "pos 40 40");
        SIZE = getPanelVisuals().getSize();

        // new Dimension(columnWidth *columns,Math.max(columns*wrap, i)*
        // getVisuals().getHeight());
    }

    private String getArgs() {
        return "flowy, insets 40 40 40 40";
    }

    private ComponentVisuals getPanelVisuals() {
        // if (columns==0)
        return VISUALS.FRAME;
    }

    private CustomButton createButton(String text) {
        CustomButton b = new CustomButton(getVisuals(), text) {
            @Override
            public void handleClick() {
                WaitMaster.receiveInput(operation, text);
            }
        };
        b.setPanelSize(getVisuals().getSize());
        return b;
    }

    private ComponentVisuals getVisuals() {
        return VISUALS.VALUE_BOX_TINY;
    }

    public String choose() {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Cannot run this on EDT!");
        }
        // preCheck EDT!!!
        show();
        String result = (String) WaitMaster.waitForInput(operation);
        return result;
    }

    private void show() {
        init();
        // getOrCreate prefSize?
        window = GuiManager.inNewWindow(panel, SIZE);
        // window.setUndecorated(true);
        window.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                WaitMaster.interrupt(operation);

            }

            @Override
            public void windowClosed(WindowEvent e) {
                WaitMaster.interrupt(operation);

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }
}
