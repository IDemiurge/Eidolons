package main.game.module.dungeoncrawl.dungeon.minimap;

import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.graphics.GuiManager;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.List;

public class Minimap {
    VISUALS v; // canvas for the map
    boolean editMode;
    boolean debugMode;
    boolean viewMode;
    Dimension size;
    private G_Panel comp;
    private Dungeon dungeon;
    private MiniGrid grid;
    private boolean initialized;
    private MouseListener customMouseListener;
    private MouseWheelListener mouseWheelListener;

    public Minimap(Dungeon dungeon) {
        this(false, dungeon);
    }

    public Minimap(boolean editMode, Dungeon dungeon) {
        this.dungeon = dungeon;
        this.editMode = editMode;
        viewMode = true;
        comp = new G_Panel(v);
    }

    public void init() {
        int borderX = editMode ? 300 : 100;
        int borderY = editMode ? 100 : 50;
        if (!viewMode) {
            size = new Dimension((int) GuiManager.getScreenWidth() - borderX, (int) GuiManager
                    .getScreenHeight()
                    - borderY);
        } else {
            size = new Dimension(GuiManager.getBattleFieldWidth(), GuiManager
                    .getBattleFieldHeight());
            borderX = 0;
            borderY = 0;
        }
        List<MiniObjComp> overlayingToLoad = new LinkedList<>();
        if (grid != null) {
            overlayingToLoad = grid.getOverlayingObjComps();
        }
        grid = new MiniGrid(editMode, this);
        if (MiniGrid.isMouseDragOffsetModeOn()) {
            grid.setCustomMouseListener(customMouseListener);
        } else {
            grid.setCustomMouseListener(customMouseListener);
        }
        grid.setMouseWheelListener(mouseWheelListener);
        grid.setOverlayingObjComps(overlayingToLoad);
        grid.init();
        comp.removeAll();
        comp.add(grid.getComp(), "pos " + borderX / 2 + " " + borderY / 2);
        comp.revalidate();
        comp.setOpaque(false);

        grid.getComp().setOpaque(false);
        initialized = true; // first, the grid...
        // unknown should remain 'black'?
        // many things need to be replicated - selection, grid,

    }

    public void resetGrid() {
        initialized = false;

    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public MiniGrid getGrid() {
        if (grid == null) {
            init();
        }
        return grid;
    }

    public void setGrid(MiniGrid grid) {
        this.grid = grid;
    }

    public G_Panel getComp() {
        if (comp == null) {
            init();
        }
        return comp;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setCustomMouseListener(MouseListener mouseMaster) {
        this.customMouseListener = mouseMaster;

    }

    public MouseWheelListener getMouseWheelListener() {
        return mouseWheelListener;
    }

    public void setMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListener = mouseWheelListener;
    }

    public enum SPECIAL_SYMBOLS {
        ENTRANCE, DOOR, TRAP, KNOWN_ENEMY_CAMP, TREASURE, UNKNOWN_OBJECT,
    }

}
