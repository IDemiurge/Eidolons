package main.rules.counter;

import main.ability.effects.Effect;
import main.content.enums.entity.UnitEnums.STATUS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 4/22/2017.
 */
public class ChargeCounterRule extends DC_CounterRule {
    public ChargeCounterRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return null;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 0;
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
        return null;
    }
}
