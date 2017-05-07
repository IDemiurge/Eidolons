package main.game.core.master;

import main.ability.effects.oneshot.attack.AttackEffect;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.core.game.DC_Game;

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
}
