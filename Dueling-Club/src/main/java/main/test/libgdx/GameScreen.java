package main.test.libgdx;

import com.badlogic.gdx.Gdx;
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
import main.libgdx.*;
import main.system.TempEventManager;
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
    private DC_GDX_Background background;
    private DC_GDX_TopPanel topPanel;
    private GridPanel gridPanel;
    private DC_GDX_TargetUnitInfoPanel unitInfoPanel;
    private DC_GDX_ActiveUnitInfoPanel activeUnitInfoPanel;
    private DC_GDX_ActionGroup actionGroup;
    private TextureCache textureCache;
    private RadialMenu radialMenu;

    protected ToolTipManager toolTipManager;

    private SpriteBatch batch;
    private OrthographicCamera cam;

    public GameScreen PostConstruct() {
        bf = new Stage();
        gui = new Stage();
        camera = cam = new OrthographicCamera();
        bf.getViewport().setCamera(cam);
        //gui.getViewport().setCamera(cam);
        MyInputController controller = new MyInputController(cam);
        cam.setToOrtho(false, 1600, 900);
        GL20 gl = Gdx.graphics.getGL20();
        gl.glEnable(GL20.GL_BLEND);
        gl.glEnable(GL20.GL_TEXTURE_2D);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch = new SpriteBatch();
        PathFinder.init();
        background = new DC_GDX_Background(PathFinder.getImagePath()).init();
        //topPanel = new DC_GDX_TopPanel(PathFinder.getImagePath()).init();
        textureCache = new TextureCache(PathFinder.getImagePath());
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
            Triple<DC_HeroObj, Float, Float> container = (Triple<DC_HeroObj, Float, Float>) obj.get();
            radialMenu.createNew(container.getMiddle(), container.getRight(), container.getLeft());
        });

//
//        unitInfoPanel = new DC_GDX_TargetUnitInfoPanel(PathFinder.getImagePath()).init();
//        unitInfoPanel.setX(Gdx.graphics.getWidth() - unitInfoPanel.getWidth());
//        unitInfoPanel.setY(Gdx.graphics.getHeight() - unitInfoPanel.getHeight());
//
//        activeUnitInfoPanel = new DC_GDX_ActiveUnitInfoPanel(PathFinder.getImagePath()).init();
//        activeUnitInfoPanel.setX(0);
//        activeUnitInfoPanel.setY(Gdx.graphics.getHeight() - activeUnitInfoPanel.getHeight());
//
//        actionGroup = new DC_GDX_ActionGroup(PathFinder.getImagePath()).init();
//        actionGroup.setY(10);
//        actionGroup.setX(Gdx.graphics.getWidth() / 2 - actionGroup.getWidth() / 2);

        //gridPanel.setY(actionGroup.getY()+actionGroup.getHeight());
        //gridPanel.setX(activeUnitInfoPanel.getMinWeight());
        InputMultiplexer multiplexer = new InputMultiplexer(controller, bf, gui);
        Gdx.input.setInputProcessor(multiplexer);
        return this;
    }

    public static OrthographicCamera camera;

    @Override
    public void render(float delta) {
        TempEventManager.processEvents();

        bf.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
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
}
