package eidolons.ability.effects;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.MicroEffect;

public abstract class DC_Effect extends MicroEffect {

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public ActiveObj getActiveObj() {
        if (getRef().getActive() instanceof ActiveObj) {
            return (ActiveObj) getRef().getActive();
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
        return getSourceUnitOrNull();
    }

    public Unit getSourceUnitOrNull() {
        if (getRef().getSourceObj() instanceof Unit) {
            return (Unit) getRef().getSourceObj();
        }
        return null;
    }

}
