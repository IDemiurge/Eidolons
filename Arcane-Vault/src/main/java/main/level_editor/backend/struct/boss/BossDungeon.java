package main.level_editor.backend.struct.boss;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import main.data.tree.LayeredData;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;

import java.util.Collection;

public class BossDungeon extends LightweightEntity implements LayeredData<Location> {


    public BossDungeon(ObjType type) {
        super(type);
    }

    @Override
    public Collection<Location> getChildren() {
        return null;
    }
    /*
    global data?
    links?
    map
    intro data

     */

}
