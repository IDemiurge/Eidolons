package eidolons.content;

import eidolons.entity.obj.unit.Unit;

public class DC_Calculator {
    public static int getAS_AtbSwitchCost(Unit unit, int switched) {
        int base =DC_CONSTS.AS_SWITCH_ATB_BASE;
        Double mod = unit.getParamDouble(PARAMS.SWITCH_AS_ATB_COST_MOD);
        base = (int) Math.round(base*mod);
        return base * switched*switched;
    }
    public static int getAS_FocusSwitchCost(Unit unit, int switched) {
        int base =DC_CONSTS.AS_SWITCH_FOCUS_BASE;
        Double mod = unit.getParamDouble(PARAMS.SWITCH_AS_FOCUS_COST_MOD);
        base = (int) Math.round(base*mod);
        return base * switched*switched;
    }

    public static int getAccuracyRating(int defense, int attackValue) {
        return DC_Formulas.calculateAccuracyRating(defense, attackValue);
    }
}
