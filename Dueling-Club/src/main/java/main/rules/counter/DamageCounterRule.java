package main.rules.counter;

import main.ability.effects.DealDamageEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.math.Formula;

public abstract class DamageCounterRule extends DC_CounterRule {

    public DamageCounterRule(DC_Game game) {
        super(game);

    }

    public String getSound() {
        // TODO Auto-generated method stub
        return null;
    }

    public abstract String getImage();

    public abstract DAMAGE_TYPE getDamageType();

    public abstract String getDamagePerCounterFormula();

    public abstract boolean isEnduranceOnly();

    public boolean apply(Unit unit) {
        if (!check(unit)) {
            return false;
        }

        Ref ref = Ref.getSelfTargetingRefCopy(unit);
        ref.setPeriodic(true);
        ref.setImage(getImage());
        return new DealDamageEffect(new Formula(getCounterRef() + " * ("
                + getDamagePerCounterFormula() + ")"),

                getDamageType().toString(),
                isEnduranceOnly() ? GenericEnums.DAMAGE_MODIFIER.PERIODIC : null).apply(ref);
    }

}
