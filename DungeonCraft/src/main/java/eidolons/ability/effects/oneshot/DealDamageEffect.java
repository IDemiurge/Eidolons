package eidolons.ability.effects.oneshot;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageDealer;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import main.ability.effects.OneshotEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.util.Arrays;

public class DealDamageEffect extends DC_Effect implements OneshotEffect {
    private DAMAGE_TYPE damage_type;
    private boolean magical = true;
    private DAMAGE_MODIFIER damage_mod;
    private DAMAGE_MODIFIER[] damage_mods; //will override damage_mod

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

    protected boolean checkEventsFired() {
        return true;
    }

    protected void fireAppliedEvent() {
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED, ref));
    }

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() == null) {
            return false;
        }
        if (!(ref.getTargetObj() instanceof BattleFieldObject)) {
            return true; // TODO if cell, apply damage to corpses?
        }
        getRef().setQuiet(checkDamageMod(DAMAGE_MODIFIER.QUIET));

        BattleFieldObject targetObj = (BattleFieldObject) ref.getTargetObj();
        int amount = formula.getAppendedByModifier(ref.getValue(KEYS.FORMULA))
                .getInt(ref);
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        boolean spell = active instanceof Spell;

        initDamageType();

        //TODO rpg Review - need an Attack to use Blockers!
        // new SpellAttack().var

        if (!checkDamageMod(DAMAGE_MODIFIER.UNBLOCKABLE)) {
            if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire()) {
                return false;
            }
            // amount = ShieldMaster.getShieldReducedAmountForDealDamageEffect(this, targetObj, amount, active);
        }

        LogMaster.log(LogMaster.COMBAT_DEBUG, "Effect is dealing damage: "
                + amount + " to " + ref.getTargetObj().toString());
        saveDamageModsToRef();

        ref.setValue(KEYS.DAMAGE_TYPE, damage_type.getName());

        int damage = DamageDealer.dealDamage(
                getDamageObject(amount)
//         damage_type, targetObj, ref, amount
        );

        // if (active.getIntParam(PARAMS.FORCE) == 0) // ONLY MAIN SPELL'S
        // DAMAGE


        return true;

    }

    private void saveDamageModsToRef() {
        if (damage_mods != null) {
            ref.setValue(KEYS.DAMAGE_MODS, ContainerUtils.constructStringContainer(Arrays.asList(damage_mods)));
        } else if (damage_mod != null) {
            ref.setValue(KEYS.DAMAGE_MODS, damage_mod.toString());
        }
    }

    public boolean checkDamageMod(DAMAGE_MODIFIER unblockable) {
        if (damage_mod == unblockable) {
            return true;
        }
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
        return DamageFactory.getDamageFromEffect(this, amount);
    }


}
