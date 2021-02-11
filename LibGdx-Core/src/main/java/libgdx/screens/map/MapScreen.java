package libgdx.screens.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import eidolons.game.core.Eidolons;
import libgdx.GdxMaster;
import eidolons.content.consts.GridCreateData;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.mouse.InputController;
import libgdx.bf.mouse.MapInputController;
import libgdx.gui.menu.selection.SelectionPanel;
import libgdx.screens.dungeon.GameScreenWithTown;
import libgdx.screens.map.editor.EditorMapView;
import libgdx.shaders.DarkShader;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.MacroGame;
import eidolons.macro.global.time.MacroTimeMaster;
import main.data.xml.XML_Reader;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.launch.Flags;

import static main.system.MapEvent.MAP_READY;
import static main.system.MapEvent.UPDATE_MAP_BACKGROUND;

/**
 * Created by JustMe on 2/3/2018.
 */
public class MapScreen extends GameScreenWithTown {

    public final static String defaultPath = "global/map/ersidris plain.jpg";
    public final static String timeVersionRootPath = "global/map/ersidris at ";
    public static final int defaultSize = 2988;
    protected static MapScreen instance;
    //    protected RealTimeGameLoop realTimeGameLoop;
    protected MapObjStage objectStage;
    protected MapStage mapStage;
    private boolean loaded;
    private boolean preloaded;

    protected MapScreen() {

    }

    public static MapScreen getInstance() {
        if (Flags.isMapEditor())
            return EditorMapView.getInstance();
        if (instance == null) {
            instance = new MapScreen();
        }
        return instance;
    }

    @Override
    protected String getLoadScreenPath() {
        return null;
    }

    public void centerCamera() {
//        Coordinates coordinatesActiveObj =
//         objectStage.getMainParty().getCoordinates();
//        Vector2 unitPosition = new Vector2(coordinatesActiveObj.x, coordinatesActiveObj.y);
//        cameraPan(unitPosition);
    }


    @Override
    protected void preLoad() {
        if (preloaded)
            return;
        guiStage = createGuiStage();
        objectStage = new MapObjStage(viewPort, getBatch());
        mapStage = new MapStage(viewPort, getBatch());
        super.preLoad();
        initGl();
        String saveName = (String) data.getParameter();
        Eidolons.onThisOrNonGdxThread(() -> {

            XML_Reader.readTypes(true);
            AdventureInitializer.newAdventureGame(saveName);
            preloaded = true;
        });

    }


    protected MapGuiStage createGuiStage() {
        return new MapGuiStage(new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    @Override
    protected GridPanel createGrid(GridCreateData param) {
        return null;
    }

    @Override
    protected void afterLoad() {
        if (loaded) {
            GuiEventManager.trigger(MAP_READY);
            return; //fix this!
        }
        GuiEventManager.trigger(UPDATE_MAP_BACKGROUND, defaultPath);
        setCam((OrthographicCamera) viewPort.getCamera());
        controller = initController();
//        particleManager = new ParticleManager();
        bindEvents();

        GuiEventManager.trigger(MAP_READY);
        loaded = true;
    }

    protected InputController initController() {
        return new MapInputController(getCam());
    }

    protected void bindEvents() {

    }

    @Override
    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        return super.createSelectionPanel(p);
    }

    @Override
    protected Stage getMainStage() {
        return guiStage;
    }

    @Override
    protected InputProcessor createInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = GdxMaster.getMultiplexer(guiStage, controller, objectStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
            current.addProcessor(controller);//new GestureDetector(controller));
        } else {
            current = GdxMaster.getMultiplexer(new MapInputController(cameraMan.getCam()));
        }

        return current;
    }

    @Override
    protected boolean isWaitForInputSupported() {
        return false;
    }
    /*


     */

    @Override
    protected boolean canShowScreen() {
        if (MacroGame.getGame() == null)
            return false;
        if (!Flags.isMapEditor())
            if (!MacroGame.getGame().isStarted())
                return false;
        return super.canShowScreen();
    }

    public void renderMain(float delta) {
//        VignetteShader.getShader().begin();
//        getBatch().setShader(VignetteShader.getShader());
        if (canShowScreen()) {
            if (!Flags.isMapEditor()) {
                MacroGame.getGame().getRealtimeLoop().act(delta);
                cameraMan.act(delta);
            }
            delta =
             delta + 0.1f * delta * (getTimeMaster().getSpeed() - 1);
            mapStage.act(delta);
            objectStage.act(delta);
            guiStage.act(delta);

            mapStage.draw();
            if (Gdx.input.isKeyPressed(Keys.O) || Flags.isMapEditor()) {
                objectStage.draw();
            }
            if (!Flags.isFootageMode())
            if (Gdx.input.isKeyPressed(Keys.G)|| Flags.isMapEditor() ) {
                guiStage.draw();
            } else {
//                getBatch().begin();
//                try{}catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);} guiStage.getVignette().draw(getBatch(), 1f);
//                getBatch().end();
            }
        }

//        VignetteShader.getShader().end();
    }

    private MacroTimeMaster getTimeMaster() {
        return MacroGame.getGame().getLoop().getTimeMaster();
    }

    protected void checkShaderReset() {
        if (batch.getShader() == DarkShader.getDarkShader())
            batch.setShader(bufferedShader);
    }

    protected void resetShader() {

        // if (batch.getShader() != DarkShader.getDarkShader()) {
            // bufferedShader = batch.getShader();
            // if (isBlocked() || ExplorationMaster.isWaiting()) {
            //     batch.setFluctuatingShader(DarkShader.getInstance());
            // } else {
            // }
        // }

    }

    @Override
    protected boolean isTownInLoaderOnly() {
        return false;
    }
    protected boolean isBlocked() {
        if (!canShowScreen())
            return false;

        if (Flags.isMapEditor())
            return false;
        if (getTimeMaster().isPlayerCamping())
            return true;
        return getGuiStage().getGameMenu().isVisible();
    }

    public MapObjStage getObjectStage() {
        return objectStage;
    }

    public MapStage getMapStage() {
        return mapStage;
    }

    public MapGuiStage getGuiStage() {
        return (MapGuiStage) guiStage;
    }

    public InputController getController() {
        return controller;
    }

    @Override
    public GridPanel getGridPanel() {
        return null;
    }

    public int getMapWidth() {
        return (int) getMapStage().getMap().getWidth();
    }
}
