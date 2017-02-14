package main.rules.magic;

import main.content.PARAMS;
import main.entity.obj.unit.DC_HeroObj;

public class SummoningSicknessRule {

    public static void apply(DC_HeroObj unit) {
        // getBuffEffect().apply(Ref.getSelfTargetingRefCopy(unit));
        // unit.setParam(PARAMS.C_INITIATIVE_BONUS, i);
        // unit.getParam(PARAMS.C_INITIATIVE, i);
        // unit.getGame().getRules().getLateActionsRule().getTimePercentageRemaining();
        unit.modifyParameter(PARAMS.C_INITIATIVE_BONUS, -(unit.getGame()
                .getRules().getTimeRule().getBaseTime() - unit.getGame()
                .getRules().getTimeRule().getTimeRemaining()));

    }

}
