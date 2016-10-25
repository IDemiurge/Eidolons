package main.rules.counter;

import main.ability.effects.Effect;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;

public class LustRule extends DC_CounterRule {

    public LustRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Lust_Counter.getName();
    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
        // TODO Auto-generated method stub
        return unit.getIntParam(PARAMS.SPIRIT);
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        // TODO Auto-generated method stub
        return super.getSpecialRoundEffects();
    }

    @Override
    protected Effect getEffect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public STATUS getStatus() {
        // checkCharmed()
        return null;
    }
    /*
	 * reduce focus change ownership
	 * 
	 * positive effect on Warp Demons - let them regenerate focus/morale for
	 * each Lust counter on units on adjacent cells and on gain essence from
	 * ones on themselves!
	 * 
	 * status buffs?
	 * 
	 * bewitched? :)
	 */
}
