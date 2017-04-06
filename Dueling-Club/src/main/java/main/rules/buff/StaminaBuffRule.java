package main.rules.buff;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.MetaEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.entity.Ref.KEYS;
import main.game.core.game.MicroGame;
import main.rules.RuleMaster.COMBAT_RULES;
import main.system.auxiliary.StringMaster;

public class StaminaBuffRule extends DC_BuffRule {
    public static final  String[] buffNames = {MetaEnums.STD_BUFF_NAMES.Exhausted.getName(),
            MetaEnums.STD_BUFF_NAMES.Fatigued.getName(), MetaEnums.STD_BUFF_NAMES.Energized.getName()};
    public static final  String[] formulas = {"1", "10", "100",};

    public StaminaBuffRule(MicroGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return getEffect(this.level);
    }

    protected Effect getEffect(int level) {
        switch (level) {
            case 0: {
                return new Effects(getEffect(1)
//         TODO won't work via buff, need oneshot mechanic
//        , new ModeEffect(STD_MODES.RESTING)
                );
            }
            case 1: {
                return new ModifyValueEffect(PARAMS.DAMAGE_MOD, MOD.MODIFY_BY_PERCENT,
                        getEffectFormula());
            }// ++ Endurance regen?
            case 2: {
                return new ModifyValueEffect(PARAMS.DAMAGE_MOD, MOD.MODIFY_BY_PERCENT,
                        getEffectFormula());

            }
        }

        return null;
    }

    @Override
    protected String getEffectFormula(Integer level) {
        switch (level) {
            case 0:
            case 1: {
                return "(" + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[1]
                        + ")*5";
            }
            case 2: {
                // "5*sqrt(" +
                return StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[2];
                // + ")";
            }

        }
        return "";
    }

    protected Condition getBuffConditions() {
        return new NotCondition(new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.RELENTLESS));

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
        return PARAMS.C_STAMINA;
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
        // TODO Auto-generated method stub
        return COMBAT_RULES.STAMINA;
    }

}
