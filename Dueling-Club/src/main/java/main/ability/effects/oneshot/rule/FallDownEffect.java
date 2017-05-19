package main.ability.effects.oneshot.rule;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.ability.effects.oneshot.DealDamageEffect;
import main.content.enums.GenericEnums;
import main.system.math.Formula;

public class FallDownEffect extends DC_Effect  implements OneshotEffect {

    @Override
    public boolean applyThis() {
        // deal damage based on throw height and weight
        DealDamageEffect dmgEffect = new DealDamageEffect(new Formula(
                "sqrt({target_height})/10*{target_weight}/4"),
                GenericEnums.DAMAGE_TYPE.BLUDGEONING);
        return dmgEffect.apply(ref);
    }

}
