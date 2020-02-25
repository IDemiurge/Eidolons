package eidolons.test.debug;

import main.swing.generic.components.G_Panel;
import main.swing.generic.windows.G_Frame;
import main.swing.panels.misc.G_InputPanel;
import main.swing.panels.misc.G_LogPanel;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
@Deprecated
public class DebugPanel {

    G_LogPanel logPanel;
    Component buttonPanel;
    G_InputPanel inputPanel;
    private G_Frame frame;
    private DebugMaster master;


    public DebugPanel(DebugMaster master) {
        frame = new G_Frame("Debug Panel");
        this.master = master;

        initGUI();
    }

    public static int getPanelWidth() {
        return GuiManager.getSquareCellSize() * 4;
    }

    public static int getPanelHeight() {
        return (int) GuiManager.getScreenHeight() - GuiManager.getSquareCellSize()
         * 2;
    }

    private void initGUI() {
        // KEY LISTENER!

        logPanel = new G_LogPanel();
        buttonPanel = new FunctionPanel(master);
        inputPanel = new G_InputPanel();

//        ip = new DC_InfoPanel(null, null, DC_Game.game.getState());

        G_Panel panel = new G_Panel();
        panel.setBackground(ColorManager.getTranslucent());
        panel.setOpaque(false);
//        panel.add(ip, "id ip, pos 0 0");

        panel.add(buttonPanel, "id btns, pos ip.x2 0");

        frame.add(panel);
        // setUndecorated(true);
        // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(new Dimension(getPanelWidth(), getPanelHeight()));
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                frame.setVisible(false);

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }
        });
    }

    public synchronized G_Frame getFrame() {
        return frame;
    }

    public synchronized void setFrame(G_Frame frame) {
        this.frame = frame;
    }

    public void refresh() {
        if (!frame.isVisible()) {
        }
//        if (master.getGame().isSimulation()) {
//            ip.setInfoObj(CharacterCreator.getHero());
//        } else {
//            ip.setInfoObj(master.getGame().getManager().getInfoObj());
//        }
//        ip.refresh();

    }

}
