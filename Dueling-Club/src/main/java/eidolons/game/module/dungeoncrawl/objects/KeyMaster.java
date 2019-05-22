package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.Ref;

public class KeyMaster {
    public static boolean hasKey(Unit unit, Door door) {
        String type = door.getProperty(PROPS.KEY_TYPE);
        DC_HeroItemObj item = unit.findItem(type, true);
        if (item != null) {
            return true;
        }
        item = unit.findItem(type, false);
        if (item != null) {
            return true;
        }
        item = unit.findItem(type, null);
        if (item != null) {
            return true;
        }
        return false;
    }

    public static void initAnimRef(DungeonObj obj, Ref ref) {
    }
}
