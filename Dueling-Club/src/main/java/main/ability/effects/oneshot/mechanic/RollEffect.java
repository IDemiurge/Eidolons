package main.ability.effects.oneshot.mechanic;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.OneshotEffect;
import main.ability.effects.ContainerEffect;
import main.ability.effects.MicroEffect;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.data.ability.construct.VariableManager;
import main.game.ai.tools.target.EffectFinder;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.roll.RollMaster;
import main.system.math.roll.Rolls;

// auto-wrap in this? 
/*
 * via special prop ROLL_TYPES 
 * 
 * 
 * 
 */
public class RollEffect extends MicroEffect  implements OneshotEffect, ContainerEffect {
    private Effect effect;
    private String success;
    private String fail;
    private String abilityName;
    private ROLL_TYPES rollType;
    private String rollString;
    private Rolls rolls;
    private String elseAbilityName;
    private Effects elseEffect;

    public RollEffect(ROLL_TYPES rollType, String success, String fail,
                      String abilityName, String elseAbilityName) {
        this.success = success;
        this.fail = fail;
        this.rollType = rollType;
        this.abilityName = abilityName;
        this.elseAbilityName = elseAbilityName;
    }

    public RollEffect(ROLL_TYPES rollType, String success, String fail,
                      String abilityName) {
        this(rollType, success, fail, abilityName, null);
    }

    public RollEffect(String rollString, String success, String fail,
                      String abilityName, String elseAbilityName) {
        this.rollString = rollString;
        this.success = success;
        this.fail = fail;
        this.abilityName = abilityName;
        this.elseAbilityName = elseAbilityName;
    }

    public RollEffect(String rollString, String success, String fail,
                      String abilityName) {
        this(rollString, success, fail, abilityName, null);
    }

    public RollEffect(String rollString, String abilityName,
                      String elseAbilityName) {
        this(rollString, "", "", abilityName, null);
    }

    public RollEffect(String rollString, String abilityName) {
        this(rollString, abilityName, null);
    }

    public RollEffect(String rollString, Effect e) {
        this.rollString = rollString;
        this.effect = e;
    }

    public RollEffect(ROLL_TYPES rollType, String success, Effect e, String fail) {
        this.effect = e;
        this.success = success;
        this.fail = fail;
        this.rollType = rollType;
    }

    public RollEffect(ROLL_TYPES t, Effect effect) {
        this.effect = effect;
        this.rollType = t;
    }

    private void initRollString() {
        rolls = RollMaster.generateRollsFromString(rollString);
    }

    private boolean roll() {
        if (rolls == null) {
            return false;
        }
        return rolls.roll(ref);
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

    public boolean applyThis() {
        boolean result;
        if (rollType == null) {
            if (success == null && fail == null) {
                initRollString();
                result = roll();
            } else {
                // init roll
                if (getRollType() == null) {
                    return false;
                }

                return applyThis(); // TODO luck?
            }

        } else {
            if (!RollMaster.checkRollType(rollType, ref)) {
                return false;
            }

            // ++ event
            result =
                    // new ChanceCondition(new Formula(successFormula),
                    // new Formula(failFormula)).preCheck(ref);
                    RollMaster.roll(rollType, getSuccess(), getFail(), ref);

            // roll method instead, with proper logging!
        }
        if (game.isDebugMode() || result) {
            return getEffect().apply(ref);
        } else {
            if (getElseEffect() != null) {
                return getElseEffect().apply(ref);
            }
        }
        return false;
    }

    public Effect getElseEffect() {
        if (elseEffect != null) {
            return elseEffect;
        }
        if (elseAbilityName != null) {
            elseEffect = new Effects();
        }
        for (String s : StringMaster.openContainer(elseAbilityName,
                StringMaster.AND_SEPARATOR)) {
            ((Effects) effect).addAll(EffectFinder
                    .getEffectsFromAbilityType(VariableManager.getVarType(s,
                            false, ref)));
        }

        return elseEffect;

    }

    @Override
    public Effect getEffect() {
        if (effect == null) {
            effect = new Effects();
            for (String s : StringMaster.openContainer(abilityName,
                    StringMaster.AND_SEPARATOR)) {
                ((Effects) effect).addAll(EffectFinder
                        .getEffectsFromAbilityType(VariableManager.getVarType(
                                s, false, ref)));
            }
        }
        return effect;
    }

    public String getSuccess() {
        if (StringMaster.isEmpty(success)) {
            success = RollMaster.getStdFormula(rollType, true);
        }
        return success;
    }

    public String getFail() {

        if (StringMaster.isEmpty(fail)) {
            fail = RollMaster.getStdFormula(rollType, false);
        }
        return fail;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public ROLL_TYPES getRollType() {
        if (rollType == null) {
            rollType = new EnumMaster<ROLL_TYPES>().retrieveEnumConst(
                    ROLL_TYPES.class, rollString);
        }
        return rollType;
    }

    public String getRollString() {
        return rollString;
    }

    public Rolls getRolls() {
        if (rolls == null) {
            initRollString();
        }
        return rolls;
    }

    public String getElseAbilityName() {
        return elseAbilityName;
    }

}
