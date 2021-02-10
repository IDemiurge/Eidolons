package main.level_editor.backend.sim.impl;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.level_editor.LevelEditor;
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

    public void initLocation (Location location) {
        if (location.isInitialEdit()) {
            location.setInitialEdit(true);
            String savePath = LevelEditor.getManager().getDataHandler().getDefaultSavePath(location);
            location.getData().setValue(LevelStructure.FLOOR_VALUES.filepath,
                    savePath);
            main.system.auxiliary.log.LogMaster.log(1,location+ " loads for first time, save path " + savePath );
        }
    }

    @Override
    public void initLevel(List<Node> nodeList) {
        super.initLevel(nodeList);
        if (getGame() instanceof LE_GameSim) {
            Map<Integer, BattleFieldObject> map = new LinkedHashMap<>();
            for (Module module : getModules()) {

                if (isIdShiftRequired()) {
//                 TODO    objMaps.add(module.getObjIdMap());
//                    typeMaps.add(module.getIdTypeMap());
                } else {
                    map.putAll(module.getObjIdMap());
                }
            }
            getGame().setStarted(true);
            ((LE_GameSim) getGame()).setObjIdMap(
                    map);
        }
    }

    private boolean isIdShiftRequired() {
        return false;
    }


}
