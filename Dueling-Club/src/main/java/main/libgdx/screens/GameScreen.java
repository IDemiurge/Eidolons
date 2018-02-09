package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import main.libgdx.DialogScenario;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.ChainedStage;
import main.system.GuiEventManager;
import main.system.audio.DC_SoundMaster;

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

    public TextureRegion getBackTexture() {
        return backTexture;
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
}
