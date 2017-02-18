package main.swing.components.panels;

import main.game.core.game.DC_Game;
import main.swing.generic.components.panels.G_ButtonPanel;

import java.awt.event.ActionEvent;

public class DC_MenuPanel extends G_ButtonPanel {

    private static final String OPTIONS = "Options";
    private static final String DEBUG = "Debug";
    static String[] menuItems = {OPTIONS, DEBUG,};
    private DC_Game game;

    public DC_MenuPanel(DC_Game game) {
        super(getMenuItems());
        this.game = game;
    }

    private static String[] getMenuItems() {
        return menuItems;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case OPTIONS: {
                break;
            }
            case DEBUG: {
                runDebug();
                break;
            }
        }

    }

    private void runDebug() {

        if (game.isDebugMode()) {
            game.getDebugMaster().showDebugWindow();

        }
    }

}
