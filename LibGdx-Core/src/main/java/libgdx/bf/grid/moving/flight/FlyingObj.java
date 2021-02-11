package libgdx.bf.grid.moving.flight;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import libgdx.bf.SuperActor;
import libgdx.bf.grid.moving.MoveController;

public class FlyingObj extends SuperActor { //just wrap?
    SuperActor actor;
    MoveController controller;

    public FlyingObj(SuperActor actor ) {
        addActor(this.actor = actor);
        setTouchable(Touchable.disabled);
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
