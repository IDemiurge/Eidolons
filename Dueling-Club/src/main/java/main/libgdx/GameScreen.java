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
import main.data.filesys.PathFinder;
import main.entity.obj.DC_Obj;
import main.game.DC_Game;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.Background;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.controls.radial.DebugRadialManager;
import main.libgdx.bf.controls.radial.RadialMenu;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.bf.mouse.ToolTipManager;
import main.libgdx.gui.dialog.DialogDisplay;
import main.libgdx.gui.dialog.LogDialog;
import main.libgdx.gui.panels.dc.InitiativeQueue;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
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
    private Stage grid;
    private Stage gui;
    private Stage dialog;
    private Stage anims;
    private Stage ambience;
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

    public static GameScreen getInstance() {
        return instance;
    }

    public void PostGameStart() {
        InputMultiplexer multiplexer = new InputMultiplexer(gui, controller, grid);
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
        grid.getViewport().setCamera(cam);
        anims.getViewport().setCamera(cam);
        effects.getViewport().setCamera(cam);
    }

    private void initBf() {
        grid = new Stage();
    }

    private void initEffects() {
        effects = new Stage();
        particleManager = new ParticleManager(effects);
    }

    private void initAnims() {
        anims = new Stage();
        animMaster = new AnimMaster(anims);
    }

    private void initDialog() {
        dialog = new Stage();
        dialogDisplay = new DialogDisplay();
        dialog.addActor(dialogDisplay);
    }

    private void initGui() {
        final Texture t = new Texture(GameScreen.class.getResource("/data/marble_green.png").getPath());
        gui = new Stage();
        gui.addActor(radialMenu = new RadialMenu(t));
        gui.addActor(toolTipManager = new ToolTipManager());
        queue = new InitiativeQueue();
        gui.addActor(queue);
        queue.setPosition(0, Gdx.app.getGraphics().getHeight() - 64);

        LogDialog ld = new LogDialog();
//        gui.addActor(ld);
//        ld.setPosition(Gdx.graphics.getWidth() - ld.getWidth(), 0);
        ld.setPosition(200, 200);
    }

    private void bindEvents() {
        GuiEventManager.bind(UPDATE_GUI, param -> {
            queue.update();
        });

        GuiEventManager.bind(GRID_CREATED, param -> {
            Pair<Integer, Integer> p = ((Pair<Integer, Integer>) param.get());
            gridPanel = new GridPanel(p.getLeft(), p.getRight()).init();
            grid.addActor(gridPanel);
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

        gui.act(delta);
        grid.act(delta);

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

        grid.draw();

        effects.draw();
        anims.draw();


        gui.draw();

        if (dialogDisplay.getDialog() != null) {
            dialog.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

/*        camera = cam = new OrthographicCamera(width, height);
        cam.update();*/
        cam.setToOrtho(false, width, height);
        anims.getViewport().update(width, height);
        effects.getViewport().update(width, height);
        grid.getViewport().update(width, height);
        gui.getViewport().update(width, height);

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
}
