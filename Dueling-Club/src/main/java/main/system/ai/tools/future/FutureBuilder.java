package main.system.ai.tools.future;

import main.ability.effects.AttackEffect;
import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.DamageMaster;
import main.game.battlefield.attack.Attack;
import main.system.ai.logic.target.EffectMaster;

import java.util.LinkedList;
import java.util.List;

public class FutureBuilder {

    public static final int LETHAL_DAMAGE = -666;

    public static int precalculateDamage(DC_ActiveObj active, Obj targetObj, boolean attack) {
        // TODO basically, I have to use a copy of the gamestate...! To make it
        // precise...
        int damage = 0;
        if (!active.isConstructed())
            active.construct();

        List<Effect> effects = new LinkedList<>();
        effects = EffectMaster.getEffectsOfClass(EffectMaster.getEffectsFromSpell(active),
                attack ? AttackEffect.class : DealDamageEffect.class);
        // TODO special effects?!
        for (Effect e : effects) {
            damage += getDamage(active, targetObj, e);
        }
        return damage;
    }

    public static int getDamage(DC_ActiveObj active, Obj targetObj, Effect e) {
        int damage = 0;
        Ref ref = active.getOwnerObj().getRef().getCopy();
        ref.setTarget(targetObj.getId());
        ref.setID(KEYS.ACTIVE, active.getId());
        e.setRef(ref);
        if (e instanceof DealDamageEffect) {
            ref.setAmount(e.getFormula().getInt(ref));
            if (((DealDamageEffect) e).getDamage_type() != null)
                ref.setValue(KEYS.DAMAGE_TYPE, ((DealDamageEffect) e).getDamage_type().toString());

            damage = DamageMaster.getDamage(ref);

        } else {
            Attack attack = ((AttackEffect) e).initAttack();
            // attack.setAttacked((DC_HeroObj) targetObj);
            damage = DamageMaster.getDamage(attack);
        }
        // active.toBase();
        main.system.auxiliary.LogMaster.log(1, active.getName() + " on " + targetObj.getName()
                + " - damage precalculated: " + damage);
        return damage;
    }

	/*
     * precalc damage dealt at least
	 * 
	 * initiative sequence
	 */

    public static DC_HeroObj getClone(DC_HeroObj source) {
        // TODO Auto-generated method stub
        return null;
    }
}
