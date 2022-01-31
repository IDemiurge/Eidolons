package eidolons.game.battlecraft.ai.tools.future;

import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FutureBuilder {

    public static final int LETHAL_DAMAGE = -666;
    private static final Map<String, Integer> cache = new HashMap<>();
    private static final Map<String, Integer> minCache = new HashMap<>();
    private static final Map<String, Integer> maxCache = new HashMap<>();

    public static int precalculateDamage(ActiveObj active, Obj targetObj, boolean attack) {
        return precalculateDamage(active, targetObj, attack, null);
    }

    public static int precalculateDamage(ActiveObj active, Obj targetObj, boolean attack,
                                         Boolean min_max_normal) {
        // TODO basically, I have to use a copy of the gamestate...! To make it
        // precise...
        Map<String, Integer> cache = getCache(min_max_normal);
        if (targetObj == null)
            targetObj = active.getOwnerUnit();
        Integer damage = cache.get(getCacheKey(active, targetObj));
        if (damage != null)
            return damage;
        damage = 0;
        if (!active.isConstructed()) {
            active.construct();
        }

        List<Effect> effects;
        effects = EffectMaster.getEffectsOfClass(EffectMaster.getEffectsFromSpell(active),
         attack ? AttackEffect.class : DealDamageEffect.class);
        // TODO special effects?!
        for (Effect e : effects) {
            damage += getDamage(active, targetObj, e, min_max_normal);
        }
        cache.put(active.getNameAndId() + targetObj.getNameAndId(), damage);
        return damage;
    }

    private static String getCacheKey(ActiveObj active, Obj targetObj) {
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

    public static int getDamage(ActiveObj active, Obj targetObj, Effect e) {
        return getDamage(active, targetObj, e, null);
    }

    public static int getDamage(ActiveObj active, Obj targetObj, Effect e,
                                Boolean min_max_normal) {
        Integer damage;
        Ref ref = active.getOwnerUnit().getRef().getCopy();
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
