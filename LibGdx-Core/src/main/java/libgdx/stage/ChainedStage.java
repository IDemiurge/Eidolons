package libgdx.stage;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.Scene;
import libgdx.GdxMaster;

import java.util.Iterator;
import java.util.List;

public class ChainedStage<T extends Scene> extends Stage {
    private boolean done;
    private Runnable onDoneCallback;
    private DialogueHandler dialogueHandler;
    private Container  current;
    private Iterator<T> iterator;
    private List<T> newList = null;
    private Matrix4 idt = new Matrix4();
    private Matrix4 shear = new Matrix4();
    private OrthographicCamera cam;

    public ChainedStage(ScreenViewport viewport, Batch batch, List<T> list) {
        super(viewport == null ?
          new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
           GdxMaster.getHeight(), new OrthographicCamera()) : viewport,
         batch == null ? new SpriteBatch() :
          batch);
        current = new Container ();
        iterator = list.iterator();
        if (iterator.hasNext()) {
            current.setActor((Actor) iterator.next());
        }
        setKeyboardFocus(current.getActor());
        addActor(current);
        setDebugAll(false);
    }

    public static Matrix4 toShear(Matrix4 m, float shx, float shy) {
        m.idt();
        m.val[Matrix4.M01] = shx;
        m.val[Matrix4.M10] = shy;
        return m;
    }

    public void play(List<T> list) {
        newList = list;
    }

    @Override
    public boolean keyDown(int keyCode) {
        return super.keyDown(keyCode);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (current.getActor() != null && ((Scene) current.getActor()).isDone()) {
            if (iterator.hasNext()) {
                if (dialogueHandler != null) {
//                    dialogueHandler.lineSpoken((Scene) current.getActor());
                }
                current.setActor((Actor) iterator.next());
                setKeyboardFocus(current.getActor());
            } else if (newList != null) {
                final List<T> list = this.newList;
                newList = null;
                iterator = list.iterator();
            } else {
                done = true;
                if (onDoneCallback != null) {
                    onDoneCallback.run();
                }
                if (dialogueHandler != null) {
                    dialogueHandler.dialogueDone();
                }
            }
        }
    }

    public void setOnDoneCallback(Runnable onDoneCallback) {
        this.onDoneCallback = onDoneCallback;
    }

    @Override
    public void draw() {
        super.draw();
//        final Matrix4 combined = getCamera().combined.cpy();
//        getCamera().update();
//
//        final Group root = getRoot();
//
//        if (!root.isVisible()) return;
//
//        combined.setToOrtho2D(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
//
//        Batch batch = this.getBatch();
//        batch.setProjectionMatrix(combined);
//        batch.begin();
//        root.draw(batch, 1);
//        batch.end();
    }

    //    @Override
//    public void render () {
//        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
//        cam.setToOrtho(false);
//        spriteBatch.setProjectionMatrix( cam.combined );
//        spriteBatch.begin();
//
//        //draw your regular sprites with identity transform...
//        spriteBatch.setTransformMatrix( idt );
//        spriteBatch.draw(texture, 0, 0, 25, 25);
//
//        //now draw your sheared sprites
//        toShear(shear, 0.80f, 0.0f);
//        spriteBatch.setTransformMatrix( shear );
//
//        spriteBatch.draw(texture, 50, 50);
//        spriteBatch.end();
//    }
    public boolean isDone() {
        return done;
    }

    public void setDialogueHandler(DialogueHandler dialogueHandler) {
        this.dialogueHandler = dialogueHandler;
    }
}
