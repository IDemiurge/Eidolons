package main.game.battlecraft.ai.tools.future;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.DealDamageEffect;
import main.ability.effects.oneshot.attack.AttackEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.battlecraft.rules.combat.attack.Attack;
import main.game.battlecraft.rules.combat.damage.DamageCalculator;
import main.system.auxiliary.log.LogMaster;

import java.util.List;

public class FutureBuilder {

    public static final int LETHAL_DAMAGE = -666;

    public static int precalculateDamage(DC_ActiveObj active, Obj targetObj, boolean attack) {
        // TODO basically, I have to use a copy of the gamestate...! To make it
        // precise...
        int damage = 0;
        if (!active.isConstructed()) {
            active.construct();
        }

        List<Effect> effects;
        effects = EffectFinder.getEffectsOfClass(EffectFinder.getEffectsFromSpell(active),
                attack ? AttackEffect.class : DealDamageEffect.class);
        // TODO special effects?!
        for (Effect e : effects) {
            damage += getDamage(active, targetObj, e);
        }
        return damage;
    }

    public static int getDamage(DC_ActiveObj active, Obj targetObj, Effect e) {
        int damage;
        Ref ref = active.getOwnerObj().getRef().getCopy();
        ref.setTarget(targetObj.getId());
        ref.setID(KEYS.ACTIVE, active.getId());
        e.setRef(ref);
        if (e instanceof DealDamageEffect) {
            ref.setAmount(e.getFormula().getInt(ref));
            if (((DealDamageEffect) e).getDamageType() != null) {
                ref.setValue(KEYS.DAMAGE_TYPE, ((DealDamageEffect) e).getDamageType().toString());
            }

            damage = DamageCalculator.precalculateDamage(ref);

        } else {
            Attack attack = ((AttackEffect) e).initAttack();
            // attack.setAttacked((DC_HeroObj) targetObj);
            damage = DamageCalculator.precalculateDamage(attack);
        }
        // active.toBase();
        LogMaster.log(1, active.getName() + " on " + targetObj.getName()
                + " - damage precalculated: " + damage);
        return damage;
    }

	/*
     * precalc damage dealt at least
	 * 
	 * initiative sequence
	 */

    public static Unit getClone(Unit source) {
        // TODO Auto-generated method stub
        return null;
    }
}
