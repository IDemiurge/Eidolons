package main.ability.ignored.oneshot;

import main.ability.effects.MicroEffect;
import main.system.math.Formula;

public class AlterFormulaEffect extends MicroEffect {
    private ALTERATION_TYPE type;

    public AlterFormulaEffect(ALTERATION_TYPE type) {
        this.type = type;
    }

    public AlterFormulaEffect(Formula formula) {
        this.formula = formula;
    }

    @Override
    public boolean applyThis() {
        if (this.formula == null) {
            this.formula = ref.getEffect().getFormula();

            switch (type) {
                case INVERT:
                    this.formula = formula.getInverted();
                    break;
                case NEGATIVE:
                    this.formula = formula.getNegative();
                    break;
                default:
                    break;
            }
        }
        try {
            ref.getEffect().setFormula(formula);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }

        return true;
    }

    public enum ALTERATION_TYPE {
        INVERT,
        NEGATIVE,

    }

}
