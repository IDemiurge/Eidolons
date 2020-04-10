package main.level_editor.backend.struct.boss;

import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Floor;
import main.data.tree.LayeredData;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;

import java.util.Set;

public class BossDungeon extends LightweightEntity implements LayeredData<LE_Floor> {

    Set<LE_Floor> floors;

    public BossDungeon(ObjType type) {
        super(type);
    }
    /*
    global data?
    links?
    map
    intro data

     */

    public Set<LE_Floor> getFloors() {
        return floors;
    }

    @Override
    public Set<LE_Floor> getChildren() {
        return getFloors();
    }
}
