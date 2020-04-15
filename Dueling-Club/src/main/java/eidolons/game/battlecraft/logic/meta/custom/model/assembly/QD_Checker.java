package eidolons.game.battlecraft.logic.meta.custom.model.assembly;

import eidolons.game.battlecraft.logic.meta.custom.QD_Enums;
import eidolons.game.battlecraft.logic.meta.custom.model.QD_Module;

public class QD_Checker {
    public static boolean moduleType(QD_Module previous, QD_Enums.ModuleType value) {
        if (previous == null) {
            return false;
        }
        return previous.getData().getEnum(value, QD_Enums.ModuleType.class) == value;
    }
}
