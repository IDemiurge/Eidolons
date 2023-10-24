package logic.rules.combat.wounds;

import elements.exec.effect.Effect;
import elements.stats.UnitProp;
import logic.rules.combat.wounds.content.Wound;
import main.content.enums.GenericEnums;

/**
 * Created by Alexander on 8/21/2023
 */
public class SanityWounds extends WoundsRule {
    @Override
    protected Effect getEffect(Wound wound) {
        return null;
    }

    @Override
    protected UnitProp getWoundValue(Wound wound) {
        return null;
    }

    @Override
    protected GenericEnums.DieType getDie() {
        return null;
    }

    @Override
    protected Wound getWound(int rolled) {
        return null;
    }
}
