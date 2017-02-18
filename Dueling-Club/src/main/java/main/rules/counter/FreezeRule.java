package main.rules.counter;

import main.ability.conditions.StatusCheckCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ConditionalEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.enums.system.MetaEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;

/*
 Water damage per Moist counter? Or just multiply? Stamina reduction? Costs penalties? 
 Reduce initiative, regeneration, 
 Increase some resistances? 
 *Frozen* status - thats when we could add the resistances :)
 * 
 */
public class FreezeRule extends DC_CounterRule {

    private static final int COUNTERS_PER_TURN = 4;
    private static final String INITIATIVE_PER_COUNTER = "(-1)";
    private static final String ENDURANCE_REGEN_PER_COUNTER = "(-5)";
    private static final String POISON_RESISTANCE_PER_COUNTER = "(2)";
    private static final String PHYS_RESISTANCE_PER_COUNTER = "(1)";
    private static final int FROZEN_PER_TURN_REDUCTION = 3;

    public FreezeRule(DC_Game game) {
        super(game);
    }

    @Override
    protected boolean isUseBuffCache() {
        return false;
    }

    @Override
    protected Effect getEffect() {
        Effects effects = new Effects(new ModifyValueEffect(
                PARAMS.ENDURANCE_REGEN, MOD.MODIFY_BY_PERCENT,
                getCounterRef() + "*" + ENDURANCE_REGEN_PER_COUNTER),

                new ModifyValueEffect(PARAMS.POISON_RESISTANCE,
                        MOD.MODIFY_BY_CONST, getCounterRef() + "*"
                        + POISON_RESISTANCE_PER_COUNTER),

                new ModifyValueEffect(PARAMS.INITIATIVE_MODIFIER,
                        MOD.MODIFY_BY_CONST, getCounterRef() + "*"
                        + INITIATIVE_PER_COUNTER));
        // if (checkIsFrozen(unit)) {
        effects.add(new ConditionalEffect(new StatusCheckCondition(
                UnitEnums.STATUS.FROZEN),

                new ModifyValueEffect(PARAMS.SLASHING_RESISTANCE.getName() + "|"
                        + PARAMS.PIERCING_RESISTANCE.getName(),
                        MOD.MODIFY_BY_CONST, getCounterRef() + "*"
                        + PHYS_RESISTANCE_PER_COUNTER)));
        // trigger effect - if dealt 33% toughness as Bludgeoning damage,
        // SHATTER! TODO
        // Also inflict 25% max endurance damage upon each FROZEN application!

        // }

        return effects;
    }

    @Override
    protected String getClashingCounter() {
        return UnitEnums.STD_COUNTERS.Blaze_Counter.getName();
    }

    @Override
    public String getCounterName() {
        return UnitEnums.STD_COUNTERS.Freeze_Counter.getName();
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAMES.Frost.getName();
    }

    @Override
    public void initEffects() {
        super.initEffects();

    }

    @Override
    public STATUS getStatus() {
        return !checkIsFrozen(unit) ? UnitEnums.STATUS.FREEZING : UnitEnums.STATUS.FROZEN;
    }

    private boolean checkIsFrozen(Unit unit) {
        return unit.getIntParam(PARAMS.INITIATIVE_MODIFIER) <= StringMaster
                .getInteger(INITIATIVE_PER_COUNTER)
                * -getNumberOfCounters(unit);
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        if (checkIsFrozen(unit)) {
            return FROZEN_PER_TURN_REDUCTION;
        }
        return COUNTERS_PER_TURN
                - Math.min(getNumberOfCounters(unit),
                (unit.getCounter(UnitEnums.STD_COUNTERS.Moist_Counter.getName())));
    }

}
