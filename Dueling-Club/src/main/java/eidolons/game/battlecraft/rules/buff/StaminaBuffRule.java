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
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.system.auxiliary.StringMaster;

public class StaminaBuffRule extends DC_BuffRule {
    public static final String[] buffNames = {MetaEnums.STD_BUFF_NAME.Exhausted.getName(),
     MetaEnums.STD_BUFF_NAME.Fatigued.getName(), MetaEnums.STD_BUFF_NAME.Energized.getName()};
    public static final String[] formulas = {"1", "10", "150",};

    public StaminaBuffRule(GenericGame game) {
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
            case 1:
            case 2: {
                return new ModifyValueEffect(PARAMS.INITIATIVE, MOD.MODIFY_BY_PERCENT,
                 getEffectFormula());
            }// ++ Endurance regen?
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
                //-5% ATB for each point of Stamina below 10
            }
            case 2: {
                // "5*sqrt(" +
                return StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[2];
                // + ")";
                //+1% ATB for each point of Stamina above 150
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
    protected COMBAT_RULES getCombatRuleEnum() {
        // TODO Auto-generated method stub
        return RuleEnums.COMBAT_RULES.STAMINA;
    }

}
