package eidolons.game.battlecraft.rules.counter.negative;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;

public class MoistRule extends DC_CounterRule {

    private static final String LIGHTNING_RES_PER_COUNTER = "(-2)";
    private static final String COLD_RES_PER_COUNTER = "(-1)";
    private static final String FIRE_RES_PER_COUNTER = "(2)";
    private static final String WEIGHT_PER_COUNTER = "(0.5)";

    public MoistRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(BattleFieldObject unit) {
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        return super.check(unit);
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.LIGHTNING_RESISTANCE,
         MOD.MODIFY_BY_CONST, getNumberOfCounters(object) + "* "
         + LIGHTNING_RES_PER_COUNTER), new ModifyValueEffect(
         PARAMS.COLD_RESISTANCE, MOD.MODIFY_BY_CONST,
         getNumberOfCounters(object) + "* " + COLD_RES_PER_COUNTER),
         new ModifyValueEffect(PARAMS.FIRE_RESISTANCE,
          MOD.MODIFY_BY_CONST, getNumberOfCounters(object)
          + "* " + FIRE_RES_PER_COUNTER),
         new ModifyValueEffect(PARAMS.WEIGHT,
          MOD.MODIFY_BY_CONST, getNumberOfCounters(object)
          + "* " + WEIGHT_PER_COUNTER));

    }

    @Override
    public int getMaxNumberOfCounters(BattleFieldObject unit) {
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.SMALL)) {
            return 25;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.HUGE)) {
            return 100;
        }
        return 50; // huge vs small?
        // skeletal less,
    }

    @Override
    protected boolean isUseBuffCache() {
        return false;
    }


    @Override
    public COUNTER getCounter() {
        return EffectEnums.COUNTER.Moist;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.SMALL)) {
            return 1;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.HUGE)) {
            return 4;
        }
        return 2;
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAME.Soaked.name();
    }

    @Override
    public STATUS getStatus() {
        return UnitEnums.STATUS.SOAKED;
    }

}
