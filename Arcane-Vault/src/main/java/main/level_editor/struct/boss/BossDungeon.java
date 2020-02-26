package main.level_editor.struct.boss;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.level_editor.struct.level.Floor;

import java.util.Set;

public class BossDungeon extends LightweightEntity {

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
}
