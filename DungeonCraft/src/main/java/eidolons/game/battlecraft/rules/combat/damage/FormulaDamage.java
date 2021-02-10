package eidolons.game.battlecraft.rules.combat.damage;

import main.system.math.Formula;

/**
 * Created by JustMe on 3/18/2017.
 */
public class FormulaDamage extends Damage {
    private Formula formula;
    private boolean percentage;
    private boolean fromRaw;

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public boolean isPercentage() {
        return percentage;
    }

    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
    }

    @Override
    public Integer getAmount() {
        setAmount(formula.getInt(getRef()));
        return amount;

    }

    public boolean isFromRaw() {
        return fromRaw;
    }

    public void setFromRaw(boolean fromRaw) {
        this.fromRaw = fromRaw;
    }
}
