package main.libgdx.screens.map;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import main.data.xml.XML_Reader;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.GdxMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.mouse.MapInputController;
import main.libgdx.gui.menu.selection.SelectionPanel;
import main.libgdx.screens.GameScreen;
import main.libgdx.screens.map.obj.PartyActor;
import main.libgdx.screens.map.obj.PartyActorFactory;
import main.libgdx.screens.map.obj.PlaceActor;
import main.libgdx.screens.map.obj.PlaceActorFactory;
import main.libgdx.shaders.DarkShader;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.MapEvent.*;

/**
 * Created by JustMe on 2/3/2018.
 */
public class MapScreen extends GameScreen {

    public final static String defaultPath = "global\\Ersidris Print mixed no text.png";
    private static MapScreen instance;
    //    private RealTimeGameLoop realTimeGameLoop;
    private ParticleManager particleManager;
    private MapGuiStage guiStage;
    private Stage objectStage;
    private  Stage mapStage;
    private Image map;

    private MapScreen() {

    }

    public static MapScreen getInstance() {
        if (instance == null) {
            instance = new MapScreen();
        }
        return instance;
    }

    @Override
    protected void preLoad() {
        guiStage = new MapGuiStage( new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
        objectStage = new Stage(viewPort, getBatch());
        mapStage = new  Stage(viewPort, getBatch());
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
        });
        new Thread(() -> {
            XML_Reader.readTypes(true);
            MacroManager.newGame();
        }, " thread").start();

//        GuiEventManager.trigger(SHOW_SELECTION_PANEL,
//         DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
//          StringMaster.getWellFormattedString(item.toString())));

//        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
//        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }

    @Override
    protected void afterLoad() {
        GuiEventManager.trigger(UPDATE_MAP_BACKGROUND, defaultPath);
        cam = (OrthographicCamera) viewPort.getCamera();
        controller = new MapInputController(cam);
//        particleManager = new ParticleManager();
        bindEvents();

        GuiEventManager.trigger(MAP_READY);
    }

    private void bindEvents() {

        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            PartyActor partyActor = PartyActorFactory.get(party);
            objectStage.addActor(partyActor);
        });
        GuiEventManager.bind(CREATE_PLACE, param -> {
            Place place = (Place) param.get();
            PlaceActor placeActor = PlaceActorFactory.get(place);
            objectStage.addActor(placeActor);
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
//                batch.setShader(VignetteShader.getShader());
            }
        }

    }

    private boolean isBlocked() {
        return guiStage.getGameMenu().isVisible();
    }
}
