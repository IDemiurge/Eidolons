package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.PlainDialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import eidolons.libgdx.GDX;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.GuiStage;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.List;

import static main.system.GuiEventType.DIALOG_SHOW;

/**
 * Created by JustMe on 2/3/2018.
 */
public abstract class GameScreen extends ScreenWithVideoLoader {

    private static final float MAX_CAM_DST = 500;
    private static Float cameraPanMod;
    public InputController controller;
    protected ChainedStage dialogsStage = null;
    protected OrthographicCamera cam;
    protected Vector2 velocity;
    protected DC_SoundMaster soundMaster;
    protected Vector2 cameraDestination;
    protected ShaderProgram bufferedShader;
    protected Float speed;
    protected TextureRegion backTexture;
    protected GuiStage guiStage;
    private RealTimeGameLoop realTimeGameLoop;
    private Boolean centerCameraAlways;
    private Vector3 lastPos;

    public GameScreen() {
        GuiEventManager.bind(GuiEventType.GAME_PAUSED, d -> {
            DC_Game.game.getLoop().setPaused(true);
        });
        GuiEventManager.bind(GuiEventType.GAME_RESUMED, d -> {
            DC_Game.game.getLoop().setPaused(false);
        });
    }

    public static float getCameraPanMod() {
        if (cameraPanMod == null)
            cameraPanMod = new Float(OptionsMaster.getControlOptions().
             getIntValue(CONTROL_OPTION.CENTER_CAMERA_DISTANCE_MOD)) / 100;

        return cameraPanMod;
    }

    public static void setCameraPanMod(float mod) {
        cameraPanMod = mod;
    }

    public TextureRegion getBackTexture() {
        return backTexture;
    }

    protected void cameraPan(Vector2 unitPosition) {
        cameraPan(unitPosition, null);
    }

    protected boolean isCameraPanningOff() {
        return false; //TODO
    }
    protected void cameraPan(Vector2 unitPosition, Boolean overrideCheck) {
        if (isCameraPanningOff()){
            return;
        }
        this.cameraDestination = unitPosition;
        float dst = cam.position.dst(unitPosition.x, unitPosition.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (isCenterAlways()) {
                overrideCheck = !controller.isWithinCamera(unitPosition.x, unitPosition.y, 128, 128);
            } else overrideCheck = false;

        if (!overrideCheck)
            if (dst < getCameraMinCameraPanDist())
                return;

        velocity = new Vector2(unitPosition.x - cam.position.x, unitPosition.y - cam.position.y).nor().scl(Math.min(cam.position.dst(unitPosition.x, unitPosition.y, 0f), dst));
        if (CoreEngine.isGraphicTestMode()) {
            //            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- coordinatesActiveObj:" + coordinatesActiveObj);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- unitPosition:" + unitPosition);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- dest:" + dst);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- velocity:" + velocity);
        }
    }


    protected float getCameraMinCameraPanDist() {
        return (GDX.size(1600, 0.1f)) / 3 * getCameraPanMod(); //TODO if too close to the edge also
    }

    protected float getCameraDistanceFactor() {
        return 8f;
    }

    protected void cameraShift() {
        checkCameraFix();
        if (cameraDestination != null)
            if (cam != null && velocity != null && !velocity.isZero()) {
                float x = velocity.x > 0
                 ? Math.min(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime())
                 : Math.max(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime());
                float y = velocity.y > 0
                 ? Math.min(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime())
                 : Math.max(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime());

//                main.system.auxiliary.log.LogMaster.log(1,"cameraShift to "+ y+ ":" +x + " = "+cam);
                cam.position.set(x, y, 0f);
                float dest = cam.position.dst(cameraDestination.x, cameraDestination.y, 0f) / getCameraDistanceFactor();
                Vector2 velocityNow = new Vector2(cameraDestination.x - cam.position.x, cameraDestination.y - cam.position.y).nor().scl(Math.min(cam.position.dst(cameraDestination.x, cameraDestination.y, 0f), dest));

                if (velocityNow.isZero() || velocity.hasOppositeDirection(velocityNow)) {
                    cameraStop();
                }
                cam.update();
                controller.cameraChanged();
            }
    }

    private void checkCameraFix() {
        if (lastPos != null)
        if (cam.position.dst(lastPos)> MAX_CAM_DST) {
            cam.position.set(lastPos);
        }
        lastPos = new Vector3(cam.position);
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
            DialogueHandler handler =
             (DialogueHandler) obj.get();

            if (isNewDialogue())
            {
                guiStage.playDialogue(handler);
                return;
            }

//            if (dialogsStage == null) {
//                dialogsStage = new ChainedStage(viewPort, getBatch(), list);
//
//            } else {
//                dialogsStage.play(list);
//            }
//            dialogsStage.setDialogueHandler(handler);
//            updateInputController();
        });
    }

    private boolean isNewDialogue() {
        return true;
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

    public GuiStage getGuiStage() {
        return guiStage;
    }

    public Boolean isCenterAlways() {
        if (centerCameraAlways == null) {
            centerCameraAlways = OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.ALWAYS_CAMERA_CENTER_ON_ACTIVE);
        }
        return centerCameraAlways;
    }

}
