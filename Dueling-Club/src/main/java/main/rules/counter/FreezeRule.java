package main.rules.counter;

import main.ability.conditions.StatusCheckCondition;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.container.ConditionalEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
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
    private static final String INITIATIVE_PER_COUNTER = "(-0.5)";
    private static final String ENDURANCE_REGEN_PER_COUNTER = "(-2.5)";
    private static final String BONUS_RESISTANCE_PER_COUNTER = "(1)";
    private static final String RESISTANCE_REDUCED_PER_COUNTER = "(-1)";
    private static final int FROZEN_PER_TURN_REDUCTION = 3;
    private static final String RESISTANCES_BOOSTED = PARAMS.FIRE_RESISTANCE.getName() + "|"
     + PARAMS.FIRE_RESISTANCE.getName() + "|"
     + PARAMS.LIGHT_RESISTANCE.getName() + "|"
     + PARAMS.POISON_RESISTANCE.getName() + "|"
     + PARAMS.PIERCING_RESISTANCE.getName();
    private static final String RESISTANCES_REDUCED = PARAMS.SONIC_RESISTANCE.getName() + "|"
     + PARAMS.SLASHING_RESISTANCE.getName()+"|"
     + PARAMS.BLUDGEONING_RESISTANCE.getName();

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

                new ModifyValueEffect(PARAMS.INITIATIVE_MODIFIER,
                        MOD.MODIFY_BY_CONST, getCounterRef() + "*"
                        + INITIATIVE_PER_COUNTER));
        // if (checkIsFrozen(unit)) {
        effects.add(new ConditionalEffect(new StatusCheckCondition(
                UnitEnums.STATUS.FROZEN),
new Effects( new ModifyValueEffect(RESISTANCES_REDUCED,
 MOD.MODIFY_BY_CONST, getCounterRef() + "*"
 + RESISTANCE_REDUCED_PER_COUNTER),
                new ModifyValueEffect(RESISTANCES_BOOSTED,
                        MOD.MODIFY_BY_CONST, getCounterRef() + "*"
                        + BONUS_RESISTANCE_PER_COUNTER))));
        // trigger effect - if dealt 33% toughness as Bludgeoning damage,
        // SHATTER! TODO
        // Also inflict 25% max endurance damage upon each FROZEN application!

        // }

        return effects;
    }



    @Override
    public String getCounterName() {
        return COUNTER.Freeze_Counter.getName();
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
                (unit.getCounter(COUNTER.Moist_Counter.getName())));
    }

}
