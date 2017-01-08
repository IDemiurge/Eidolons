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
import main.libgdx.bf.Background;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.controls.radial.DebugRadialManager;
import main.libgdx.bf.controls.radial.RadialMenu;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.bf.mouse.ToolTipManager;
import main.libgdx.gui.dialog.DialogDisplay;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.libgdx.prototype.ParticleActor;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.GRID_CREATED;

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
    private Stage bf;
    private Stage gui;
    private Stage dialog;
    private Background background;
    private GridPanel gridPanel;
    private RadialMenu radialMenu;
    private SpriteBatch batch;
    private OrthographicCamera cam;
    private InputController controller;
    private DialogDisplay dialogDisplay;
    private Stage effects;

    // temp by Bogdan
    private ParticleActor partAct;
    // temp end by Bogdan

    public static GameScreen getInstance() {
        return instance;
    }

    public void PostGameStart() {
        InputMultiplexer multiplexer = new InputMultiplexer(gui, controller, bf);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public GameScreen PostConstruct() {
        PathFinder.init();
        instance = this;
        bf = new Stage();
        effects= new Stage();
        dialog= new Stage();
        dialogDisplay =new DialogDisplay();
        dialog.addActor(dialogDisplay);
        initGui();

        // temp by Bogdan
        partAct = new ParticleActor();
        // temp end by Bogdan

        camera = cam = new OrthographicCamera();
        cam.setToOrtho(false, 1600, 900);
        bf.getViewport().setCamera(cam);
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

    private void initGui() {
        final Texture t = new Texture(GameScreen.class.getResource("/data/marble_green.png").getPath());
        gui = new Stage();
        gui.addActor(radialMenu = new RadialMenu(t));
        gui.addActor(toolTipManager = new ToolTipManager());
    }

    private void bindEvents() {
        GuiEventManager.bind(GRID_CREATED, param -> {
            Pair<Integer, Integer> p = ((Pair<Integer, Integer>) param.get());
            gridPanel = new GridPanel(p.getLeft(), p.getRight()).init();
            bf.addActor(gridPanel);
            effects.addActor(partAct);
        });

        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            DC_Obj dc_obj = (DC_Obj) obj.get();

            if (Gdx.input.isButtonPressed(0)||Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
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

        //gui.act(delta);
        //bf.act(delta);

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

        bf.draw();
        effects.draw();
        if (dialogDisplay.getDialog()==null )
        gui.draw();
        else
        dialog.draw();
    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

/*        camera = cam = new OrthographicCamera(width, height);
        cam.update();*/
        cam.setToOrtho(false, width, height);
        bf.getViewport().update(width, height);
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
}
