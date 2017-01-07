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
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.bf.Background;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.controls.radial.DebugRadialManager;
import main.libgdx.bf.controls.radial.RadialMenu;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.bf.mouse.ToolTipManager;
import main.libgdx.gui.GuiStage;
import main.libgdx.texture.TextureCache;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.GRID_CREATED;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {
    private Stage bf;
    private Stage gui;
    private Stage anim;
    private Stage dialog;

    private Background background;
    private GridPanel gridPanel;
    private TextureCache textureCache;
    private RadialMenu radialMenu;

    protected ToolTipManager toolTipManager;

    private SpriteBatch batch;
    private OrthographicCamera cam;

    private static GameScreen instance;
    private InputController controller;
    private PhaseAnimator phaseAnimator;

    public void PostGameStart() {
        InputMultiplexer multiplexer = new InputMultiplexer(controller, bf, gui);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public GameScreen PostConstruct() {
        PathFinder.init();
        instance = this;
        bf = new Stage();
        initGui();

        camera = cam = new OrthographicCamera();
        cam.setToOrtho(false, 1600, 900);
        bf.getViewport().setCamera(cam);
        //gui.getViewport().setCamera(cam);
        controller = new InputController(bf, gui, cam);
        GL20 gl = Gdx.graphics.getGL20();
        gl.glEnable(GL20.GL_BLEND);
        gl.glEnable(GL20.GL_TEXTURE_2D);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch = new SpriteBatch();
        background = new Background().init();


        bindEvents();


        return this;
    }

    private void initGui() {
        textureCache = TextureManager.getCache();
        final Texture t = new Texture(GameScreen.class.getResource("/data/marble_green.png").getPath());

        radialMenu = new RadialMenu(t, textureCache);
        toolTipManager = new ToolTipManager(textureCache);
        gui = new GuiStage();
        phaseAnimator = PhaseAnimator.getInstance();
        gui.addActor(radialMenu);
        gui.addActor(toolTipManager);
//        gui.addActor(phaseAnimator);
    }

    private void bindEvents() {
        GuiEventManager.bind(GRID_CREATED, param -> {
            Pair<Integer, Integer> p = ((Pair<Integer, Integer>) param.get());
            gridPanel = new GridPanel(textureCache, p.getLeft(), p.getRight()).init();
            bf.addActor(gridPanel);
        });

        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            Triple<DC_Obj, Float, Float> container =
                    (Triple<DC_Obj, Float, Float>) obj.get();

            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                radialMenu.init(DebugRadialManager.getDebugNodes(container.getLeft()));
            } else {


                radialMenu.createNew(container.getLeft());
            }
        });
    }

    public static OrthographicCamera camera;

    public static GameScreen getInstance() {
        return instance;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }

        GuiEventManager.processEvents();

        bf.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        if (background.isDirty())
            background.update();
        batch.disableBlending();
        background.draw(batch, 1);
        batch.enableBlending();
        batch.end();

        bf.draw();

        gui.act(delta);
        gui.draw();
        try {
            if (DC_Game.game.getAnimationManager().updateAnimations())
                PhaseAnimator.getInstance().update();
        } catch (Exception e) {
        }
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
