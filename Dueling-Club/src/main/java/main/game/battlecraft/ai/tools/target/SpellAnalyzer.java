package main.game.battlecraft.ai.tools.target;

import main.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import main.entity.active.DC_ActiveObj;

public class SpellAnalyzer {

    public static boolean isCounterModSpell(DC_ActiveObj active) {
        return (EffectFinder.check(active.getAbilities(),
                ModifyCounterEffect.class));
    }

}