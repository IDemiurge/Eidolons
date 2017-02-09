package main.rules.buff;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.ModeEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.enums.STD_MODES;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.entity.Ref.KEYS;
import main.game.MicroGame;
import main.rules.mechanics.RuleMaster.COMBAT_RULES;
import main.system.auxiliary.StringMaster;

public class StaminaBuffRule extends DC_BuffRule {
    private String[] buffNames = {STD_BUFF_NAMES.Exhausted.getName(),
            STD_BUFF_NAMES.Fatigued.getName(), STD_BUFF_NAMES.Energized.getName()};
    private String[] formulas = {"1", "10", "100",};

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
                return new Effects(getEffect(1), new ModeEffect(STD_MODES.RESTING));
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
        return new NotCondition(new StdPassiveCondition(STANDARD_PASSIVES.RELENTLESS));

    }

    @Override
    protected boolean isReverse(Integer level) {
        if (level == getMaxLevel()) {
            return true;
        }
        return super.isReverse(level);
    }

    @Override
    public Integer getMaxLevel() {
        return 2;
    }

    @Override
    protected VALUE getValue() {
        return PARAMS.C_STAMINA;
    }

    @Override
    protected String[] getFormulas() {
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
