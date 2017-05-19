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

/**
 * Created by JustMe on 4/19/2017.
 */
public class ClayRule extends  DC_CounterRule {
    private static final String WEIGHT_PER_COUNTER = "1";
    private static final String RESISTANCES ="Acid|Lightning|Bludgeoning" ;
    private static final String RESIST_PER_COUNTER = "1";

    public ClayRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return COUNTER.Clay.getName();
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 3; // %-BASED?
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.WEIGHT,
         MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
         + WEIGHT_PER_COUNTER),
         new ModifyValueEffect(RESISTANCES
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + RESIST_PER_COUNTER)
        );
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}
