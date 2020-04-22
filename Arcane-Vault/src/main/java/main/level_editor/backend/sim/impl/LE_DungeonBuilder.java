package main.level_editor.backend.sim.impl;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.netherflame.dungeons.model.assembly.ModuleGridMapper;
import main.level_editor.backend.sim.LE_GameSim;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public void initWidthAndHeight(Location dungeonWrapper) {
        int w = ModuleGridMapper.maxWidth;
        int h = ModuleGridMapper.maxHeight;
        if (dungeonWrapper.getWidth() == 0) {
            dungeonWrapper.setWidth(w);
        }
        if (dungeonWrapper.getHeight() == 0) {
            dungeonWrapper.setHeight(h);
        }
        super.initWidthAndHeight(dungeonWrapper);
    }

    @Override
    public Location buildDungeon(String path, String data, List<Node> nodeList) {
        Location location = super.buildDungeon(path, data, nodeList);
        if (getGame() instanceof LE_GameSim) {
            Map<Integer, BattleFieldObject> map = new LinkedHashMap<>();
            for (Module module : getModules()) {

                if (isIdShiftRequired()){
//                 TODO    objMaps.add(module.getObjIdMap());
//                    typeMaps.add(module.getIdTypeMap());
                } else {
                    map.putAll(module.getObjIdMap());
                }
            }
            ((LE_GameSim) getGame()).setObjIdMap(
                   map);
        }
        return location;
    }

    private boolean isIdShiftRequired() {
        return false;
    }


}
