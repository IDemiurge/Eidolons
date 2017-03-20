package main.game.logic.combat.damage;

import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.RESIST_GRADE;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.EnumMaster;
import main.system.math.DC_MathManager;

public class ResistMaster {

    public static void initUnitResistances(Unit unit) {
        int resist = unit.getIntParam(PARAMS.RESISTANCE);
        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            PARAMETER resistForDmgType = DC_ContentManager.getResistForDmgType(dmg_type);
            if (resistForDmgType != null) {
                if (dmg_type.isMagical()) {
                    RESIST_GRADE grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(
                            RESIST_GRADE.class, unit.getProperty(DC_ContentManager
                                    .getResistGradeForDmgType(dmg_type)));
                    if (grade == null) {
                        grade = GenericEnums.RESIST_GRADE.Normal;
                    }
                    int amount = resist * grade.getPercent() / 100;
                    unit.modifyParameter(resistForDmgType, amount);

                }
            }
        }

    }

    public static int getResistanceForDamageType(Unit attacked,
                                                  Unit attacker, DAMAGE_TYPE type) {
        if (type == null) {
            return 0;
        }
        int resistance = DC_MathManager.getDamageTypeResistance(attacked, type);
        if (type.isMagical()) {
            resistance -= attacker.getIntParam(PARAMS.RESISTANCE_PENETRATION);
        }
        return resistance;
    }
}
