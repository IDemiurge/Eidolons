package main.ability.effects.oneshot;

import main.ability.effects.AttackEffect;
import main.content.CONTENT_CONSTS.ACTION_TAGS;
import main.content.properties.G_PROPS;
import main.data.ability.AE_ConstrArgs;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.DC_WeaponObj;

public class ThrowEffect extends AttackEffect {

    boolean stormOfMissiles;
    private Boolean fromHand = false;

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

            DC_HeroObj hero = (DC_HeroObj) ref.getObj(KEYS.SOURCE);
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
        try {
            if (offhand
                    || ref.getObj(KEYS.ACTIVE).checkProperty(G_PROPS.ACTION_TAGS,
                    "" + ACTION_TAGS.OFF_HAND)) {
                weapon = (DC_WeaponObj) ref.getObj(KEYS.OFFHAND);
                offhand = true;
            }
            // offhand mods?
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set mods briefly? or should the action itself have those?
        // what about costs, are they static or variable?
        // I wanted to enable throwing the equipped weapons as well...

        // new DurabilityReductionEffect(attacker, dmg_amount).apply(ref);

        // target.addStuckItem(weapon);

        return throwWeapon(weapon);
    }

    private boolean throwWeapon(DC_WeaponObj weapon) {
        ref.setID(KEYS.WEAPON, weapon.getId());
        ref.setValue(KEYS.DAMAGE_TYPE, weapon.getDamageType().getName());
        setWeapon(weapon);
        setOffhand(offhand);
        boolean result = super.applyThis();
        if (fromHand) {
            DC_HeroObj hero = (DC_HeroObj) ref.getSourceObj();
            if (offhand) {
                hero.setSecondWeapon(null);
            } else {
                hero.setWeapon(null);
                if (hero.getSecondWeapon() != null) {
                    if (!hero.getSecondWeapon().isRanged()) {
                        if (hero.getSecondWeapon().isWeapon()) {
                            hero.setWeapon(hero.getSecondWeapon());
                            hero.setSecondWeapon(null);
                        }
                    }
                }
            }
        }
        weapon.getGame().getDroppedItemManager().itemFalls(ref.getTargetObj().getCoordinates(),
                weapon);

        return result;
    }

}
