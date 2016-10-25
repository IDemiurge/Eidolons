package main.game.battlefield;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.RESIST_GRADE;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.EnumMaster;

public class ResistMaster {

    public static void initUnitResistances(DC_HeroObj unit) {
        int resist = unit.getIntParam(PARAMS.RESISTANCE);
        for (DAMAGE_TYPE dmg_type : DAMAGE_TYPE.values()) {
            PARAMETER resistForDmgType = DC_ContentManager.getResistForDmgType(dmg_type);
            if (resistForDmgType != null)
                if (dmg_type.isMagical()) {
                    RESIST_GRADE grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(
                            RESIST_GRADE.class, unit.getProperty(DC_ContentManager
                                    .getResistGradeForDmgType(dmg_type)));
                    if (grade == null)
                        grade = RESIST_GRADE.Normal;
                    int amount = resist * grade.getPercent() / 100;
                    unit.modifyParameter(resistForDmgType, amount);

                }
        }

    }
}
