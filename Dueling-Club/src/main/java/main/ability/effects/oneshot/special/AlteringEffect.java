package main.ability.effects.oneshot.special;

import main.ability.effects.DC_Effect;
import main.system.math.Formula;
import main.system.math.MathMaster;

public class AlteringEffect extends DC_Effect {

    private String factor;
    private String mod;

    public AlteringEffect(Boolean modOrFactor, String formula) {
        if (modOrFactor == null)
            this.factor = formula;
        else if (modOrFactor)
            this.mod = formula;
        else
            this.factor = formula;
    }

    @Override
    public boolean applyThis() {
        // ref.setAltered(true);
        if (ref.getAmount() == null)
            return false;
        if (factor != null)
            ref.setAmount(MathMaster.addFactor(ref.getAmount(), new Formula(
                    factor).getInt(ref)));
        else
            ref.setAmount(MathMaster.applyMod(ref.getAmount(),
                    new Formula(mod).getInt(ref)));
        return true;
    }

}
