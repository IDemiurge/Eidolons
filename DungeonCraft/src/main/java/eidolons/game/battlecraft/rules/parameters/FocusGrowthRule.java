package eidolons.game.battlecraft.rules.parameters;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.DC_SecondsRule;

public class FocusGrowthRule implements DC_SecondsRule {

    public void secondsPassed(Unit unit, int seconds) {
        Integer addValue = unit.getIntParam(PARAMS.FOCUS_REGEN);
        unit.modifyParameter(PARAMS.C_FOCUS, addValue * seconds, 100);
        //can it be negative?

    }
}
