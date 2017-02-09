package main.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.*;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;

public class MoistRule extends DC_CounterRule {

    private static final String LIGHTNING_RES_PER_COUNTER = "(-2)";
    private static final String COLD_RES_PER_COUNTER = "(-1)";    // already
    // syng
    // with
    // Freeze
    // rule...
    private static final String FIRE_RES_PER_COUNTER = "(2)";
    private static final String WEIGHT_PER_COUNTER = "(0.5)";

    public MoistRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(DC_HeroObj unit) {
        if (unit.checkPassive(STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        return super.check(unit);
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.LIGHTNING_RESISTANCE,
                MOD.MODIFY_BY_CONST, getNumberOfCounters(unit) + "* "
                + LIGHTNING_RES_PER_COUNTER), new ModifyValueEffect(
                PARAMS.COLD_RESISTANCE, MOD.MODIFY_BY_CONST,
                getNumberOfCounters(unit) + "* " + COLD_RES_PER_COUNTER),
                new ModifyValueEffect(PARAMS.FIRE_RESISTANCE,
                        MOD.MODIFY_BY_CONST, getNumberOfCounters(unit)
                        + "* " + FIRE_RES_PER_COUNTER),
                new ModifyValueEffect(PARAMS.WEIGHT,
                        MOD.MODIFY_BY_CONST, getNumberOfCounters(unit)
                        + "* " + WEIGHT_PER_COUNTER));

    }

    @Override
    public int getMaxNumberOfCounters(DC_HeroObj unit) {
        if (unit.checkClassification(CLASSIFICATIONS.SMALL)) {
            return 25;
        }
        if (unit.checkClassification(CLASSIFICATIONS.HUGE)) {
            return 100;
        }
        return 50; // huge vs small?
        // skeletal less,
    }

    @Override
    protected boolean isUseBuffCache() {
        return false;
    }

    protected String getClashingCounter() {
        return STD_COUNTERS.Blaze_Counter.getName();
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Moist_Counter.getName();
    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
        if (unit.checkClassification(CLASSIFICATIONS.SMALL)) {
            return 1;
        }
        if (unit.checkClassification(CLASSIFICATIONS.HUGE)) {
            return 4;
        }
        return 2;
    }

    @Override
    public String getBuffName() {
        return STD_BUFF_NAMES.Soaked.name();
    }

    @Override
    public STATUS getStatus() {
        return STATUS.SOAKED;
    }

}
