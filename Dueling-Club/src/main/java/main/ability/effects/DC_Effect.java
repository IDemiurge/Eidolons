package main.ability.effects;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public abstract class DC_Effect extends MicroEffect {

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public DC_ActiveObj getActiveObj() {
        if (getRef().getActive() instanceof DC_ActiveObj) {
            return (DC_ActiveObj) getRef().getActive();
        }
        return null;
    }

    public Unit getTarget() {
        if (getRef().getTargetObj() instanceof Unit) {
            return (Unit) getRef().getTargetObj();
        }
        return null;
    }

    public Unit getSource() {
        return getUnit();
    }

    public Unit getUnit() {
        if (getRef().getSourceObj() instanceof Unit) {
            return (Unit) getRef().getSourceObj();
        }
        return null;
    }

}
