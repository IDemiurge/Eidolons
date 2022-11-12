package eidolons.game.battlecraft.rules.counter.negative;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.battlecraft.rules.counter.generic.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;

/**
 * Created by JustMe on 4/22/2017.
 */
public class SuffocationRule extends DC_CounterRule implements TimedRule{
    private static final Integer DISCOMBOBULATED_THRESHOLD = 25;
    private static final String PERCENT_LOST_PER_ROUND = "0.5";

    public SuffocationRule(DC_Game game) {
        super(game);
    }

    @Override
    public COUNTER getCounter() {
        return EffectEnums.COUNTER.Suffocation;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
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
        if (getNumberOfCounters(object) > DISCOMBOBULATED_THRESHOLD)
            return STATUS.DISCOMBOBULATED;
        return null;
    }
}
