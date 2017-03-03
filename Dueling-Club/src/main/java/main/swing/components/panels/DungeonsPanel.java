package main.swing.components.panels;

import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.DungeonMaster;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.graphics.GuiManager;

import java.util.HashMap;
import java.util.Map;

public class DungeonsPanel {

    private G_Panel panel;
    private DC_Game game;

    public DungeonsPanel(DC_Game game) {
        panel = new G_Panel(getDefaultVisuals());
        // extent tree view?

		/*
         * fast-travel
		 * info 
		 * for large locations/settlements 
		 * 
		 * map form 
		 * 
		 * now just to navigate between dungeons 
		 * debug controls  
		 */
        this.setGame(game);

        refresh();
    }

    private VISUALS getDefaultVisuals() {
        return VISUALS.INFO_PANEL;
        // return VISUALS.TREE_VIEW;
    }

    public void refresh() {
        getPanel().removeAll();
        Dungeon root = getGame().getDungeonMaster().getRootDungeon();
        Map<Dungeon, Coordinates> map = new HashMap<>();
        int h = GuiManager.getCellWidth();
        int w = GuiManager.getFullObjSize();
        int x = 0;
        int y = 0;
        int xOffset = 15;
        int yOffset = 24;
        for (Dungeon dungeon : DungeonMaster.getDungeons()) {
            DungeonComponent dungeonComponent = new DungeonComponent(dungeon);
            y++;
            // dungeon.getParent();
            Object pos = "pos " + (x * w + xOffset) + " " + (y * h + yOffset);
            getPanel().add(dungeonComponent, pos);
            map.put(dungeon, new Coordinates(x, y));

        }
        getPanel().revalidate();
    }

    public void init() {
        // kind of a tree?
        // bottom-right overlapping gcp with 'close'

    }

    public G_Panel getPanel() {
        return panel;
    }

    public void setPanel(G_Panel panel) {
        this.panel = panel;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

}
