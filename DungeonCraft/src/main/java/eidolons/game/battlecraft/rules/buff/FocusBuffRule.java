package eidolons.game.battlecraft.rules.buff;

import eidolons.ability.conditions.shortcut.StdPassiveCondition;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleEnums.COMBAT_RULES;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.VALUE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.MetaEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.game.core.game.GenericGame;

public class FocusBuffRule extends DC_BuffRule {
    public static final String[] buffNames = {
            MetaEnums.STD_BUFF_NAME.Dizzy.getName(), MetaEnums.STD_BUFF_NAME.Razorsharp.getName()};
    public static final String[] formulas = {  "10", "50",};

    // reverse means MORE THAN {THIS} and
    public FocusBuffRule(GenericGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return getEffect(this.level);
    }

    protected Effect getEffect(int level) {
        switch (level) {
            case 0: {
                return new Effects(new ModifyValueEffect(true, PARAMS.DEFENSE_MOD,
                        MOD.MODIFY_BY_PERCENT, "("
                        + getValueRef() + "-"
                        + formulas[level] + ")*10"),

                        new ModifyValueEffect(true, PARAMS.ATTACK_MOD, MOD.MODIFY_BY_PERCENT, "("
                                + getValueRef() + "-" + formulas[level]
                                + ")*10"));
            }
            case 1: {
                return new Effects(new ModifyValueEffect(true, PARAMS.DEFENSE_MOD,
                        MOD.MODIFY_BY_PERCENT, "("
                        + getValueRef() + "-"
                        + formulas[level] + ")* 2"),

//                        new ModifyValueEffect(true, PARAMS.ACCURACY, MOD.MODIFY_BY_CONST,
//                                "(" + getValueRef() + " - " +formulas[2] +")* 2"),
//
//                        new ModifyValueEffect(true, PARAMS.EVASION, MOD.MODIFY_BY_CONST,
//                                "(" + getValueRef() + " - " +formulas[2]+")* 2"),

                        new ModifyValueEffect(true, PARAMS.ATTACK_MOD, MOD.MODIFY_BY_PERCENT, "("
                                + getValueRef() + "-" + formulas[level]
                                + ")* 2" ));
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
        return 1;
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
    protected COMBAT_RULES getCombatRuleEnum() {
        return RuleEnums.COMBAT_RULES.FOCUS;
    }

}
