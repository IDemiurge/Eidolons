package main.game.battlecraft.rules.buff;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.container.ConditionalEffect;
import main.ability.effects.continuous.BehaviorModeEffect;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.MetaEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.entity.Ref.KEYS;
import main.game.battlecraft.rules.RuleMaster.COMBAT_RULES;
import main.game.core.game.MicroGame;
import main.system.auxiliary.StringMaster;

public class MoraleBuffRule extends DC_BuffRule {
    public static final  String[] buffNames = {MetaEnums.STD_BUFF_NAMES.Panic.getName(),
            // STD_TYPE_NAMES.Terrified.getName(), // panic -> auto-run away,
            // disable
            // counters
            MetaEnums.STD_BUFF_NAMES.Fearful.getName(), MetaEnums.STD_BUFF_NAMES.Inspired.getName()};
    public static final  String[] formulas = {"1", "10", "100",}; // make
    // fearful
    // and
    // treason
    // stack?
    public static final  String parameterString = PARAMS.SPIRIT.getName() + StringMaster.AND_SEPARATOR
            + PARAMS.INITIATIVE_MODIFIER.getName() + StringMaster.AND_SEPARATOR
            + PARAMS.RESISTANCE.getName();
    public static final  String parameterStringPanic = PARAMS.SPIRIT.getName() + StringMaster.AND_SEPARATOR
            + PARAMS.RESISTANCE.getName();

    // reverse means MORE THAN {THIS} and
    public MoraleBuffRule(MicroGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return getEffect(this.level);
    }

    protected Effect getEffect(int level) {
        switch (level) {
            case 0: {
                return new ConditionalEffect(new NotCondition(new StdPassiveCondition(
                        UnitEnums.STANDARD_PASSIVES.FEARLESS)),

                        new Effects(new ModifyValueEffect(parameterStringPanic,
                                MOD.MODIFY_BY_PERCENT, "("
                                + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-"
                                + formulas[1] + ")*2"), new BehaviorModeEffect(
                                AiEnums.BEHAVIOR_MODE.PANIC)));
                // return new OwnershipChangeEffect();
            }
            case 1: {
                return new ConditionalEffect(new NotCondition(new StdPassiveCondition(
                        UnitEnums.STANDARD_PASSIVES.FEARLESS)), new Effects(new ModifyValueEffect(
                        parameterString, MOD.MODIFY_BY_PERCENT, "("
                        + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-"
                        + formulas[1] + ")*2")));
            }
            case 2: {
                return new Effects(new ModifyValueEffect(parameterString,
                        MOD.MODIFY_BY_PERCENT, "("
                        + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-"
                        + formulas[2] + ")/4"));

            }
        }
        return null;
    }

    protected Condition getBuffConditions() {
        return new NotCondition(new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.DISPASSIONATE));

    }

    @Override
    protected boolean isConditionGreater(Integer level) {
        if (level == getMaxLevel()) {
            return true;
        }
        return super.isConditionGreater(level);
    }

    @Override
    public Integer getMaxLevel() {
        return 2;
    }

    @Override
    protected VALUE getValue() {
        return PARAMS.C_MORALE;
    }

    protected String[] getConditionFormulas() {
        return formulas;
    }

    @Override
    protected String[] getBuffNames() {

        return buffNames;
    }

    @Override
    protected COMBAT_RULES getCombatRuleEnum() {
        return COMBAT_RULES.MORALE;
    }

}
