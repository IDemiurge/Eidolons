package eidolons.game.battlecraft.rules.counter.active;

import main.ability.effects.Effect;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;

public class AdrenalineCRule extends ActiveCRule {
    @Override
    protected PARAMETER getRetainmentParam() {
        return null;
    }

    @Override
    public UnitEnums.COUNTER getCounter() {
        return null;
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
    public UnitEnums.STATUS getStatus() {
        return null;
    }
}
