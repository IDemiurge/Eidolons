package eidolons.libgdx.screens.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import eidolons.game.module.adventure.MacroManager;
import eidolons.game.module.adventure.MacroTimeMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.shaders.DarkShader;
import main.data.xml.XML_Reader;
import main.game.bf.Coordinates;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.mouse.MapInputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.map.editor.EditorMapView;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.launch.CoreEngine;

import static main.system.MapEvent.MAP_READY;
import static main.system.MapEvent.UPDATE_MAP_BACKGROUND;

/**
 * Created by JustMe on 2/3/2018.
 */
public class MapScreen extends GameScreen {

    public final static String defaultPath = "global\\map\\ersidris plain.jpg";
    public final static String timeVersionRootPath = "global\\map\\ersidris at ";
    public static final int defaultSize = 2988;
    protected static MapScreen instance;
    //    protected RealTimeGameLoop realTimeGameLoop;
    protected MapGuiStage guiStage;
    protected MapObjStage objectStage;
    protected MapStage mapStage;
    private boolean loaded;
    private boolean preloaded;

    protected MapScreen() {

    }

    public static MapScreen getInstance() {
        if (CoreEngine.isMapEditor())
            return EditorMapView.getInstance();
        if (instance == null) {
            instance = new MapScreen();
        }
        return instance;
    }

    public void centerCamera() {
        Coordinates coordinatesActiveObj =
         objectStage.getMainParty().getCoordinates();
        Vector2 unitPosition = new Vector2(coordinatesActiveObj.x, coordinatesActiveObj.y);
        cameraPan(unitPosition);
    }

    @Override
    protected void cameraPan(Vector2 unitPosition) {
        super.cameraPan(unitPosition);
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
        initDialogue();

        new Thread(() -> {
            //if
            XML_Reader.readTypes(true);
            MacroManager.newGame();
            preloaded = true;
        }, "macro init thread").start();

//        GuiEventManager.trigger(SHOW_SELECTION_PANEL,
//         DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
//          StringMaster.getWellFormattedString(item.toString())));

//        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
//        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }


    protected MapGuiStage createGuiStage() {
        return new MapGuiStage(new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    @Override
    protected void afterLoad() {
        if (loaded) {
            GuiEventManager.trigger(MAP_READY);
            return; //fix this!
        }
        GuiEventManager.trigger(UPDATE_MAP_BACKGROUND, defaultPath);
        cam = (OrthographicCamera) viewPort.getCamera();
        controller = initController();
//        particleManager = new ParticleManager();
        bindEvents();

        GuiEventManager.trigger(MAP_READY);
        loaded = true;
    }

    protected InputController initController() {
        return new MapInputController(cam);
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
    protected InputMultiplexer getInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, objectStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
            current.addProcessor(controller);//new GestureDetector(controller));
        } else {
            current = super.getInputController();
        }

        return current;
    }

    @Override
    protected boolean isWaitForInput() {
        return false;
    }
    /*


     */

    @Override
    protected boolean canShowScreen() {
        if (MacroGame.getGame() == null)
            return false;
        if (!CoreEngine.isMapEditor())
            if (!MacroGame.getGame().isStarted())
                return false;
        return super.canShowScreen();
    }

    public void renderMain(float delta) {
//        VignetteShader.getShader().begin();
//        getBatch().setShader(VignetteShader.getShader());
        if (canShowScreen()) {
            if (!CoreEngine.isMapEditor()) {
                MacroGame.getGame().getRealtimeLoop().act(delta);
                cameraShift();
            }
            delta =
             delta + 0.1f * delta * (getTimeMaster().getSpeed() - 1);
            mapStage.act(delta);
            objectStage.act(delta);
            guiStage.act(delta);

            mapStage.draw();
            if (!Gdx.input.isKeyPressed(Keys.O)) {
                objectStage.draw();
            }
            if (!Gdx.input.isKeyPressed(Keys.G)) {
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
        if (batch.getShader() == DarkShader.getShader())
            batch.setShader(bufferedShader);
    }

    protected void checkShader() {

        if (batch.getShader() != DarkShader.getShader()) {
            bufferedShader = batch.getShader();
            if (isBlocked() || ExplorationMaster.isWaiting()) {
                batch.setShader(DarkShader.getShader());
            } else {
            }
        }

    }

    protected boolean isBlocked() {
        if (!canShowScreen())
            return false;

        if (CoreEngine.isMapEditor())
            return false;
        if (getTimeMaster().isPlayerCamping())
            return true;
        return guiStage.getGameMenu().isVisible();
    }

    public MapObjStage getObjectStage() {
        return objectStage;
    }

    public MapStage getMapStage() {
        return mapStage;
    }

    public MapGuiStage getGuiStage() {
        return guiStage;
    }

    public InputController getController() {
        return controller;
    }

    public int getMapWidth() {
        return (int) getMapStage().getMap().getWidth();
    }
}
