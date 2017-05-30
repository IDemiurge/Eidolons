package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.core.game.DC_Game;
import main.libgdx.DialogScenario;
import main.libgdx.bf.BFDataCreatedEvent;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.AnimationEffectStage;
import main.libgdx.stage.BattleGuiStage;
import main.libgdx.stage.ChainedStage;
import main.system.GuiEventManager;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.DIALOG_SHOW;
import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends ScreenWithLoader {
    public static OrthographicCamera camera;
    private static DungeonScreen instance;

    private Stage gridStage;
    private BattleGuiStage guiStage;
    private GridPanel gridPanel;
    private ChainedStage dialogsStage = null;
    private OrthographicCamera cam;
    private InputController controller;

    private TextureRegion backTexture;
    private AnimationEffectStage animationEffectStage;

    public static DungeonScreen getInstance() {
        return instance;
    }

    @Override
    protected void preLoad() {
        instance = this;
        if (data.getDialogScenarios().size() > 0) {
            dialogsStage = new ChainedStage(data.getDialogScenarios());
        }

        gridStage = new Stage();
        gridStage.setViewport(viewPort);

        guiStage = new BattleGuiStage();

        animationEffectStage = new AnimationEffectStage();

        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        cam = camera = (OrthographicCamera) viewPort.getCamera();
        controller = new InputController(cam);

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
        });

        GuiEventManager.bind(DIALOG_SHOW, obj -> {
            final List<DialogScenario> list = (List<DialogScenario>) obj.get();
            if (dialogsStage == null) {
                dialogsStage = new ChainedStage(list);
                updateInputController();
            } else {
                dialogsStage.play(list);
            }
        });
    }

    @Override
    protected void afterLoad() {
        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH()).init(param.getObjects());
        gridStage.addActor(gridPanel);
    }

    @Override
    protected InputMultiplexer getInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, gridStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
        } else {
            current = super.getInputController();
        }

        return current;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }

        super.render(delta);

        guiStage.act(delta);
        animationEffectStage.act(delta);
        gridStage.act(delta);

        cam.update();

        if (canShowScreen()) {
            if (backTexture != null) {
                guiStage.getBatch().begin();
                guiStage.getBatch().draw(backTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                guiStage.getBatch().end();
            }

            gridStage.draw();

            animationEffectStage.draw();

            guiStage.draw();

            if (dialogsStage != null) {
                dialogsStage.act(delta);
                if (dialogsStage.isDone()) {
                    final ChainedStage dialogsStage = this.dialogsStage;
                    this.dialogsStage = null;
                    dialogsStage.dispose();
                    updateInputController();
                } else {
                    dialogsStage.draw();
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

        cam.setToOrtho(false, width, height);
        animationEffectStage.getViewport().update(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public InputController getController() {
        return controller;
    }

    public Stage getGridStage() {
        return gridStage;
    }

    public Stage getAnimsStage() {
        return animationEffectStage;
    }
}
