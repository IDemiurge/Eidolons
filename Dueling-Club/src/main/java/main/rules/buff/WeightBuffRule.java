package main.rules.buff;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.special.ImmobilizeEffect;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.enums.system.MetaEnums;
import main.entity.Ref.KEYS;
import main.game.core.game.MicroGame;
import main.rules.RuleMaster.COMBAT_RULES;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

public class WeightBuffRule extends DC_BuffRule {
    private static final String penalty_factor = "0.02";
    private static final String FORMULA = StringMaster.getValueRef(KEYS.SOURCE,
            PARAMS.C_CARRYING_WEIGHT)
            + "-" + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.CARRYING_CAPACITY);
    private String[] buffNames = {MetaEnums.STD_BUFF_NAMES.Immobilized.getName(), // TODO
            // there
            // must
            // be
            // "drop item"
            // action!
            // :)
            MetaEnums.STD_BUFF_NAMES.Overburdened.getName(), MetaEnums.STD_BUFF_NAMES.Encumbered.getName(),};
    private String[] formulas = {getCarryingCapacity() + "*2",

            getCarryingCapacity() + "*3/2", getCarryingCapacity(),};

    // reverse means MORE THAN {THIS} and
    public WeightBuffRule(MicroGame game) {
        super(game);
    }

    public static String getCarryingCapacity() {
        return StringMaster.getValueRef(KEYS.SOURCE, PARAMS.CARRYING_CAPACITY);
    }

    @Override
    protected Effect getEffect() {
        switch (level) {
            case 0: {
                return new ImmobilizeEffect();
            }
            case 1: {
                return new Effects(new ModifyValueEffect(PARAMS.FOCUS_PENALTY,
                        MOD.MODIFY_BY_CONST, new Formula(FORMULA)
                        .getAppendedByFactor(penalty_factor)
                        + ""),

                        new ModifyValueEffect(PARAMS.ESSENCE_PENALTY, MOD.MODIFY_BY_CONST,
                                new Formula(FORMULA).getAppendedByFactor(penalty_factor) + ""),

                        new ModifyValueEffect(PARAMS.STAMINA_PENALTY, MOD.MODIFY_BY_CONST,
                                new Formula(FORMULA).getAppendedByFactor(penalty_factor) + ""),
                        new ModifyValueEffect(PARAMS.AP_PENALTY, MOD.MODIFY_BY_CONST,
                                new Formula(FORMULA).getAppendedByFactor(penalty_factor) + ""));
            }
            case 2: {
                return new Effects(new ModifyValueEffect(PARAMS.FOCUS_PENALTY,
                        MOD.MODIFY_BY_CONST, FORMULA), new ModifyValueEffect(
                        PARAMS.ESSENCE_PENALTY, MOD.MODIFY_BY_CONST, FORMULA),

                        new ModifyValueEffect(PARAMS.STAMINA_PENALTY, MOD.MODIFY_BY_CONST,
                                FORMULA), new ModifyValueEffect(PARAMS.AP_PENALTY,
                        MOD.MODIFY_BY_CONST, FORMULA));
            }
        }
        return null;
    }

    @Override
    public void initConditions() {
        // TODO applies to non-living
    }

    @Override
    protected boolean isReverse(Integer level) {
        return true;
    }

    @Override
    public Integer getMaxLevel() {
        return 2;
    }

    @Override
    protected VALUE getValue() {
        return PARAMS.C_CARRYING_WEIGHT;
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
        return COMBAT_RULES.WEIGHT;
    }

}
