package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/18/2017.
 */
public class FormulaDamage extends  Damage {
    private Formula formula;
    private boolean percentage;
    private boolean fromRaw;

    public FormulaDamage(DAMAGE_TYPE damage_type, Ref ref,
                         Formula formula, boolean percentage) {
        super(damage_type, ref, 0);
        this.formula = formula;
        this.percentage = percentage;
    }

    public Formula getFormula() {
        return formula;
    }

    public boolean isPercentage() {
        return percentage;
    }

    @Override
    public Integer getAmount() {
        amount = formula.getInt(getRef());
        return amount;

    }

    public boolean isFromRaw() {
        return fromRaw;
    }

    public void setFromRaw(boolean fromRaw) {
        this.fromRaw = fromRaw;
    }
}
