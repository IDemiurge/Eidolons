package eidolons.game.battlecraft.rules.counter.active;

import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;

public class AdrenalineCRule extends ActiveCRule {
    public AdrenalineCRule(DC_Game game) {
        super(game);
    }

    @Override
    protected String getSaveAmount() {
        return null;
    }

    @Override
    protected PARAMETER getRetainmentParam() {
        return null;
    }

    @Override
    public EffectEnums.COUNTER getCounter() {
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
