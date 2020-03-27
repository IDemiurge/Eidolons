package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.game.bf.directions.DirectionMaster;
import main.level_editor.backend.sim.LE_GameSim;
import main.system.graphics.GuiManager;
import main.system.math.PositionMaster;
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

    protected void initWidthAndHeight(Location dungeonWrapper) {
        GuiManager.setBattleFieldCellsX(dungeonWrapper.getDungeon().getCellsX());
        GuiManager.setBattleFieldCellsY(dungeonWrapper.getDungeon().getCellsY());
        GuiManager.setCurrentLevelCellsX(dungeonWrapper.getWidth());
        GuiManager.setCurrentLevelCellsY(dungeonWrapper.getHeight());
        //TODO clean up this shit!

        PositionMaster.initDistancesCache();
        DirectionMaster.initCache(dungeonWrapper.getDungeon().getCellsX(),
                dungeonWrapper.getDungeon().getCellsY());
        Coordinates.resetCaches();
        GammaMaster.resetCaches();
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

    @Override
    public void initModuleZoneLazily(Module module) {
        super.initModuleZoneLazily(module);
    }

    @Override
    public Location loadDungeonMap(String data) {
        return super.loadDungeonMap(data);
    }

    @Override
    protected boolean isZoneModulesLazy() {
        return super.isZoneModulesLazy();
    }

}
