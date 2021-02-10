package eidolons.ability.effects.oneshot.attack;

import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.properties.G_PROPS;
import main.data.ability.AE_ConstrArgs;
import main.entity.Ref.KEYS;

public class ThrowEffect extends AttackEffect {

    boolean stormOfMissiles;
    private Boolean fromHand = true;

    public ThrowEffect() {
        this(false, false);
    }

    @AE_ConstrArgs(argNames = {"stormOfMissiles"})
    public ThrowEffect(Boolean stormOfMissiles) {
        this.stormOfMissiles = stormOfMissiles;
    }

    @AE_ConstrArgs(argNames = {"fromHand", "offhand"})
    public ThrowEffect(Boolean fromHand, Boolean offhand) {
        this.offhand = offhand;
        this.fromHand = fromHand;
    }

    @Override
    public boolean applyThis() {
        if (stormOfMissiles) {
            fromHand = true;
            DC_WeaponObj weapon = (DC_WeaponObj) ref.getObj(KEYS.WEAPON);
            boolean result = throwWeapon(weapon);
            weapon = (DC_WeaponObj) ref.getObj(KEYS.OFFHAND);
            if (weapon != null) {
                offhand = true;
                result = throwWeapon(weapon);
            }

            Unit hero = (Unit) ref.getObj(KEYS.SOURCE);
            fromHand = false;
            for (DC_QuickItemObj q : hero.getQuickItems()) {
                weapon = q.getWrappedWeapon();
                if (weapon != null) {
                    result &= throwWeapon(weapon);
                }
            }
            return result;
        }
        DC_WeaponObj weapon = (DC_WeaponObj) ref.getObj(KEYS.WEAPON);
        if (!fromHand)
        {
            weapon =  ((DC_QuickItemObj) ref.getObj(KEYS.ITEM)).getWrappedWeapon();
        }
        try {
            if (offhand
             || ref.getObj(KEYS.ACTIVE).checkProperty(G_PROPS.ACTION_TAGS,
             "" + ActionEnums.ACTION_TAGS.OFF_HAND)) {
                weapon = (DC_WeaponObj) ref.getObj(KEYS.OFFHAND);
                offhand = true;
            }
            // offhand mods?
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        // set mods briefly? or should the action itself have those?
        // what about costs, are they static or variable?
        // I wanted to enable throwing the equipped weapons as well...

        // new DurabilityReductionEffect(attacker, dmg_amount).apply(ref);

        // target.addStuckItem(weapon);

        return throwWeapon(weapon);
    }

    private boolean throwWeapon(DC_WeaponObj weapon) {
        if (ref.getTargetObj()==ref.getSourceObj()){
            Unit hero = (Unit) ref.getSourceObj();
            if (!hero.equip(weapon, ITEM_SLOT.MAIN_HAND)){
                hero.equip(weapon, ITEM_SLOT.OFF_HAND);
            }
            return true;
        }
        ref.setID(KEYS.WEAPON, weapon.getId());
        ref.setValue(KEYS.DAMAGE_TYPE, weapon.getDamageType().getName());
        setWeapon(weapon);
        setOffhand(offhand);
        boolean result = super.applyThis();
        if (fromHand) {
            Unit hero = (Unit) ref.getSourceObj();
            if (offhand) {
                hero.unequip(ITEM_SLOT.OFF_HAND);
                hero.dropItemFromInventory(weapon, ref.getTargetObj().getCoordinates());
            } else {
                hero.unequip(ITEM_SLOT.MAIN_HAND);
                hero.dropItemFromInventory(weapon, ref.getTargetObj().getCoordinates());
                if (hero.getOffhandWeapon() != null) {
                    if (!hero.getOffhandWeapon().isRanged()) {
                        if (hero.getOffhandWeapon().isWeapon()) {
                            hero.setWeapon(hero.getOffhandWeapon());
                            hero.setSecondWeapon(null);
                        }
                    }
                }
            }
        }
        weapon.getGame().getDroppedItemManager().itemFalls(
         ref.getTargetObj().getCoordinates(),
         weapon);

        getActiveObj().getRef().setID(offhand ? KEYS.OFFHAND : KEYS.WEAPON, null);

        return result;
    }

}
