package main.ability.effects.special;

import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.system.math.Formula;

public class DrainEffect extends ModifyValueEffect {
    boolean resistanceApplies;
    boolean armorApplies;
    boolean alwaysRestoreFull = false;
    private Formula buffer;

    public DrainEffect(String sparam, Formula formula) {
        this(ContentManager.getPARAM(sparam), formula);
    }

    public DrainEffect(PARAMETER param, Formula formula) {
        super();
        this.formula = formula;
        this.param = param;
        this.mod_type = MOD.MODIFY_BY_CONST;
    }

    @Override
    public boolean applyThis() {
        applyDrain();
        applyRestoration();
        return true;
    }

    private void applyDrain() {
        ref.setTarget(target);
        if (buffer != null) {
            formula = buffer;
        }
        formula = this.formula.getNegative();
        setMaxParam(null);
        if (resistanceApplies) // resistance rule (mod) should take care of
            // this!
        {
            formula.addResistance();
        }
        super.applyThis();

    }

    private void applyRestoration() {
        ref.setTarget(source);
        formula = formula.getNegative();
        setMaxParam(getMaxParameter(param));
        if (!alwaysRestoreFull) {
            buffer = formula;
            formula = new Formula("" + -amount_modified);
        }
        super.applyThis();
    }

}
