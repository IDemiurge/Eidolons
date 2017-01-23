package main.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import main.data.filesys.PathFinder;
import main.entity.obj.DC_Obj;
import main.game.DC_Game;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.bf.Background;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.controls.radial.DebugRadialManager;
import main.libgdx.bf.controls.radial.RadialMenu;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.bf.mouse.ToolTipManager;
import main.libgdx.gui.dialog.DialogDisplay;
import main.libgdx.gui.panels.dc.InitiativeQueue;
import main.libgdx.gui.panels.dc.LogPanel;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.FAST_DC;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {
    public static OrthographicCamera camera;
    private static GameScreen instance;
    protected ToolTipManager toolTipManager;
    private Stage gridStage;
    private Stage guiStage;
    private Stage dialogStage;
    private Stage animsStage;
    private Stage ambienceStage;
    private Stage phaseAnimsStage;
    private Background background;
    private GridPanel gridPanel;
    private RadialMenu radialMenu;
    private SpriteBatch batch;
    private OrthographicCamera cam;
    private InputController controller;
    private DialogDisplay dialogDisplay;
    private Stage effects;

    private ParticleManager particleManager;
    private AnimMaster animMaster;
    private InitiativeQueue queue;
    private PhaseAnimator phaseAnimator;

    public static GameScreen getInstance() {
        return instance;
    }

    public void PostGameStart() {
        InputMultiplexer multiplexer = new InputMultiplexer(guiStage, controller, gridStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public GameScreen PostConstruct() {
        PathFinder.init();
        instance = this;

        initBf();
        initDialog();
        initEffects();
        initGui();
        initAnims();
        initCamera();
        controller = new InputController(cam);

        GL20 gl = Gdx.graphics.getGL20();
        gl.glEnable(GL20.GL_BLEND);
        gl.glEnable(GL20.GL_TEXTURE_2D);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch = new SpriteBatch();

        background = new Background().init();

        bindEvents();

        WaitMaster.receiveInput(WAIT_OPERATIONS.GDX_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GDX_READY);
        WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);

        return this;
    }

    private void initCamera() {
        camera = cam = new OrthographicCamera();
        cam.setToOrtho(false, 1600, 900);
        gridStage.getViewport().setCamera(cam);
        animsStage.getViewport().setCamera(cam);
        phaseAnimsStage.getViewport().setCamera(cam);
        effects.getViewport().setCamera(cam);
    }

    private void initBf() {
        gridStage = new Stage();
    }

    private void initEffects() {
        effects = new Stage();
        particleManager = new ParticleManager(effects);
    }

    private void initAnims() {
        animsStage = new Stage();
        phaseAnimsStage = new Stage();
        animMaster = new AnimMaster(animsStage);
        phaseAnimator = new PhaseAnimator(phaseAnimsStage);
    }

    private void initDialog() {
        dialogStage = new Stage();
        dialogDisplay = new DialogDisplay();
        dialogStage.addActor(dialogDisplay);
    }

    private void initGui() {
        final Texture t = new Texture(GameScreen.class.getResource("/data/marble_green.png").getPath());
        guiStage = new Stage();
        guiStage.addActor(radialMenu = new RadialMenu(t));
        guiStage.addActor(toolTipManager = new ToolTipManager());
        queue = new InitiativeQueue();
        guiStage.addActor(queue);
        queue.setPosition(0, 0, Align.topLeft);


        if (!FAST_DC.getGameLauncher().getFAST_MODE()
                && !FAST_DC.getGameLauncher().getSUPER_FAST_MODE()) {
            LogPanel ld = new LogPanel();
            guiStage.addActor(ld);
            ld.setPosition(Gdx.graphics.getWidth() - ld.getWidth(), 0);
        }
    }

    private void bindEvents() {
        GuiEventManager.bind(UPDATE_GUI, param -> {
            queue.update();
        });

        GuiEventManager.bind(GRID_CREATED, param -> {
            Pair<Integer, Integer> p = ((Pair<Integer, Integer>) param.get());
            gridPanel = new GridPanel(p.getLeft(), p.getRight()).init();
            gridStage.addActor(gridPanel);
        });

        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            DC_Obj dc_obj = (DC_Obj) obj.get();

            if (Gdx.input.isButtonPressed(0) || Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                radialMenu.init(DebugRadialManager.getDebugNodes(dc_obj));
            } else {
                radialMenu.createNew(dc_obj);
            }
        });
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }

        GuiEventManager.processEvents();

        guiStage.act(delta);
        gridStage.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        if (background.isDirty()) {
            background.update();
        }
        batch.disableBlending();
        background.draw(batch, 1);
        batch.enableBlending();

        batch.end();

        gridStage.draw();

        effects.draw();
        if (DC_Game.game != null)
            if (DC_Game.game.getAnimationManager() != null)
                DC_Game.game.getAnimationManager().updateAnimations();
        if (animMaster.isOn()) {
            phaseAnimsStage.draw();
            animsStage.draw();
        }


        guiStage.draw();

        if (dialogDisplay.getDialog() != null) {
            dialogStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

/*        camera = cam = new OrthographicCamera(width, height);
        cam.update();*/
        cam.setToOrtho(false, width, height);
        animsStage.getViewport().update(width, height);
        phaseAnimsStage.getViewport().update(width, height);
        effects.getViewport().update(width, height);
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

    public Background getBackground() {
        return background;
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public InputController getController() {
        return controller;
    }

    public DialogDisplay getDialogDisplay() {
        return dialogDisplay;
    }

    public Stage getGridStage() {
        return gridStage;
    }

    public Stage getGuiStage() {
        return guiStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public Stage getAnimsStage() {
        return animsStage;
    }

    public Stage getAmbienceStage() {
        return ambienceStage;
    }

    public Stage getPhaseAnimsStage() {
        return phaseAnimsStage;
    }
}
