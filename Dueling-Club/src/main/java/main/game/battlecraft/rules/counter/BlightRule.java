package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class BlightRule extends DC_CounterRule {
    /*
     * resistance reduction
	 * 
	 * consider that it will be used in Witchery, Affliction and Demonology
	 */

    private static final String ASTRAL_RESIST_PER_COUNTER = "(-1)";
    private static final String RESIST_PER_COUNTER = "(-1)";

    // private static final String AFFECTED_RESISTANCES =
    // "Acid|Fire|Lightning|Cold";

    public BlightRule(DC_Game game) {
        super(game);
    }


    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 0;
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Blight;
    }

    @Override
    public String getBuffName() {
        return "Blighted";
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.RESISTANCE,
         MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
         + RESIST_PER_COUNTER), new ModifyValueEffect(
         VALUE_GROUP.ASTRAL_RESISTANCES.toString(),
         MOD.MODIFY_BY_CONST, getCounterRef() + "*"
         + ASTRAL_RESIST_PER_COUNTER));
    }

    @Override
    public STATUS getStatus() {
        return null;
    }

}
