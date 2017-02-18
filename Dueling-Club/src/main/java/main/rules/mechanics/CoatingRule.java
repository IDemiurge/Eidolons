package main.rules.mechanics;

import main.content.enums.entity.UnitEnums.STD_COUNTERS;
import main.content.DC_ContentManager;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.attack.Attack;
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

    public static final STD_COUNTERS[] COATING_COUNTERS = {
            UnitEnums.STD_COUNTERS.Poison_Counter, UnitEnums.STD_COUNTERS.Disease_Counter,
            UnitEnums.STD_COUNTERS.Blight_Counter, UnitEnums.STD_COUNTERS.Corrosion_Counter,
            UnitEnums.STD_COUNTERS.Blaze_Counter, UnitEnums.STD_COUNTERS.Freeze_Counter,
            UnitEnums.STD_COUNTERS.Moist_Counter,};
    public static final Integer RANGED_MOD = 2;
    public static final Integer THROWING_MOD = 5;

    public static int getMaxNumberOfCountersApplied(Obj item,
                                                    STD_COUNTERS counter) {
        // SIZE
        // 'SKILL'!
        return 0;

    }

    public static int getMaxNumberOfCountersPerAttack(Obj item,
                                                      STD_COUNTERS counter) {
        switch (counter) {
            case Blaze_Counter:
                return 5;
            case Bleeding_Counter:
                return 5;
            case Blight_Counter:
                return 5;
            case Corrosion_Counter:
                return 5;

            case Disease_Counter:
                return 5;

            case Freeze_Counter:
                return 5;

            case Moist_Counter:
                return 5;
            case Poison_Counter:
                return 5;

        }
        return 1;
    }

    public static void unitIsHit(Unit target, Unit source,
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

        if (action instanceof DC_ItemActiveObj) {
            weapon = (DC_Obj) action.getRef().getObj(KEYS.ITEM);
            throwing = true;
        }

        DC_Obj armor = target.getArmor();
        // if (armor == null) //interesting....
        // armor = source;

        for (STD_COUNTERS c : COATING_COUNTERS) {
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

    private static void applyCounters(Unit target, DC_Obj item,
                                      Unit source, STD_COUNTERS c, DC_ActiveObj action,
                                      boolean throwing) {
        if (item.getCounter(c) <= 0) {
            return;
        }
        Integer max = getMaxNumberOfCountersPerAttack(item, c);
        max = MathMaster.addFactor(max, source.getIntParam(DC_ContentManager
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
