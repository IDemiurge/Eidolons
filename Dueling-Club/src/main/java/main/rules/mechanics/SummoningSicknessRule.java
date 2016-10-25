package main.rules.mechanics;

import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;

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
