package main.rules.buff;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.special.BehaviorModeEffect;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.enums.system.AiEnums;
import main.content.enums.system.MetaEnums;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.entity.Ref.KEYS;
import main.game.core.game.MicroGame;
import main.rules.RuleMaster.COMBAT_RULES;
import main.system.auxiliary.StringMaster;

public class FocusBuffRule extends DC_BuffRule {
    private String[] buffNames = {MetaEnums.STD_BUFF_NAMES.Discombobulated.getName(),
            MetaEnums.STD_BUFF_NAMES.Dizzy.getName(), MetaEnums.STD_BUFF_NAMES.Razorsharp.getName()};
    private String[] formulas = {"1", "10", "50",};

    // reverse means MORE THAN {THIS} and
    public FocusBuffRule(MicroGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return getEffect(this.level);
    }

    protected Effect getEffect(int level) {
        switch (level) {
            case 0: {
                return new Effects(getEffect(1), new BehaviorModeEffect(AiEnums.BEHAVIOR_MODE.CONFUSED));
                // return new ModeEffect(STD_MODES.CONCENTRATION); // BEHAVIOR -
                // CONFUSED
            }
            case 1: {
                return new Effects(new ModifyValueEffect(true, PARAMS.DEFENSE_MOD,
                        MOD.MODIFY_BY_PERCENT, "("
                        + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-"
                        + formulas[1] + ")*5"),

                        new ModifyValueEffect(true, PARAMS.ATTACK_MOD, MOD.MODIFY_BY_PERCENT, "("
                                + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[1]
                                + ")*5"));
            }
            case 2: {
                return new Effects(new ModifyValueEffect(true, PARAMS.DEFENSE_MOD,
                        MOD.MODIFY_BY_PERCENT, "("
                        + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-"
                        + formulas[2] + ")"),

                        new ModifyValueEffect(true, PARAMS.ATTACK_MOD, MOD.MODIFY_BY_PERCENT, "("
                                + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[2]
                                + ")"));
            }
        }

        return null;
    }

    protected Condition getBuffConditions() {
        return new NotCondition(new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.ZOMBIFIED));

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
        return PARAMS.C_FOCUS;
    }

    protected String[] getConditionFormulas() {
        return formulas;
    }

    @Override
    protected String[] getBuffNames() {

        return buffNames;
    }

    @Override
    protected COMBAT_RULES getRuleEnum() {
        return COMBAT_RULES.FOCUS;
    }

}
