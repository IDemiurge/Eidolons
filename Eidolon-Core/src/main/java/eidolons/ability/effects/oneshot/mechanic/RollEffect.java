package eidolons.ability.effects.oneshot.mechanic;

import eidolons.game.core.master.EffectMaster;
import eidolons.system.math.roll.Roll;
import eidolons.system.math.roll.RollMaster;
import main.ability.StringsContainer;
import main.ability.effects.*;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.RollType;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import static main.content.enums.GenericEnums.DieType.d10;

// auto-wrap in this? 
/*
 * via special prop ROLL_TYPES
 *
 *
 *
 */
public class RollEffect extends MicroEffect implements OneshotEffect, ContainerEffect {
    private Effect effect;
    private String abilityName;
    private Effect elseEffect;
    private String elseAbilityName;

    private String sValue;
    private String tValue;
    private RollType rollType;

    private String sDice;
    private String tDice;
    private GenericEnums.DieType dieType;
    private boolean ignoresChaosRule;

    private String rollString;
    private String constValue;
    private Roll roll;

    public RollEffect(RollType type, Effect fx) {
        this(type, fx, null);
    }

    public RollEffect(RollType rollType, Effect effect,
                      StringsContainer container) {
        this(rollType, d10, effect, null, container);
    }

    public RollEffect(RollType rollType, GenericEnums.DieType dieType, Effect effect,
                      StringsContainer container) {
        this(rollType, dieType, effect, null, container);
    }

    public RollEffect(RollType rollType, GenericEnums.DieType dieType, Effect effect, Effect elseEffect,
                      StringsContainer container) {
        this.rollType = rollType;
        this.dieType = dieType;
        this.effect = effect;
        this.elseEffect = elseEffect;
        if (container != null) {
            container.unpack(this, RollEffect.class);
        }
    }


    public RollEffect(RollType rollType, String sourceValue, String targetValue,
                      String abilityName, String elseAbilityName) {
        this.sValue = sourceValue;
        this.tValue = targetValue;
        this.rollType = rollType;
        this.abilityName = abilityName;
        this.elseAbilityName = elseAbilityName;
    }


    public boolean applyThis() {
        boolean result = false;

        if (rollType != null) {
            roll = new Roll(rollType, dieType, sValue, tValue, sDice, tDice);
            result = roll.roll(getRef());
        } else {
            // if (!RollMaster.checkRollAutoResolves(rollType, ref)) {
            //     return true;
            // }
            // result = RollMaster.roll(rollType, getSourceValue(), getTargetValue(), ref);
        }

        if (game.isDebugMode() || result) {
            return getEffect().apply(ref);
        } else {
            if (getElseEffect() != null) {
                return getElseEffect().apply(ref);
            }
        }
        return true;
    }

    public Effect getElseEffect() {
        if (elseEffect != null) {
            return elseEffect;
        }
        if (elseAbilityName != null) {
            elseEffect = new Effects();
        }
        for (String s : ContainerUtils.open(elseAbilityName,
                Strings.VERTICAL_BAR)) {
            ((Effects) effect).addAll(EffectMaster
                    .getEffectsFromAbilityType(VariableManager.getVarType(s,
                            false, ref)));
        }

        return elseEffect;

    }

    @Override
    public Effect getEffect() {
        if (effect == null) {
            effect = new Effects();
            for (String s : ContainerUtils.open(abilityName,
                    Strings.VERTICAL_BAR)) {
                ((Effects) effect).addAll(EffectMaster
                        .getEffectsFromAbilityType(VariableManager.getVarType(
                                s, false, ref)));
            }
        }
        return effect;
    }

    public String getSourceValue() {
        if (StringMaster.isEmpty(sValue)) {
            sValue = RollMaster.getStdFormula(rollType, true);
        }
        return sValue;
    }

    public String getTargetValue() {

        if (StringMaster.isEmpty(tValue)) {
            tValue = RollMaster.getStdFormula(rollType, false);
        }
        return tValue;
    }

    @Override
    public String toString() {
        String string = "RollEffect:";
        if (rollString != null) {
            string = rollString;
        } else if (rollType != null) {
            string += rollType.getName();
        }
        if (effect != null) {
            string += StringMaster.wrapInParenthesis(effect.toString());
        } else if (abilityName != null) {
            string += StringMaster.wrapInParenthesis(abilityName);
        }
        return string;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public RollType getRollType() {
        if (rollType == null) {
            rollType = new EnumMaster<RollType>().retrieveEnumConst(
                    RollType.class, rollString);
        }
        return rollType;
    }

    public String getRollString() {
        return rollString;
    }

    public String getElseAbilityName() {
        return elseAbilityName;
    }

}
