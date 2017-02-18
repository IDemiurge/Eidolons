package main.game.ai.tools.target;

import main.ability.effects.oneshot.common.ModifyCounterEffect;
import main.entity.active.DC_ActiveObj;

public class SpellAnalyzer {

    public static boolean isCounterModSpell(DC_ActiveObj active) {
        return (EffectFinder.check(active.getAbilities(),
                ModifyCounterEffect.class));
    }

}
