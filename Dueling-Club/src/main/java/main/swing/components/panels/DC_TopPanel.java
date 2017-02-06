package main.swing.components.panels;

import main.client.cc.gui.misc.HC_TopComp;
import main.game.DC_Game;
import main.swing.components.buttons.MenuButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.VisualComponent;
import main.test.debug.DebugGui;

import java.awt.*;

/**
 * @author Regulus
 */
public class DC_TopPanel extends G_Panel {

    // Dungeon name and turn number as 2 custom painted strings

    private static final int xOffset = 230;
    private static final String POS = "pos " + xOffset + " 0";
    private static final int BUTTON_X = 105;
    private static final int BUTTON_Y = 0;
    TopComp topComp;
    MenuButton button;
    VisualComponent dungeonSymbol;
    private DebugGui debugGui;
    private boolean debugOn;
    private DC_Game game;

    public DC_TopPanel(DC_Game game) {
        button = new MenuButton(game);
        topComp = (game.isSimulation()) ? new HC_TopComp(game) : new TopComp(game);
        this.game = game;
        addComps();
    }

    private void addComps() {
        if (game.isSimulation()) {
            add(topComp);
        } else {
            add(topComp, POS + ", id top");
        }
        add(button, "pos " + (game.isSimulation() ? BUTTON_X : xOffset + BUTTON_X) + " " + BUTTON_Y);

        setComponentZOrder(button, 0);
        setComponentZOrder(topComp, 1);

    }

    @Override
    public void refresh() {
        topComp.refresh();
    }

    public void toggleDebugGui() {
        refresh();
        if (debugGui == null) {
            debugGui = new DebugGui();
        }
        debugOn = !debugOn;
        G_Panel removed = !debugOn ? debugGui : topComp;
        G_Panel added = debugOn ? debugGui : topComp;

        remove(removed);
        add(added);
        add(added, !debugOn ? "pos " + xOffset + " 0" + ", id top" : "pos 0 0");
        if (debugOn) {
            setPanelSize(new Dimension(debugGui.getPanelSize()));
        }
        // else
        // setPanelSize(topComp.getVisuals().getSize());
        setComponentZOrder(button, 0);
        setComponentZOrder(added, 1);
        revalidate();
        repaint();
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

}
