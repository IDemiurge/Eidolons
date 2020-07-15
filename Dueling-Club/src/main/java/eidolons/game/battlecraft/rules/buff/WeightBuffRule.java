package eidolons.game.battlecraft.rules.buff;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.content.ValuePages;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleEnums.COMBAT_RULES;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.content.VALUE;
import main.content.enums.system.MetaEnums;
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import java.util.Arrays;

/*
For each pound of weight over your max. carrying capacity, you suffer 1% penalty to all Move costs
If the weight reaches 150% of your capacity, penalty also applies to all other actions
With 200%+, unit is Immobilized.

 */
public class WeightBuffRule extends DC_BuffRule {
    // integral effect
    public static final String FORMULA = StringMaster.getValueRef(KEYS.SOURCE,
     PARAMS.C_CARRYING_WEIGHT)
     + "-" + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.CARRYING_CAPACITY);
    public static final String[] buffNames = {MetaEnums.STD_BUFF_NAME.Immobilized.getName(), // TODO
     MetaEnums.STD_BUFF_NAME.Overburdened.getName(), MetaEnums.STD_BUFF_NAME.Encumbered.getName(),};
    public static final String[] formulas = {getCarryingCapacity() + "*2",
     getCarryingCapacity() + "*3/2", getCarryingCapacity(),};
    private static final String PARAMETERS_MODIFIED_1 =
     ContainerUtils.constructStringContainer(Arrays.asList(ValuePages.PENALTIES_MOVE), Strings.VERTICAL_BAR);
    private static final String PARAMETERS_MODIFIED_2 =
     ContainerUtils.constructStringContainer(Arrays.asList(ValuePages.PENALTIES_MAIN), Strings.VERTICAL_BAR);


    public WeightBuffRule(GenericGame game) {
        super(game);
    }

    public static String getCarryingCapacity() {
        return StringMaster.getValueRef(KEYS.SOURCE, PARAMS.CARRYING_CAPACITY);
    }

    @Override
    protected boolean isConditionGreater(Integer level) {
        return true;
    }

    @Override
    protected Effect getEffect() {
        switch (level) {
            case 0: {
//               TODO  return new ImmobilizeEffect();
            }
            case 1: {
                return
                 new ModifyValueEffect(
                  PARAMETERS_MODIFIED_2,
                  MOD.MODIFY_BY_CONST, FORMULA);
            }
            case 2: {
                return
                 new ModifyValueEffect(
                  PARAMETERS_MODIFIED_1,
                  MOD.MODIFY_BY_CONST, FORMULA);
            }
        }
        return null;
    }

    @Override
    public void initConditions() {
        // TODO applies to non-living
    }

    @Override
    public Integer getMaxLevel() {
        return 2;
    }

    @Override
    protected VALUE getValue() {
        return PARAMS.C_CARRYING_WEIGHT;
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
        return RuleEnums.COMBAT_RULES.WEIGHT;
    }

}
