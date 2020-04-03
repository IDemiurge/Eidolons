package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.level_editor.LevelEditor;

public class LE_FloorLoader extends FloorLoader {
    public LE_FloorLoader(DungeonMaster<Location> master) {
        super(master);
    }

    @Override
    protected void processTransitPair(Integer id, Integer id2, Location location) {
        LevelEditor.getManager().getMapHandler().addTransit(id, id2);
    }
}
