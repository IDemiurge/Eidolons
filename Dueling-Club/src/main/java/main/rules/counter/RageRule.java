package main.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ConditionalEffect;
import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.special.BehaviorModeEffect;
import main.content.enums.system.MetaEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.PARAMS;
import main.content.enums.system.AiEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.NumericCondition;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class RageRule extends DC_CounterRule {
    // can
    // calculate!
    public static final String COUNTER_NAME =
            // STD_COUNTERS.RAGE_COUNTER;
            "Rage Counter";
    // TODO ++ override morale killing rule!!! add Rage counters instead of
    // modifying morale!
    private static final String DAMAGE_PER_COUNTER = "5";
    private static final String INITIATIVE_PER_COUNTER = "5";
    private static final String SPELLPOWER_PER_COUNTER = "5";
    private static final String DEFENSE_PER_COUNTER = "-5";
    private static final String BERSERK_THRESHOLD = "5+{Willpower}";    // tooltip
    private static final Integer MAX_BERSERK = 40;
    private static final Integer MAX = 20;

    public RageRule(DC_Game game) {
        super(game);
    }

    @Override
    protected Effect getEffect() {
        return new Effects(

                new ModifyValueEffect(true, PARAMS.DAMAGE_MOD,
                        MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
                        + DAMAGE_PER_COUNTER), new ModifyValueEffect(true,
                PARAMS.INITIATIVE_MODIFIER, MOD.MODIFY_BY_PERCENT,
                getCounterRef() + "*" + INITIATIVE_PER_COUNTER),
                new ModifyValueEffect(true, PARAMS.SPELLPOWER,
                        MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
                        + SPELLPOWER_PER_COUNTER),
                new ModifyValueEffect(true, PARAMS.DEFENSE_MOD,
                        MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
                        + DEFENSE_PER_COUNTER), new ConditionalEffect(

                new NumericCondition(getCounterRef(), BERSERK_THRESHOLD),
                new Effects(new ModifyPropertyEffect(
                        G_PROPS.STANDARD_PASSIVES, MOD_PROP_TYPE.ADD,
                        UnitEnums.STANDARD_PASSIVES.BERSERKER + ""),
                        new BehaviorModeEffect(AiEnums.BEHAVIOR_MODE.BERSERK)))

        );
    }

    @Override
    public String getCounterName() {
        return COUNTER_NAME;
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAMES.Enraged.getName();
    }

    @Override
    public STATUS getStatus() {
        return null;
    }

    @Override
    public int getMaxNumberOfCounters(Unit unit) {
        return (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.BERSERKER)) ? MAX_BERSERK
                : MAX;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 1;
    }

}
