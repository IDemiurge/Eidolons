package main.rules.counter;

import main.ability.effects.DealDamageEffect;
import main.content.CONTENT_CONSTS.DAMAGE_MODIFIER;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
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

    public boolean apply(DC_HeroObj unit) {
        if (!check(unit)) {
            return false;
        }

        Ref ref = Ref.getSelfTargetingRefCopy(unit);
        ref.setPeriodic(true);
        ref.setImage(getImage());
        return new DealDamageEffect(new Formula(getCounterRef() + " * ("
                + getDamagePerCounterFormula() + ")"),

                getDamageType().toString(),
                isEnduranceOnly() ? DAMAGE_MODIFIER.PERIODIC : null).apply(ref);
    }

}
