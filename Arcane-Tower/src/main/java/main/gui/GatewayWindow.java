package main.gui;

import main.gui.gateway.GatewayView;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class GatewayWindow {
    // upper jmenu?

    static JFrame window;
    GatewayButtonHandler buttonHandler;
    G_Panel panel;
    private GatewayView view;

    public GatewayWindow() {
        buttonHandler = new GatewayButtonHandler(this);
        init();
        window = GuiManager.inNewWindow(false, panel, "Gateway", getGatewaySize());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static Dimension getGatewaySize() {
        return new Dimension(GuiManager.getScreenWidthInt() - 300,
                GuiManager.getScreenHeightInt() - 100);
    }

    public static boolean isInitiallyExpanded() {
        return true;
    }

    public static JFrame getWindow() {
        return window;
    }

    public void init() {
        panel = new G_Panel();
        G_Panel backgroundPanel = initBackgroundPanel();
        String pos = "pos 0 0, id backgroundPanel";
        // panel.add(backgroundPanel, pos);

        G_Panel buttonPanel = initButtonPanel();
        pos = "pos 0 0, id buttonPanel";
        panel.add(buttonPanel, pos);
        view = new GatewayView();
        pos = "pos 0 buttonPanel.y2, id gatewayView";
        panel.add(view, pos);

        // G_Panel statsPanel; //
        // G_Panel sessionInfoPanel;
        // G_Panel objEditPanel; // task, goal, ...
        // G_Panel boxPanel; // switch style,

    }

    private G_Panel initBackgroundPanel() {
        Image img = getBackgroundPicture();
        G_Panel panel = new G_Panel(new CompVisuals(img));
//        img = DC_GameGUI.createBackgroundOverlay();
        panel.add(new JLabel(new ImageIcon(img)));
        return panel;
    }

    private Image getBackgroundPicture() {
        return ImageManager.getImage("UI\\Arcane Tower\\background.jpg"
                // backgroundImgPath
        );
    }

    private G_Panel initButtonPanel() {
        int i = 0;
        G_Panel buttonPanel = new G_Panel("  ");
        buttonPanel.setPanelSize(new Dimension(GuiManager.getScreenWidthInt(), 64));
        for (String command : GatewayButtonHandler.BUTTONS) {
            command = StringMaster.getWellFormattedString(command);
            JButton btn = new JButton(command);
            String pos = "";
            i++;
            // if (i >= wrapButtons) {
            // i = 0;
            // pos = "wrap";
            // }
            buttonPanel.add(btn, pos);
            btn.setActionCommand(command);
            btn.addActionListener(buttonHandler);
        }
        return buttonPanel;
    }

    public GatewayButtonHandler getButtonHandler() {
        return buttonHandler;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public GatewayView getView() {
        return view;
    }

}
