package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.system.math.Formula;

public class BashEffect extends DC_Effect implements OneshotEffect {

    private Formula formulaIni;
    private Formula formulaFocus;

    public BashEffect(String formulaIni, String formulaFocus) {
        this.formulaIni = new Formula(formulaIni);
        this.formulaFocus = new Formula(formulaFocus);
    }

    @Override
    public boolean applyThis() {
        int mod = 100 - getTarget().getIntParam(PARAMS.BASH_RESISTANCE);
        // generate from force? try inflict skull crush...
        int amount = formulaIni.getInt(ref) * mod / 100;
        if (mod != 0) {
            amount = -amount * mod / 100;
            new ModifyValueEffect(PARAMS.C_INITIATIVE_BONUS, MOD.MODIFY_BY_CONST, "" + amount)
             .apply(ref);
        }

        amount = formulaFocus.getInt(ref) * mod / 100;
        if (mod != 0) {
            amount = -amount * mod / 100;
            new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, "" + amount).apply(ref);
        }
        return true;
    }

}
