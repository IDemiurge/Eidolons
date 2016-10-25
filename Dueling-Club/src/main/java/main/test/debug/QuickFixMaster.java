package main.test.debug;

import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.entity.type.ObjType;

public class QuickFixMaster {

    public static void applyTypeFixes(ObjType type) {
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.ACTIONS) {
            if (isWeaponSpecFixOn()) {
                // TODO or make it a dynamic hero-bonus fix?
                type.modifyParameter(PARAMS.ATTACK_MOD, 25);
            }
        }

    }

    private static boolean isWeaponSpecFixOn() {
        return true;
    }

}
