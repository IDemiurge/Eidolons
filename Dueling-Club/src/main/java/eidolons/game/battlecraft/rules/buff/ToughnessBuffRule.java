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

public class ToughnessBuffRule extends DC_BuffRule {
    public static final String[] buffNames = {
     MetaEnums.STD_BUFF_NAME.Fatigued.getName(), MetaEnums.STD_BUFF_NAME.Energized.getName()};
    public static final String[] formulas = {  "20", "50",};
    //TODO into PERCENTAGE!!!

    public ToughnessBuffRule(GenericGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return getEffect(this.level);
    }

    protected Effect getEffect(int level) {
        switch (level) {
            case 1:
            case 0: {
                return new Effects(
                        new ModifyValueEffect(PARAMS.SPELLPOWER_MOD, MOD.MODIFY_BY_PERCENT,
                                getEffectFormula()),
                        new ModifyValueEffect(PARAMS.DAMAGE_MOD, MOD.MODIFY_BY_PERCENT,
                 getEffectFormula()));
            }// ++ Endurance regen?
        }

        return null;
    }

    @Override
    protected String getEffectFormula(Integer level) {
        switch (level) {
            case 0: {
                return "(" + StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[0]
                 + ")*5";
                //-5% ATB for each point of Stamina below 10
            }
            case 1: {
                // "5*sqrt(" +
                return StringMaster.getValueRef(KEYS.SOURCE, getValue()) + "-" + formulas[1];
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
        return 1;
    }

    @Override
    protected VALUE getValue() {
        return PARAMS.C_TOUGHNESS;
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
        return RuleEnums.COMBAT_RULES.TOUGHNESS;
    }

}
