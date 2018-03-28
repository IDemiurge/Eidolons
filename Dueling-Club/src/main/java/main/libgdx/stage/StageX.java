package main.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.system.auxiliary.secondary.ReflectionMaster;

/**
 * Created by JustMe on 3/26/2018.
 */
public class StageX extends Stage {

    public StageX() {
    }

    public StageX(Viewport viewport) {
        super(viewport);
    }

    public StageX(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    public Actor getMouseOverActor() {
        return new ReflectionMaster<Actor>().getFieldValue("mouseOverActor", this, Stage.class);
    }
}
