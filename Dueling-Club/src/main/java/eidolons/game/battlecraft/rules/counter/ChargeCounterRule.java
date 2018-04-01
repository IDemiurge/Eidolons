package eidolons.game.battlecraft.rules.counter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;

/**
 * Created by JustMe on 4/22/2017.
 */
public class ChargeCounterRule extends DC_CounterRule {
    //deals lightning damage to adjacent units?
    // always 'snap' counters upon lightning damage?
    public ChargeCounterRule(DC_Game game) {
        super(game);
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Charge;
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
