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
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.GuiStage;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.DIALOG_SHOW;

/**
 * Created by JustMe on 2/3/2018.
 */
public abstract class GameScreen extends ScreenWithVideoLoader {

    public InputController controller;
    protected ChainedStage dialogsStage = null;
    protected DC_SoundMaster soundMaster;
    protected ShaderProgram bufferedShader;
    protected Float speed;
    protected TextureRegion backTexture;
    protected GuiStage guiStage;
    private RealTimeGameLoop realTimeGameLoop;
    private List<Screenshake> shakes = new ArrayList<>();
    protected CameraMan cameraMan;

    public GameScreen() {
        GuiEventManager.bind(GuiEventType.GAME_PAUSED, d -> {
            DC_Game.game.getLoop().setPaused(true);
        });
        GuiEventManager.bind(GuiEventType.GAME_RESUMED, d -> {
            DC_Game.game.getLoop().setPaused(false);
        });
        GuiEventManager.bind(GuiEventType.CAMERA_SHAKE, p -> {
            shakes.add((Screenshake) p.get());
        });
        GuiEventManager.bind(GuiEventType.CAMERA_SET_TO, p -> {
            getCam().position.set((Vector2) p.get(), 0);

        });
        GuiEventManager.bind(DIALOG_SHOW, obj -> {
            DialogueHandler handler =
                    (DialogueHandler) obj.get();
            guiStage.afterBlackout(() -> guiStage.playDialogue(handler));
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

    @Override
    public void render(float delta) {
        if (!shakes.isEmpty()) {
            for (Screenshake shake : new ArrayList<>(shakes)) {
                if (!shake.update(delta, getCam(), cameraMan.getCameraCenter())) {
                    shakes.remove(shake);
                }
            }
            getCam().update();
        }
        super.render(delta);
    }


    public GuiStage getGuiStage() {
        return guiStage;
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

    public TextureRegion getBackTexture() {
        return backTexture;
    }

    public RealTimeGameLoop getRealTimeGameLoop() {
        return realTimeGameLoop;
    }

    public void setRealTimeGameLoop(RealTimeGameLoop realTimeGameLoop) {
        this.realTimeGameLoop = realTimeGameLoop;
    }

    public OrthographicCamera getCamera() {
        return getCam();
    }

    public CameraMan getCameraMan() {
        return cameraMan;
    }

    public OrthographicCamera getCam() {
        return cameraMan.getCam();
    }

    public void setCam(OrthographicCamera cam) {
        cameraMan = new CameraMan(cam, this );
    }

    public void cameraStop(boolean full) {
        cameraMan.cameraStop(full);
    }
}
