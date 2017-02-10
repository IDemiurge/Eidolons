package main.rules.mechanics;

import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.rules.generic.RoundRule;
import main.system.ai.tools.ParamAnalyzer;

public class MoraleRule extends RoundRule {

    public MoraleRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(DC_HeroObj unit) {
        return !ParamAnalyzer.isMoraleIgnore(unit);
    }

    @Override
    public void apply(DC_HeroObj unit) {
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
