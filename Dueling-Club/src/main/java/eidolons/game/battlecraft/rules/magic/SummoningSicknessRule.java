package eidolons.game.battlecraft.rules.magic;

import eidolons.content.PARAMS;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbController;
import main.entity.obj.ActiveObj;

public class SummoningSicknessRule {

    public static void apply(Unit unit, ActiveObj active) {
        // getBuffEffect().apply(Ref.getSelfTargetingRefCopy(unit));
        // unit.setParam(PARAMS.C_INITIATIVE_BONUS, i);
        // unit.getParams(PARAMS.C_INITIATIVE, i);
        // unit.getGame().getRules().getTimeRule().getTimePercentageRemaining();

        if (active instanceof Spell) {
            if (((Spell) active).checkParam(PARAMS.SUMMON_ATB)){
            int i = ((Spell) active).getIntParam(PARAMS.SUMMON_ATB);
            unit.setParam(PARAMS.C_INITIATIVE, i* 100);
                unit.setParam(PARAMS.ATB_START_PRESET, i, true, true);
            }
        }
//outdated

//        unit.modifyParameter(PARAMS.C_INITIATIVE_BONUS, -(unit.getGame()
//         .getRules().getTimeRule().getBaseTime() - unit.getGame()
//         .getRules().getTimeRule().getTimeRemaining()));

    }

}
