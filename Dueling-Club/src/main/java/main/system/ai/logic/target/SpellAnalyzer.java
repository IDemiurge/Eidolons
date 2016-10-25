package main.system.ai.logic.target;

import main.ability.effects.oneshot.common.ModifyCounterEffect;
import main.entity.obj.top.DC_ActiveObj;

public class SpellAnalyzer {

    public static boolean isCounterModSpell(DC_ActiveObj active) {
        return (EffectMaster.check(active.getAbilities(),
                ModifyCounterEffect.class));
    }

}
