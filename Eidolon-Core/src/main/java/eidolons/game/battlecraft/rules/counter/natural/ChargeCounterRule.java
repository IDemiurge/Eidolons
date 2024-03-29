package eidolons.game.battlecraft.rules.counter.natural;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
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
        return EffectEnums.COUNTER.Charge;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
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
