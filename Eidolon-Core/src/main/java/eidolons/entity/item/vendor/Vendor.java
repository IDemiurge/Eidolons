package eidolons.entity.item.vendor;

import eidolons.game.exploration.dungeon.objects.DungeonObj;
import main.entity.type.ObjType;

public class Vendor extends DungeonObj {
    public Vendor(ObjType type, int x, int y) {
        super(type, x, y);
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return null;
    }
}
