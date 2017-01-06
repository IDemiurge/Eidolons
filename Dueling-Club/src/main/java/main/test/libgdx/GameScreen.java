package main.test.libgdx;

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
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.libgdx.Background;
import main.libgdx.GridPanel;
import main.libgdx.texture.TextureCache;
import main.libgdx.ToolTipManager;
import main.libgdx.gui.radial.RadialMenu;
import main.libgdx.texture.TextureManager;
import main.system.TempEventManager;
import main.libgdx.gui.radial.DebugRadialManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {
    private Stage bf;
    private Stage gui;

    private Background background;
    private GridPanel gridPanel;
    private TextureCache textureCache;
    private RadialMenu radialMenu;

    protected ToolTipManager toolTipManager;

    private SpriteBatch batch;
    private OrthographicCamera cam;

    private static GameScreen instance;
    private MyInputController controller;

    public void PostGameStart() {
        InputMultiplexer multiplexer = new InputMultiplexer(controller, bf, gui);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public GameScreen PostConstruct() {
        instance = this;
        bf = new Stage();
        gui = new Stage();

        camera = cam = new OrthographicCamera();
        cam.setToOrtho(false, 1600, 900);
        bf.getViewport().setCamera(cam);
        //gui.getViewport().setCamera(cam);
        controller = new MyInputController(bf, gui, cam);
        GL20 gl = Gdx.graphics.getGL20();
        gl.glEnable(GL20.GL_BLEND);
        gl.glEnable(GL20.GL_TEXTURE_2D);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch = new SpriteBatch();
        PathFinder.init();
        background = new Background().init();
        //topPanel = new DC_GDX_TopPanel(PathFinder.getImagePath()).init();
        textureCache = TextureManager.getCache();
        final Texture t = new Texture(GameScreen.class.getResource("/data/marble_green.png").getPath());
        radialMenu = new RadialMenu(t, textureCache);
        toolTipManager = new ToolTipManager(textureCache);

        gui.addActor(radialMenu);
        gui.addActor(toolTipManager);

        TempEventManager.bind("grid-created", param -> {
            Pair<Integer, Integer> p = ((Pair<Integer, Integer>) param.get());
            gridPanel = new GridPanel(textureCache, p.getLeft(), p.getRight()).init();
            bf.addActor(gridPanel);
        });

        TempEventManager.bind("create-radial-menu", obj -> {
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)){
                DebugRadialManager.show(radialMenu);
            }else {

            Triple<DC_HeroObj, Float, Float> container = (Triple<DC_HeroObj, Float, Float>) obj.get();
            radialMenu.createNew(container.getLeft());
            }
        });

        return this;
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

        TempEventManager.processEvents();

        bf.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        if (background.isDirty())
            background.update();
        background.draw(batch, 1);
        batch.end();

        bf.draw();

        gui.act(delta);
        gui.draw();
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
}
