package eidolons.game.battlecraft.rules.counter;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;

/**
 * Created by JustMe on 4/19/2017.
 */
public class EncaseRule extends DC_CounterRule {
    private static final String WEIGHT_PER_COUNTER = "1";
    //    private static final String RESISTANCES ="Acid|Lightning|Bludgeoning" ;
//    private static final String RESIST_PER_COUNTER = "1";
    private static final String ARMOR_PER_COUNTER = "1";
    private static final String PENALTY_PER_COUNTER = "1";

    public EncaseRule(DC_Game game) {
        super(game);
    }


    @Override
    public COUNTER getCounter() {
        return COUNTER.Encase;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
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
         new ModifyValueEffect(PARAMS.ATB_COST_MOD
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + PENALTY_PER_COUNTER),
         new ModifyValueEffect(PARAMS.ARMOR
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + ARMOR_PER_COUNTER)
        );
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}
