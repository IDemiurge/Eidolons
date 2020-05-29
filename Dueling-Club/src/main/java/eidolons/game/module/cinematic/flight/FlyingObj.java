package eidolons.game.module.cinematic.flight;

import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.moving.MoveController;
import main.system.data.DataUnit;

public class FlyingObj { //just wrap?
    SuperActor actor;
    MoveController controller;

    public FlyingObj(SuperActor actor, MoveController controller) {
        this.actor = actor;
        this.controller = controller;
    }

    public void act(float delta) {
        actor.act(delta);
        controller.act(delta);
    }

    public void setPosition(float x, float y) {
        actor.setPosition(x, y);
    }
    public class FlyObjData extends DataUnit<FLY_OBJ_VALUE> {

    }
    public enum FLY_OBJ_VALUE{
        minDelay,
        maxDelay,
        destination,
        max_speed,
        min_speed,
        acceleration
    }
}
