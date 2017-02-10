package main.rules.mechanics;

import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.rules.generic.RoundRule;
import main.system.ai.tools.ParamAnalyzer;
import main.system.math.DC_MathManager;

public class FocusRule extends RoundRule {

    public FocusRule(DC_Game game) {
        super(game);
    }

    public boolean check(DC_HeroObj unit) {
        return !ParamAnalyzer.isFocusIgnore(unit);
    }

    public void apply(DC_HeroObj unit) {

        int diff = unit.getIntParam(PARAMS.C_FOCUS)
                - DC_MathManager.getStartingFocus(unit);
        if (diff == 0) {
            return;
        }
        boolean restore = diff < 0;
        int mod;
        if (restore) {
            mod = 25
                    // DC_Formulas.FOCUS_RESTORE_PERC
                    + unit.getIntParam(PARAMS.FOCUS_RESTORATION);
        } else {
            mod = 25
                    // DC_Formulas.FOCUS_REDUCE_PERC
                    - unit.getIntParam(PARAMS.FOCUS_RETAINMENT);
        }
        mod = Math.min(100, mod);
        int amount = diff;
        if (mod > 0) {
            amount = diff * mod / 100;
        }

        if (restore) {
            unit.modifyParameter(PARAMS.C_FOCUS, -amount, 100);
        } else {
            unit.modifyParameter(PARAMS.C_FOCUS, -amount, 0);
        }

        // game.getLogManager().log(string);

    }

}
