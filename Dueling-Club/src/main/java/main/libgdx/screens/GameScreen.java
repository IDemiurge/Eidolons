package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.DialogScenario;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.ChainedStage;
import main.system.GuiEventManager;
import main.system.audio.DC_SoundMaster;
import main.system.launch.CoreEngine;

import java.util.List;

import static main.system.GuiEventType.DIALOG_SHOW;

/**
 * Created by JustMe on 2/3/2018.
 */
public abstract class GameScreen extends ScreenWithVideoLoader{

    protected ChainedStage dialogsStage = null;
    protected OrthographicCamera cam;
    protected InputController controller;
    protected Vector2 velocity;
    protected DC_SoundMaster soundMaster;
    protected Vector2 cameraDestination;
    protected ShaderProgram bufferedShader;
    protected Float speed;
    protected TextureRegion backTexture;
    private RealTimeGameLoop realTimeGameLoop;


    public TextureRegion getBackTexture() {
        return backTexture;
    }

    protected void cameraPan(Vector2 unitPosition) {
        this.cameraDestination = unitPosition;
        float dest = cam.position.dst(unitPosition.x, unitPosition.y, 0f);// / getCameraDistanceFactor();
        if (dest < getCameraMinCameraPanDist())
            return;
        velocity = new Vector2(unitPosition.x - cam.position.x, unitPosition.y - cam.position.y).nor().scl(Math.min(cam.position.dst(unitPosition.x, unitPosition.y, 0f), dest));
        if (CoreEngine.isGraphicTestMode()) {
//            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- coordinatesActiveObj:" + coordinatesActiveObj);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- unitPosition:" + unitPosition);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- dest:" + dest);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- velocity:" + velocity);
        }
    }

    protected float getCameraMinCameraPanDist() {
        return 350; //TODO if too close to the edge also
    }

    protected float getCameraDistanceFactor() {
        return 8f;
    }

    protected void cameraShift() {
        if (cameraDestination != null)
            if (cam != null && velocity != null && !velocity.isZero()) {
                try {
                    float x = velocity.x > 0
                     ? Math.min(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime())
                     : Math.max(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime());
                    float y = velocity.y > 0
                     ? Math.min(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime())
                     : Math.max(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime());
                    cam.position.set(x, y, 0f);
                    float dest = cam.position.dst(cameraDestination.x, cameraDestination.y, 0f) / getCameraDistanceFactor();
                    Vector2 velocityNow = new Vector2(cameraDestination.x - cam.position.x, cameraDestination.y - cam.position.y).nor().scl(Math.min(cam.position.dst(cameraDestination.x, cameraDestination.y, 0f), dest));
//                    if (CoreEngine.isGraphicTestMode()) {
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- pos x:" + x);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- pos y:" + y);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity:" + velocity);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocityNow:" + velocityNow);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity.hasOppositeDirection(velocityNow):" + velocity.hasOppositeDirection(velocityNow));
//                    }

                    if ( velocityNow.isZero()|| velocity.hasOppositeDirection(velocityNow)) {
                        cameraStop();
                    }
                } catch (Exception exp) {
                    if (CoreEngine.isGraphicTestMode())
                        Gdx.app.log("DungeonScreen::cameraShift()", "-- exp:" + exp);
                }
                cam.update();
            }
    }

    public void cameraStop() {
        if (velocity != null)
            velocity.setZero();
    }

    public InputController getController() {
        return controller;
    }

    protected void initGl() {
        GL30 gl = Gdx.graphics.getGL30();
        if (gl != null) {
            gl.glEnable(GL30.GL_BLEND);
            gl.glEnable(GL30.GL_TEXTURE_2D);
            gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GL20 gl20 = Gdx.graphics.getGL20();
            gl20.glEnable(GL20.GL_BLEND);
            gl20.glEnable(GL20.GL_TEXTURE_2D);
            gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
    protected void initDialogue() {

        GuiEventManager.bind(DIALOG_SHOW, obj -> {
            DialogueHandler handler = (DialogueHandler) obj.get();
            final List<DialogScenario> list = handler.getList();
            if (dialogsStage == null) {
                dialogsStage = new ChainedStage(viewPort, getBatch(), list);
                updateInputController();
            } else {
                dialogsStage.play(list);
            }
            dialogsStage.setDialogueHandler(handler);
        });
    }

    public RealTimeGameLoop getRealTimeGameLoop() {
        return realTimeGameLoop;
    }

    public void setRealTimeGameLoop(RealTimeGameLoop realTimeGameLoop) {
        this.realTimeGameLoop = realTimeGameLoop;
    }

    public OrthographicCamera getCamera() {
        return cam;
    }
}
