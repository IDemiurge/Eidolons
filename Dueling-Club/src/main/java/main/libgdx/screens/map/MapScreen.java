package main.libgdx.screens.map;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.xml.XML_Reader;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.GdxMaster;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.bf.mouse.MapInputController;
import main.libgdx.gui.menu.selection.SelectionPanel;
import main.libgdx.screens.GameScreen;
import main.libgdx.screens.map.editor.EditorMapView;
import main.libgdx.screens.map.obj.*;
import main.libgdx.screens.map.sfx.MapAlphaLayers;
import main.libgdx.screens.map.sfx.MapMoveLayers;
import main.libgdx.screens.map.sfx.MapParticles;
import main.libgdx.shaders.DarkShader;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.launch.CoreEngine;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.MapEvent.*;

/**
 * Created by JustMe on 2/3/2018.
 */
public class MapScreen extends GameScreen {

    public final static String defaultPath = "global\\map\\ersidris plain.jpg";
    private static MapScreen instance;
    //    private RealTimeGameLoop realTimeGameLoop;
    private MapParticles particleManager;
    protected MapGuiStage guiStage;
    private Stage objectStage;
    private  Stage mapStage;
    private Image map;
    private MapMoveLayers moveLayerMaster;

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

    @Override
    protected void preLoad() {
        guiStage =createGuiStage();
        objectStage = new Stage(viewPort, getBatch());
        mapStage = new  Stage(viewPort, getBatch());
        new MapAlphaLayers(mapStage).init();
        new MapParticles(mapStage).init();
//        emitterMap = new MacroEmitterMap();
        super.preLoad();
        initGl();
        initDialogue();
        GuiEventManager.bind(UPDATE_MAP_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
            if (map != null)
                map.remove();
            map = new Image(backTexture);
            mapStage.addActor(map);
            map.setZIndex(0);
            moveLayerMaster = new MapMoveLayers(backTexture.getRegionWidth(),
             backTexture.getRegionHeight());
            mapStage.addActor(moveLayerMaster);
            moveLayerMaster.setTime(DAY_TIME.NOON);
        });

        new Thread(() -> {
            //if
            XML_Reader.readTypes(true);
            MacroManager.newGame();
        }, " thread").start();

//        GuiEventManager.trigger(SHOW_SELECTION_PANEL,
//         DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
//          StringMaster.getWellFormattedString(item.toString())));

//        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
//        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }

    protected MapGuiStage createGuiStage() {
        return new MapGuiStage( new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    @Override
    protected void afterLoad() {
        GuiEventManager.trigger(UPDATE_MAP_BACKGROUND, defaultPath);
        cam = (OrthographicCamera) viewPort.getCamera();
        controller =initController();
//        particleManager = new ParticleManager();
        bindEvents();

        GuiEventManager.trigger(MAP_READY);
    }

    protected InputController initController() {
        return new MapInputController(cam);
    }

    private void bindEvents() {

        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            if (party == null) {
                return ;
            }
            PartyActor partyActor = PartyActorFactory.getParty(party);
            objectStage.addActor(partyActor);
            if (party.isMine()) {
                guiStage.setParty(party);
            }
        });
        GuiEventManager.bind(CREATE_PLACE, param -> {
            Place place = (Place) param.get();
            PlaceActor placeActor = PlaceActorFactory.getPlace(place);
            objectStage.addActor(placeActor);
        });
        GuiEventManager.bind(REMOVE_MAP_OBJ, param -> {
            MapActor actor = (MapActor) param.get();
            actor.remove();
        });
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
            current.addProcessor(new GestureDetector(controller));
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

    public void renderMain(float delta) {
//        VignetteShader.getShader().begin();
//        getBatch().setShader(VignetteShader.getShader());
        if (canShowScreen()) {
            mapStage.act(delta);
            objectStage.act(delta);
            guiStage.act(delta);

            mapStage.draw();
            objectStage.draw();
            guiStage.draw();
        }
//        VignetteShader.getShader().end();
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

    private boolean isBlocked() {
        if (CoreEngine.isMapEditor())
            return false;
        return guiStage.getGameMenu().isVisible();
    }
}
