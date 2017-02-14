package main.swing.listeners;

import main.entity.obj.Obj;
import main.game.DC_Game;
import main.swing.builders.DC_Builder;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.system.graphics.GuiManager;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class BF_MouseWheelListener implements MouseWheelListener {
    private DC_Builder builder;

    public BF_MouseWheelListener(DC_Builder builder) {
        this.builder = builder;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        DC_BattleFieldGrid grid = builder.getGrid();
        boolean x = e.isAltDown();
        Obj hoverObj = DC_Game.game.getManager().getHoverObj();
        if (hoverObj != null) {
            if (grid.isOnEdgeX(hoverObj.getCoordinates()) != null) {
                if (grid.isOnEdgeY(hoverObj.getCoordinates()) == null) {
                    x = true;
                }
            } else if (grid.isOnEdgeY(hoverObj.getCoordinates()) != null) {
                x = false;
            }
        } else {
            Point p = e.getPoint();
            int y_diff = Math.max(p.y, GuiManager.getBattleFieldHeight() - p.y);
            int x_diff = Math.max(p.x, GuiManager.getBattleFieldWidth() - p.x);
            x = x_diff > y_diff;
        }

        int n = (int) Math.round(e.getPreciseWheelRotation());
        if (n == 0) {
            return;
        }
        grid.wheelRotates(n, x);
    }
}
// int xDiff = LevelEditor.getCurrentLevel().getDungeon().getCellsX()
// * 100
// / Math.max(
// LevelEditor.getCurrentLevel().getDungeon().getCellsX() - hoverObj.getX(),
// hoverObj.getX());
// int yDiff = LevelEditor.getCurrentLevel().getDungeon().getCellsY()
// * 100
// / Math.max(
// LevelEditor.getCurrentLevel().getDungeon().getCellsY() - hoverObj.getY(),
// hoverObj.getY());
//
// boolean x = xDiff < yDiff;