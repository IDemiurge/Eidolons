package main.game.battlecraft.rules.round;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.core.game.DC_Game;

public class MoraleRule extends RoundRule {

    public MoraleRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(Unit unit) {
        return !ParamAnalyzer.isMoraleIgnore(unit);
    }

    @Override
    public void apply(Unit unit) {
        int diff = unit.getIntParam(PARAMS.C_MORALE)
         - unit.getIntParam(PARAMS.MORALE);
        if (diff == 0) {
            return;
        }
        boolean restore = diff < 0;
        int mod;
        if (restore) {
            mod = 25
             // DC_Formulas.FOCUS_RESTORE_PERC
             + unit.getIntParam(PARAMS.MORALE_RESTORATION);
        } else {
            mod = 25
             // DC_Formulas.FOCUS_REDUCE_PERC
             - unit.getIntParam(PARAMS.MORALE_RETAINMENT);
        }
        mod = Math.min(100, mod);
        int amount = Math.abs(diff);
        if (mod > 0) {
            amount = amount * mod / 100;
        } else {
            return;
        }
        if (restore) {
            unit.modifyParameter(PARAMS.C_MORALE, amount, unit
             .getIntParam(PARAMS.MORALE));
        } else {
            unit.modifyParameter(PARAMS.C_MORALE, -amount);
        }

    }

}
