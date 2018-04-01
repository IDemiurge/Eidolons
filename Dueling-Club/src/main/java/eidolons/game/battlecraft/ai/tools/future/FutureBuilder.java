package eidolons.game.battlecraft.ai.tools.future;

import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FutureBuilder {

    public static final int LETHAL_DAMAGE = -666;
    private static Map<String, Integer> cache = new HashMap<>();
    private static Map<String, Integer> minCache = new HashMap<>();
    private static Map<String, Integer> maxCache = new HashMap<>();

    public static int precalculateDamage(DC_ActiveObj active, Obj targetObj, boolean attack) {
        return precalculateDamage(active, targetObj, attack, null);
    }

    public static int precalculateDamage(DC_ActiveObj active, Obj targetObj, boolean attack,
                                         Boolean min_max_normal) {
        // TODO basically, I have to use a copy of the gamestate...! To make it
        // precise...
        Map<String, Integer> cache = getCache(min_max_normal);

        Integer damage = cache.get(getCacheKey(active, targetObj));
        if (damage != null)
            return damage;
        damage = 0;
        if (!active.isConstructed()) {
            active.construct();
        }

        List<Effect> effects;
        effects = EffectFinder.getEffectsOfClass(EffectFinder.getEffectsFromSpell(active),
         attack ? AttackEffect.class : DealDamageEffect.class);
        // TODO special effects?!
        for (Effect e : effects) {
            damage += getDamage(active, targetObj, e, min_max_normal);
        }
        cache.put(active.getNameAndId() + targetObj.getNameAndId(), damage);
        return damage;
    }

    private static String getCacheKey(DC_ActiveObj active, Obj targetObj) {
        return active.getNameAndId() + targetObj.getNameAndId();
    }

    public static void clearCaches() {
        getCache().clear();
        getMinCache().clear();
        getMaxCache().clear();
    }

    private static Map<String, Integer> getCache(Boolean min_max_normal) {
        if (min_max_normal == null) {
            return getCache();
        } else {
            return min_max_normal ? getMinCache() : getMaxCache();
        }
    }

    private static Map<String, Integer> getCache() {
        return cache;
    }

    private static Map<String, Integer> getMinCache() {
        return minCache;
    }

    private static Map<String, Integer> getMaxCache() {
        return maxCache;
    }

    public static int getDamage(DC_ActiveObj active, Obj targetObj, Effect e) {
        return getDamage(active, targetObj, e, null);
    }

    public static int getDamage(DC_ActiveObj active, Obj targetObj, Effect e,
                                Boolean min_max_normal) {
        Integer damage;
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
            Map<String, Integer> _cache = getCache(min_max_normal);
            damage = _cache.get(getCacheKey(active, targetObj));
            if (damage == null) {
                damage = DamageCalculator.precalculateDamage(attack, min_max_normal);
                _cache.put(getCacheKey(active, targetObj), damage);
            }
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
