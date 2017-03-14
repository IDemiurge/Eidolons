package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.content.PARAMS;
import main.system.math.Formula;

public class LightEmittingEffect extends DC_Effect {
    boolean circular;
    private String rangeFormula = "3";
    private SpectrumEffect effect;

    public LightEmittingEffect(String formula, Boolean circular) {
        this.formula = new Formula(formula);
        this.circular = circular;
    }

    public LightEmittingEffect(String formula) {
        this(formula, true);
    }

    @Override
    public boolean applyThis() {
        // packaged into PassiveAbil
        // on self?
        return getEffect().apply(ref);
    }

    public SpectrumEffect getEffect() {
        if (effect == null) {
            effect =
                    // SpectrumEffect
                    new SpectrumEffect(new ModifyValueEffect(PARAMS.ILLUMINATION,
                            MOD.MODIFY_BY_CONST, formula.toString()), rangeFormula, circular);
            effect.setApplyThrough(true);

        }

        return effect;
    }

    public void setEffect(SpectrumEffect effect) {
        this.effect = effect;
    }
}
