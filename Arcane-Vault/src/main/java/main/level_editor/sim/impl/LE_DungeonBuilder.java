package main.level_editor.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.level_editor.sim.LE_GameSim;
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
    @Override
    public Location buildDungeon(String path, String data, List<Node> nodeList) {
        Location location=super.buildDungeon(path, data, nodeList);
        if (getGame() instanceof LE_GameSim) {
            ((LE_GameSim) getGame()).setObjIdMap(
                    getObjIdMap());
        }
        return location;
    }

    @Override
    protected void processNode(Node n, Location dungeon, DungeonPlan plan) {
        super.processNode(n, dungeon, plan);
    }

    @Override
    public void initModuleZoneLazily(Module module) {
        super.initModuleZoneLazily(module);
    }

    @Override
    public DungeonPlan loadDungeonMap(String data) {
        return super.loadDungeonMap(data);
    }

    @Override
    protected boolean isZoneModulesLazy() {
        return super.isZoneModulesLazy();
    }

}
