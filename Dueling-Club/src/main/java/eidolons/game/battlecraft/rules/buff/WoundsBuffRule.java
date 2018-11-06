package eidolons.game.battlecraft.rules.buff;

import eidolons.ability.conditions.shortcut.StdPassiveCondition;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.continuous.BehaviorModeEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.RuleKeeper.COMBAT_RULES;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.container.IfElseEffect;
import main.content.VALUE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.MetaEnums;
import main.elements.conditions.NotCondition;
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.system.auxiliary.StringMaster;

/**
 * Enraged will be exception ++ bleeding!!!
 *
 * @author JustMe
 */
public class WoundsBuffRule extends DC_BuffRule {
    public static final String BERSERK_WOUNDS_DAMAGE_MOD = "50";

    public static final String[] formulas = {
     "{SOURCE_ENDURANCE}/10", "{SOURCE_ENDURANCE}/5"};

    public static final String[] buffTypeNames = {MetaEnums.STD_BUFF_NAMES.Critically_Wounded.getName(),
     MetaEnums.STD_BUFF_NAMES.Wounded.getName(),};

    public WoundsBuffRule(GenericGame game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        switch (level) {
            case 0: {
                ModifyValueEffect modifyValueEffect = new ModifyValueEffect(PARAMS.DAMAGE_MOD,
                 MOD.MODIFY_BY_PERCENT, getCriticalApPenalty());
                return new IfElseEffect(modifyValueEffect, new NotCondition(
                 new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.BERSERKER)), new Effects(
                 new ModifyValueEffect(PARAMS.DAMAGE_MOD, MOD.MODIFY_BY_PERCENT,
                  BERSERK_WOUNDS_DAMAGE_MOD), new BehaviorModeEffect(
                 AiEnums.BEHAVIOR_MODE.BERSERK)));

            }
            case 1: {
                ModifyValueEffect modifyValueEffect = new ModifyValueEffect(PARAMS.DAMAGE_MOD,
                 MOD.MODIFY_BY_PERCENT, getApPenalty());
                ModifyValueEffect modifyValueEffect2 = new ModifyValueEffect(PARAMS.DAMAGE_MOD,
                 MOD.MODIFY_BY_PERCENT, BERSERK_WOUNDS_DAMAGE_MOD);
                return new IfElseEffect(modifyValueEffect, new NotCondition(
                 new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.BERSERKER)), modifyValueEffect2);
            }
        }

        return null;
    }

    protected String getApPenalty() {
        // return "min(-15, -33+" + StringMaster.getValueRef(KEYS.SOURCE,
        // PARAMS.FORTITUDE) + " )";
        return "-33+(33*" + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.WOUNDS_RESISTANCE)
         + ")/100";
    }

    protected String getCriticalApPenalty() {
        return "-66+(66*" + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.WOUNDS_RESISTANCE)
         + ")/100";
        // return "min(-25, -66+2*" + StringMaster.getValueRef(KEYS.SOURCE,
        // PARAMS.FORTITUDE) + " )";
    }

    @Override
    protected COMBAT_RULES getCombatRuleEnum() {
        return COMBAT_RULES.WOUNDS;
    }

    @Override
    public Integer getMaxLevel() {
        return 1;
    }


    @Override
    protected VALUE getValue() {
        return PARAMS.C_ENDURANCE;
    }

    protected String[] getConditionFormulas() {
        return formulas;
    }

    @Override
    protected String[] getBuffNames() {
        return buffTypeNames;
    }

}
