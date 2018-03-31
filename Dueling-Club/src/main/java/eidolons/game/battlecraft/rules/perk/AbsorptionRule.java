package eidolons.game.battlecraft.rules.perk;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.entity.Ref;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.DC_CounterRule;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 12/27/2016.
 */
public class AbsorptionRule extends DC_CounterRule {


    public AbsorptionRule(DC_Game game) {
        super(game);
    }

    public void damageDealt(DAMAGE_TYPE type, int amount, Ref ref) {
//        preCheck
    }

    @Override
    public String getCounterName() {
        return COUNTER.Blaze.getName();
    }

    @Override
    public COUNTER getCounter() {
        return null;
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
