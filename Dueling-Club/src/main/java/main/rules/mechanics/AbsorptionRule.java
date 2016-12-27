package main.rules.mechanics;

import main.ability.effects.Effect;
import main.content.CONTENT_CONSTS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.rules.counter.DC_CounterRule;

/**
 * Created by JustMe on 12/27/2016.
 */
public class AbsorptionRule extends DC_CounterRule{



    public AbsorptionRule(DC_Game game) {
        super(game);
    }

    public void damageDealt(CONTENT_CONSTS.DAMAGE_TYPE type, int amount, Ref ref){
//        check
    }

    @Override
    public String getCounterName() {
        return CONTENT_CONSTS.STD_COUNTERS.Blaze_Counter.getName();
    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
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
    public CONTENT_CONSTS.STATUS getStatus() {
        return null;
    }
}
