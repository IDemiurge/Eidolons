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
 * Created by JustMe on 4/20/2017.
 */
public class GreaseRule extends DC_CounterRule {
    private static final String RESISTANCES = "Fire";
    private static final String RESISTANCES_BONUS = "Acid|Lightning|Cold";
    private static final String PENALTY_PARAMS = PARAMS.MOVE_AP_PENALTY.getName();
    private static final String RESIST_PER_COUNTER = "-3";
    private static final String RESIST_BONUS_PER_COUNTER = "1";
    private static final String MOVE_AP_PENALTY_PER_COUNTER = "2";

    public GreaseRule(DC_Game game) {
        super(game);
    }


    @Override
    public COUNTER getCounter() {
        return COUNTER.Grease;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 1; // %-BASED?
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getEffect() {
        return new Effects(
         new ModifyValueEffect(RESISTANCES
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + RESIST_PER_COUNTER),

         new ModifyValueEffect(RESISTANCES_BONUS
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + RESIST_BONUS_PER_COUNTER),

         new ModifyValueEffect(PENALTY_PARAMS
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + MOVE_AP_PENALTY_PER_COUNTER)
        );
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}
