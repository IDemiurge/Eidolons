package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.level_editor.backend.functions.mapping.ModuleGridMapper;
import main.level_editor.backend.sim.LE_GameSim;
import org.w3c.dom.Node;

import java.util.List;

public class LE_DungeonBuilder extends LocationBuilder {
    /*
    what do we NOT do here?
    it seems we're planning for A LOT of additional data
    layers
    WOW - whole new format with IDs!
    editor data?
     */

    public LE_DungeonBuilder(DungeonMaster master) {
        super(master);
    }

    protected void initWidthAndHeight(Location dungeonWrapper) {
        int w = ModuleGridMapper.maxWidth;
        int h = ModuleGridMapper.maxHeight;
        dungeonWrapper.setWidth(w);
        dungeonWrapper.setHeight(h);
        super.initWidthAndHeight(dungeonWrapper);
    }

    @Override
    public Location buildDungeon(String path, String data, List<Node> nodeList) {
        Location location=super.buildDungeon(path, data, nodeList);
        if (getGame() instanceof LE_GameSim) {
            ((LE_GameSim) getGame()).setObjIdMap(
                   master.getObjIdMap());
        }
        return location;
    }




}
