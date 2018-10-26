package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.system.math.DC_MathManager;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.RESIST_GRADE;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.EnumMaster;
import main.system.math.Formula;

public class ResistMaster {

    private static final String RESISTANCE = "{TARGET_RESISTANCE}";

    public static void initUnitResistances(BattleFieldObject unit) {
        int resist = unit.getIntParam(PARAMS.RESISTANCE);
        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            PARAMETER resistForDmgType = DC_ContentValsManager.getResistForDmgType(dmg_type);
            if (resistForDmgType != null) {
                if (dmg_type.isMagical()) {
                    RESIST_GRADE grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(
                     RESIST_GRADE.class, unit.getProperty(DC_ContentValsManager
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

    public static int getResistanceForDamageType(BattleFieldObject attacked,
                                                 BattleFieldObject attacker, DAMAGE_TYPE type) {
        if (type == null) {
            return 0;
        }
        int resistance = DC_MathManager.getDamageTypeResistance(attacked, type);
        if (type.isMagical()) {
            resistance -= attacker.getIntParam(PARAMS.RESISTANCE_PENETRATION);
        }
        return resistance;
    }

    public static void addResistance(Formula formula) {
        formula.setFormula("((" + formula + ")*100-" +
         formula + "*" + RESISTANCE + ")/100"
        );

    }
}
