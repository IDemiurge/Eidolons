package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class DespairRule extends DC_CounterRule {
    /*
     *
	 * 
	 * provide morale to Warp Demons nearby?
	 */

    private static final String MORALE_PER_COUNTER = "(-0.5)";
    private static final String SPIRIT_PER_COUNTER = "(-1)";

    // private static final String AFFECTED_RESISTANCES =
    // "Acid|Fire|Lightning|Cold";

    public DespairRule(DC_Game game) {
        super(game);
    }


    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return unit.getIntParam(PARAMS.SPIRIT) / 2;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        Effects effects = new Effects(new ModifyValueEffect(PARAMS.C_MORALE,
         MOD.MODIFY_BY_CONST, getCounterRef() + "*"
         + MORALE_PER_COUNTER));

        return effects;
    }

    @Override
    public String getBuffName() {
        return "Despair";
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Despair;
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.SPIRIT,
         MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
         + SPIRIT_PER_COUNTER));
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}
