package libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import libgdx.GdxMaster;
import libgdx.screens.batch.CustomSpriteBatch;
import main.system.auxiliary.secondary.ReflectionMaster;

/**
 * Created by JustMe on 3/26/2018.
 */
public class StageX extends Stage {

    public StageX() {
    }

    @Override
    public Batch getBatch() {
        return GdxMaster.getMainBatch();
    }
    //TODO 2022 Review - why the heck ignore this parameters?
    public StageX(Viewport viewport, Batch batch) {
        super(viewport, GdxMaster.getMainBatch());
    }

    public CustomSpriteBatch getCustomSpriteBatch() {
        return (CustomSpriteBatch) super.getBatch();
    }

    @Override
    public void setDebugAll(boolean debugAll) {
        super.setDebugAll(debugAll);
        setDebugInvisible(debugAll);
        setDebugParentUnderMouse(debugAll);
        setDebugTableUnderMouse(debugAll);
        setDebugParentUnderMouse(debugAll);
        setDebugUnderMouse(debugAll);
    }

    public Actor getMouseOverActor() {
        return new ReflectionMaster<Actor>().getFieldValue("mouseOverActor", this, Stage.class);
    }
}
