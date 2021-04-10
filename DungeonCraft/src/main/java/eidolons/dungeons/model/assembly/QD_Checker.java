package eidolons.game.netherflame.dungeons.model.assembly;

import eidolons.game.netherflame.dungeons.QD_Enums;
import eidolons.game.netherflame.dungeons.model.QD_Module;

public class QD_Checker {
    public static boolean moduleType(QD_Module previous, QD_Enums.ModuleType value) {
        if (previous == null) {
            return false;
        }
        return previous.getData().getEnum(
                QD_Enums.ModuleProperty.type, QD_Enums.ModuleType.class) == value;
    }
}
