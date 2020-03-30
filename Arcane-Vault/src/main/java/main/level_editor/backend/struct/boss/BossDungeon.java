package main.level_editor.backend.struct.boss;

import main.data.tree.LayeredData;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.level_editor.backend.struct.level.Floor;

import java.util.Set;

public class BossDungeon extends LightweightEntity implements LayeredData<Floor> {

    Set<Floor> floors;

    public BossDungeon(ObjType type) {
        super(type);
    }
    /*
    global data?
    links?
    map
    intro data

     */

    public Set<Floor> getFloors() {
        return floors;
    }

    @Override
    public Set<Floor> getChildren() {
        return getFloors();
    }
}
