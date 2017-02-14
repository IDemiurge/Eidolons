package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;

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

    public DC_HeroObj getTarget() {
        if (getRef().getTargetObj() instanceof DC_HeroObj) {
            return (DC_HeroObj) getRef().getTargetObj();
        }
        return null;
    }

    public DC_HeroObj getSource() {
        return getUnit();
    }

    public DC_HeroObj getUnit() {
        if (getRef().getSourceObj() instanceof DC_HeroObj) {
            return (DC_HeroObj) getRef().getSourceObj();
        }
        return null;
    }

}
