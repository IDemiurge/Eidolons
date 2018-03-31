package eidolons.game.battlecraft.ai.tools.target;

import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.entity.active.DC_ActiveObj;

public class SpellAnalyzer {

    public static boolean isCounterModSpell(DC_ActiveObj active) {
        return (EffectFinder.check(active.getAbilities(),
         ModifyCounterEffect.class));
    }

}
