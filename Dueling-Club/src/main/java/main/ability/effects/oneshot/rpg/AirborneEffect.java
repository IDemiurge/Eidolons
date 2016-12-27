package main.ability.effects.oneshot.rpg;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.PARAMS;

/**
 * Created by JustMe on 12/26/2016.
 */
public class AirborneEffect extends DC_Effect {

    @Override
    public boolean applyThis() {
        new ModifyValueEffect(PARAMS.HEIGHT, Effect.MOD.MODIFY_BY_CONST, ""
        ).apply(ref);

        return true;

    }
}
