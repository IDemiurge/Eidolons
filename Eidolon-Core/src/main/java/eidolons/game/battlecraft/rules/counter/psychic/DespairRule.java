package eidolons.game.battlecraft.rules.counter.psychic;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;

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
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        return unit.getIntParam(PARAMS.SPIRIT) / 2;
    }

    @Override
    protected Effect getSpecialRoundEffects() {

        return new Effects(new ModifyValueEffect(PARAMS.C_ESSENCE,
         MOD.MODIFY_BY_CONST, getCounterRef() + "*"
         + MORALE_PER_COUNTER));
    }

    @Override
    public String getBuffName() {
        return "Despair";
    }

    @Override
    public COUNTER getCounter() {
        return EffectEnums.COUNTER.Despair;
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
