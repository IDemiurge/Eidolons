package eidolons.game.module.cinematic.flight;

import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.moving.MoveController;

public class FlyingObj extends SuperActor { //just wrap?
    SuperActor actor;
    MoveController controller;

    public FlyingObj(SuperActor actor ) {
        addActor(this.actor = actor);
    }

    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public String toString() {
        return "Flying obj at  " + getX() +                " " +getY()
                + getActions();
    }

    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

}
