package eidolons.ability.effects;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.MicroEffect;

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

    public BattleFieldObject getTarget() {
        if (getRef().getTargetObj() instanceof BattleFieldObject) {
            return (BattleFieldObject) getRef().getTargetObj();
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
