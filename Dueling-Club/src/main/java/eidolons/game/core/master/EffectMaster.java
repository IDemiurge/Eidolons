package eidolons.game.core.master;

import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.ActiveObj;

/**
 * Created by JustMe on 2/16/2017.
 */
public class EffectMaster extends Master {
    public EffectMaster(DC_Game game) {
        super(game);
    }

    public static AttackEffect getAttackEffect(ActiveObj action) {
        AttackEffect effect = (AttackEffect) EffectFinder.getEffectsOfClass((DC_ActiveObj) action,
         AttackEffect.class).get(0);
        return effect;
    }

    public static Effect getDisablingEffect() {
        return new AddStatusEffect(UnitEnums.STATUS.DISABLED);
    }
}
