package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 4/22/2017.
 */
public class SuffocationRule extends DamageCounterRule {
    private static final Integer DISCOMBOBULATED_THRESHOLD = 25;
    private static final String PERCENT_LOST_PER_ROUND = "0.5";

    public SuffocationRule(DC_Game game) {
        super(game);
    }


    @Override
    public DAMAGE_TYPE getDamageType() {
        return DAMAGE_TYPE.PURE;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return "{TARGET_ENDURANCE}/100*" + PERCENT_LOST_PER_ROUND;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Suffocation;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 3;
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getEffect() {
        return null;
    }

    @Override
    public STATUS getStatus() {
        if (getNumberOfCounters(unit) > DISCOMBOBULATED_THRESHOLD)
            return STATUS.DISCOMBOBULATED;
        return null;
    }
}
