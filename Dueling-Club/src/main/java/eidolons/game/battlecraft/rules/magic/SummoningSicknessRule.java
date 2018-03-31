package eidolons.game.battlecraft.rules.magic;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;

public class SummoningSicknessRule {

    public static void apply(Unit unit) {
        // getBuffEffect().apply(Ref.getSelfTargetingRefCopy(unit));
        // unit.setParam(PARAMS.C_INITIATIVE_BONUS, i);
        // unit.getParams(PARAMS.C_INITIATIVE, i);
        // unit.getGame().getRules().getTimeRule().getTimePercentageRemaining();
        unit.modifyParameter(PARAMS.C_INITIATIVE_BONUS, -(unit.getGame()
         .getRules().getTimeRule().getBaseTime() - unit.getGame()
         .getRules().getTimeRule().getTimeRemaining()));

    }

}