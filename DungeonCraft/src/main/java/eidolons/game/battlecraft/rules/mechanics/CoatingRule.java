package eidolons.game.battlecraft.rules.mechanics;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.math.MathMaster;

public class CoatingRule {
    /*
     * Manage counters! Alchemy mastery?
	 * 
	 * Use up *some* of the charges
	 * 
	 * Limit counters on the item
	 * 
	 * Manage counter application to the target
	 * 
	 * Handle the 'special effects' (removes the multi-application problem!)
	 * 
	 * ++ CoatingEffect ++ Charges on Coating and Item Mastery as variable
	 */

    public static final COUNTER[] COATING_COUNTERS = {
     COUNTER.Poison, COUNTER.Disease,
     COUNTER.Blight, COUNTER.Corrosion,
     COUNTER.Blaze, COUNTER.Chill,
     COUNTER.Moist,};
    public static final Integer RANGED_MOD = 2;
    public static final Integer THROWING_MOD = 5;

    public static int getMaxNumberOfCountersApplied(Obj item,
                                                    COUNTER counter) {
        // SIZE
        // 'SKILL'!
        return 0;

    }

    public static int getMaxNumberOfCountersPerAttack(Obj item,
                                                      COUNTER counter) {
        switch (counter) {
            case Blaze:
            case Poison:

            case Moist:

            case Chill:

            case Disease:
            case Corrosion:
            case Blight:
            case Bleeding:
                return 5;

        }
        return 1;
    }

    public static void unitIsHit(BattleFieldObject target, Unit source,
                                 boolean offhand, DC_ActiveObj action, Attack attack, DC_Obj weapon) {
        boolean throwing = false;

        // if (weapon instanceof DC_WeaponObj) {
        // DC_WeaponObj weaponObj = (DC_WeaponObj) weapon;
        if (action.isRanged()) {
            if (action.getRef().getObj(KEYS.RANGED) != null) {
                if (!action.isThrow()) {
                    if (action.getRef().getObj(KEYS.RANGED).getRef()
                     .getObj(KEYS.AMMO) != null) {
                        weapon = (DC_Obj) action.getRef().getObj(KEYS.RANGED)
                         .getRef().getObj(KEYS.AMMO);
                    }
                }
            }
        }

        // }

        if (action instanceof DC_QuickItemAction) {
            weapon = (DC_Obj) action.getRef().getObj(KEYS.ITEM);
            throwing = true;
        }

        DC_Obj armor = (DC_Obj) target.getRef().getObj(KEYS.ARMOR);
        // if (armor == null) //interesting....
        // armor = source;

        for (COUNTER c : COATING_COUNTERS) {
            boolean ranged = action.isRanged() || throwing;
            applyCounters(target, weapon, source, c, action, throwing);

            if (ranged) // TODO throwing doesn't count?
            {
                continue;
            }
            // TODO apply to weapon instead? :)
            // counters could have effect on items as well, durability at least
            if (armor != null) {
                applyCounters(source, armor, target, c, action, throwing);
            }
        }

    }

    private static void applyCounters(BattleFieldObject target, DC_Obj item,
                                      BattleFieldObject source, COUNTER c, DC_ActiveObj action,
                                      boolean throwing) {
        if (item.getCounter(c) <= 0) {
            return;
        }
        Integer max = getMaxNumberOfCountersPerAttack(item, c);
        max = MathMaster.addFactor(max, source.getIntParam(DC_ContentValsManager
         .getCoatingMaxPerHitModParam(c)));
        if (throwing) {
            max *= THROWING_MOD;
        } else if (action.isRanged()) {
            max *= RANGED_MOD;
        }
        int modValue = Math.min(item.getCounter(c), max);

        target.getGame().getLogManager().logCoating(target, item, source, c);
        target.modifyCounter(c.getName(), modValue);
        item.modifyCounter(c.getName(), -modValue);
    }

    public enum COATING_COUNTERS {
        POISON, DISEASE, BLIGHT, CORROSION, BLAZE, FREEZE, MOIST,;
        boolean spent;
        int maxApplied;
    }

}
