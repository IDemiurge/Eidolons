package main.ability.effects.oneshot;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.logic.combat.damage.*;
import main.game.logic.combat.mechanics.ForceRule;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.util.Arrays;
import java.util.List;

public class DealDamageEffect extends DC_Effect implements OneshotEffect {
    private DAMAGE_TYPE damage_type;
    private boolean magical = true;
    private DAMAGE_MODIFIER damage_mod;
    private DAMAGE_MODIFIER[] damage_mods; //will override damage_mod
    private Damage damageObject;

    // private int damage_dealt = 0;

    // damage type?!
    public DealDamageEffect(Formula formula) {
        super();
        this.formula = formula;
    }

    public DealDamageEffect(Formula formula, Boolean magical) {
        this(formula);
        this.magical = magical;
    }

    public DealDamageEffect(Formula formula, DAMAGE_TYPE damage_type) {
        this(formula);
        this.damage_type = damage_type;
    }

    public DealDamageEffect(Formula formula, String damage_type, DAMAGE_MODIFIER damage_mod) {
        this(formula, new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
         damage_type));
        this.damage_mod = damage_mod;
    }

    @OmittedConstructor
    public DealDamageEffect(String damage_type, Formula formula, DAMAGE_MODIFIER... damage_mods) {
        this(formula, new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
         damage_type));
        this.damage_mods = damage_mods;
    }

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() == null) {
            return false;
        }
        if (!(ref.getTargetObj() instanceof Unit)) {
            return true; // TODO if cell, apply damage to corpses?
        }
        if (checkDamageMod(DAMAGE_MODIFIER.QUIET)) {
            getRef().setQuiet(true);
        } else {
            getRef().setQuiet(false);
        }

        Unit targetObj = (Unit) ref.getTargetObj();
        int amount = formula.getAppendedByModifier(ref.getFormula()).getInt(ref);
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        boolean spell = active instanceof DC_SpellObj;

        initDamageType();
        if (!checkDamageMod(DAMAGE_MODIFIER.UNBLOCKABLE))
            amount = ArmorMaster.getShieldReducedAmountForDealDamageEffect(this, targetObj, amount, active);

        LogMaster.log(LogMaster.COMBAT_DEBUG, "Effect is dealing damage: "
         + amount + " to " + ref.getTargetObj().toString());
        saveDamageModsToRef();

        ref.setValue(KEYS.DAMAGE_TYPE, damage_type.getName());

        int damage = DamageDealer.dealDamageOfType(
         getDamageObject(amount)
//         damage_type, targetObj, ref, amount
        );

        // if (active.getIntParam(PARAMS.FORCE) == 0) // ONLY MAIN SPELL'S
        // DAMAGE
        if (damage > 0) {
            if (!ref.isPeriodic()) {
                if (!ref.isTriggered()) {
                    if (!active.isAttack()) {
                        ForceRule.applyForceEffects(active);
                    }

                }
            }
        }


        return true;

    }

    private void saveDamageModsToRef() {
        if (damage_mods != null) {
            ref.setValue(KEYS.DAMAGE_SOURCE, StringMaster.constructStringContainer(Arrays.asList(damage_mods)));
        } else if (damage_mod != null) {
            ref.setValue(KEYS.DAMAGE_SOURCE, damage_mod.toString());
        }
    }

    public boolean checkDamageMod(DAMAGE_MODIFIER unblockable) {
        if (damage_mod == unblockable)
            return true;
        if (damage_mods != null) {
            return new ArrayMaster<DAMAGE_MODIFIER>().contains(damage_mods, unblockable);
        }
        return false;
    }

    private void initDamageType() {
        if (damage_type == null) {
            try {
                damage_type = ((DC_Obj) ref.getObj(KEYS.ACTIVE)).getDamageType();
            } catch (Exception ignored) {
            }
        }
        if (damage_type == null) {
            if (magical) {
                damage_type = GenericEnums.DAMAGE_TYPE.MAGICAL;
            } else {
                damage_type = GenericEnums.DAMAGE_TYPE.PHYSICAL;
            }
        }
    }

    @Override
    public String toString() {
        String string = "DMG: " + formula;
        if (damage_type != null) {
            string += StringMaster.wrapInParenthesis(damage_type.getName());
        }
        return string;
    }

    public DAMAGE_TYPE getDamageType() {
        return damage_type;
    }

    public boolean isMagical() {
        return magical;
    }


    public DAMAGE_MODIFIER getDamage_mod() {
        return damage_mod;
    }

    private Damage getDamageObject(int amount) {
        List<Damage> list = DamageCalculator.getBonusDamageList(ref, isMagical() ? DAMAGE_CASE.SPELL : DAMAGE_CASE.ACTION);
        if (!list.isEmpty())
            damageObject = new MultiDamage(damage_type, ref, amount, list);
        else damageObject = new Damage(damage_type, ref, amount);

        return damageObject;
    }


}
