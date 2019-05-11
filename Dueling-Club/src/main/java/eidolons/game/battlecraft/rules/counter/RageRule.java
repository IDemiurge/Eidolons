package eidolons.game.battlecraft.rules.counter;

import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.continuous.BehaviorModeEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.Effects;
import main.ability.effects.common.AddStatusEffect;
import main.ability.effects.container.ConditionalEffect;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.AiEnums;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.NumericCondition;

public class RageRule extends DC_CounterRule {
    // can
    // calculate!
    // TODO ++ override morale killing rule!!! add Rage counters instead of
    // modifying morale!
    private static final String DAMAGE_PER_COUNTER = "5";
    private static final String INITIATIVE_PER_COUNTER = "5";
    private static final String SPELLPOWER_PER_COUNTER = "5";
    private static final String DEFENSE_PER_COUNTER = "-5"; //focus  fatigue?
    private static final String WOUNDS_THRESHOLD = "12";    // tooltip
    private static final String STAMINA_THRESHOLD = "20";    // tooltip
    private static final String BERSERK_THRESHOLD = "10+2*{Willpower}";    // tooltip
    private static final Integer MAX_BERSERK = 40;
    private static final Integer MAX = 20;

    public RageRule(DC_Game game) {
        super(game);
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Rage;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        if (unit instanceof Unit) {
            if (((Unit) unit).getBehaviorMode()== AiEnums.BEHAVIOR_MODE.BERSERK) {
            return unit.getIntParam(PARAMS.WILLPOWER)/2+ 1 + getNumberOfCounters(unit) / 5;
            }
                return 1+getNumberOfCounters(unit) / 10;
        }
        return 0;
    }
    @Override
    protected Effect getEffect() {
        return new Effects(
                new Effect[]{
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
                                + DEFENSE_PER_COUNTER),

                        new ConditionalEffect(
                                new NumericCondition(getCounterRef(), WOUNDS_THRESHOLD),
                                new ModifyPropertyEffect(
                                        G_PROPS.STANDARD_PASSIVES, MOD_PROP_TYPE.ADD,
                                        UnitEnums.STANDARD_PASSIVES.BERSERKER + "")),

                        new ConditionalEffect(new NumericCondition(getCounterRef(), STAMINA_THRESHOLD),
                                new ModifyPropertyEffect(G_PROPS.STANDARD_PASSIVES, MOD_PROP_TYPE.ADD,
                                        UnitEnums.STANDARD_PASSIVES.RELENTLESS + "")),


                        new ConditionalEffect(
                                new NumericCondition(getCounterRef(), BERSERK_THRESHOLD),
                                new Effects(new AddStatusEffect(STATUS.UNDYING),
                                        new BehaviorModeEffect(AiEnums.BEHAVIOR_MODE.BERSERK)))
                });
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
    public int getMaxNumberOfCounters(BattleFieldObject unit) {
        return (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.BERSERKER)) ? MAX_BERSERK
                : MAX;
    }


}
