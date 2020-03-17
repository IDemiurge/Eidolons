package main.level_editor.backend.struct.boss;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.tree.data.LayeredData;

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
