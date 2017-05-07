package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.core.game.DC_Game;
import main.libgdx.bf.BFDataCreatedEvent;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.AnimationEffectStage;
import main.libgdx.stage.BattleGuiStage;
import main.libgdx.stage.LoadingStage;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen implements Screen {
    public static OrthographicCamera camera;
    private static DungeonScreen instance;
    private Stage gridStage;
    private BattleGuiStage guiStage;
    private GridPanel gridPanel;
    private OrthographicCamera cam;
    private InputController controller;

    private TextureRegion backTexture;
    private boolean showLoading = true;
    private LoadingStage loadingStage;
    private AnimationEffectStage animationEffectStage;

    public static DungeonScreen getInstance() {
        return instance;
    }

    public DungeonScreen PostConstruct() {
        instance = this;

        gridStage = new Stage();
        GuiEventManager.bind(BF_CREATED, param -> {
            final BFDataCreatedEvent data = (BFDataCreatedEvent) param;
            gridPanel = new GridPanel(data.getGridW(), data.getGridH()).init(data.getObjects());
            gridStage.addActor(gridPanel);
        });

        loadingStage = new LoadingStage();

        guiStage = new BattleGuiStage();

        animationEffectStage = new AnimationEffectStage();

        initCamera();

        controller = new InputController(cam);

        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
        });

        GuiEventManager.bind(DUNGEON_LOADED, param -> {
            showLoading = false;
            InputMultiplexer multiplexer = new InputMultiplexer(guiStage, controller, gridStage);
            Gdx.input.setInputProcessor(multiplexer);
        });

        WaitMaster.receiveInput(WAIT_OPERATIONS.GDX_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GDX_READY);

        return this;
    }

    private void initCamera() {
        camera = cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gridStage.getViewport().setCamera(cam);
        animationEffectStage.getViewport().setCamera(cam);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }

        GuiEventManager.processEvents();

        guiStage.act(delta);
        animationEffectStage.act(delta);
        gridStage.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        if (showLoading) {
            loadingStage.act(delta);
            loadingStage.draw();
        } else {
            if (backTexture != null) {
                guiStage.getBatch().begin();
                guiStage.getBatch().draw(backTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                guiStage.getBatch().end();
            }

            gridStage.draw();

            animationEffectStage.draw();

            guiStage.draw();
        }

    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

/*        camera = cam = new OrthographicCamera(width, height);
        cam.update();*/
        cam.setToOrtho(false, width, height);
        animationEffectStage.getViewport().update(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);
/*        to disable pixelperfect
        float camWidth = MapView.TILE_WIDTH * 10.0f;
        float camHeight = camWidth * ((float)height / (float)width);
*/

/*        engine.camera = new Camera(camWidth, camHeight);
        engine.camera.update();*/
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

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
