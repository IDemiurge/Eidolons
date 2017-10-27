package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.DialogScenario;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.BFDataCreatedEvent;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridMaster;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.BattleGuiStage;
import main.libgdx.stage.ChainedStage;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.audio.DC_SoundMaster;
import main.system.launch.CoreEngine;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends ScreenWithLoader {
    public static OrthographicCamera camera;
    private static DungeonScreen instance;
    private static boolean cameraAutoCenteringOn =  OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.AUTO_CAMERA);
    private Stage gridStage;
    private BattleGuiStage guiStage;
    private GridPanel gridPanel;
    private ChainedStage dialogsStage = null;
    private OrthographicCamera cam;
    private InputController controller;
    private TextureRegion backTexture;
    private Vector2 velocity;
    private DC_SoundMaster soundMaster;
    private Vector2 cameraDestination;
    private RealTimeGameLoop realTimeGameLoop;
    private ParticleManager particleManager;

    public static DungeonScreen getInstance() {
        return instance;
    }

    public static boolean isCameraAutoCenteringOn() {
        return cameraAutoCenteringOn;
    }

    public static void setCameraAutoCenteringOn(boolean b) {
        cameraAutoCenteringOn = b;
    }

    @Override
    protected void preLoad() {
        instance = this;
        super.preLoad();

        gridStage = new Stage(viewPort, getBatch());

        guiStage = new BattleGuiStage(null, getBatch());


        GL30 gl = Gdx.graphics.getGL30();
        if (gl != null) {
            gl.glEnable(GL30.GL_BLEND);
            gl.glEnable(GL30.GL_TEXTURE_2D);
            gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GL20 gl20 = Gdx.graphics.getGL20();
            gl20.glEnable(GL20.GL_BLEND);
            gl20.glEnable(GL20.GL_TEXTURE_2D);
            gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }


        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
            if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
                TextureManager.initBackgroundCache(backTexture);
            }

        });

        GuiEventManager.bind(DIALOG_SHOW, obj -> {
            DialogueHandler handler = (DialogueHandler) obj.get();
            final List<DialogScenario> list = handler.getList();
            if (dialogsStage == null) {
                dialogsStage = new ChainedStage(viewPort, getBatch(), list);
                updateInputController();
            } else {
                dialogsStage.play(list);
            }
            dialogsStage.setDialogueHandler(handler);
        });
    }

    @Override
    protected void afterLoad() {
        if (CoreEngine.isGraphicTestMode()) {
            Gdx.app.log("DungeonScreen::afterLoad()", "-- Start!");
        }
        cam = camera = (OrthographicCamera) viewPort.getCamera();
        controller = new InputController(cam);

        soundMaster = new DC_SoundMaster(this);
        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH()).init(param.getObjects());
        gridStage.addActor(gridPanel);
        particleManager = new ParticleManager();
        gridStage.addActor(particleManager.getEmitterMap());
//        GuiEventManager.bind(GuiEventType.BF_CREATED, p -> {
        try {
            controller.setDefaultPos();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        });
        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED, p -> {
            if (isCameraAutoCenteringOn()) {
                Coordinates coordinatesActiveObj = DC_Game.game.getManager().getActiveObj().getCoordinates();
                Vector2 unitPosition = new Vector2(coordinatesActiveObj.x * GridConst.CELL_W + GridConst.CELL_W / 2, (gridPanel.getRows() - coordinatesActiveObj.y) * GridConst.CELL_H - GridConst.CELL_H / 2);
                cameraPan(unitPosition);
            }
        });
        GuiEventManager.bind(UPDATE_GUI, obj -> {
            updateGui();
        });
        if (CoreEngine.isGraphicTestMode()) {
            Gdx.app.log("DungeonScreen::afterLoad()", "-- End!");
        }
        try {

            Unit unit = null;
            if (Eidolons.game.getMetaMaster() == null) {
                unit = Eidolons.game.getMetaMaster().
                 getPartyManager().getParty().getLeader();
            } else
                unit = (Unit) Eidolons.game.getPlayer(true).getHeroObj();
            Vector2 unitPosition =
             GridMaster.getCenteredPos(
              unit.getCoordinates());

            cameraPan(unitPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateGui() {
//        gridPanel.updateGui();
        guiStage.getBottomPanel().update();

    }

    private void cameraPan(Vector2 unitPosition) {
        this.cameraDestination = unitPosition;
        float dest = cam.position.dst(unitPosition.x, unitPosition.y, 0f) / getCameraDistanceFactor();
        if (dest < getCameraMinCameraPanDist())
            return;
        velocity = new Vector2(unitPosition.x - cam.position.x, unitPosition.y - cam.position.y).nor().scl(Math.min(cam.position.dst(unitPosition.x, unitPosition.y, 0f), dest));
        if (CoreEngine.isGraphicTestMode()) {
//            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- coordinatesActiveObj:" + coordinatesActiveObj);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- unitPosition:" + unitPosition);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- dest:" + dest);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- velocity:" + velocity);
        }
    }

    private float getCameraMinCameraPanDist() {
        return 140;
    }

    private float getCameraDistanceFactor() {
        return 3f;
    }

    @Override
    protected InputMultiplexer getInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, gridStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
            current.addProcessor(new GestureDetector(controller)); // TODO коряво:D пересикается с обычным контролером
        } else {
            current = super.getInputController();
        }

        return current;
    }

    @Override
    public void render(float delta) {
        if (DC_Game.game != null)
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }
        super.render(delta);
        if (!hideLoader)
            return;
        guiStage.act(delta);
        gridStage.act(delta);

        cameraShift();
        //cam.update();
        if (canShowScreen()) {
            if (DC_Game.game != null)
            if (DC_Game.game.getGameLoop() instanceof  RealTimeGameLoop) {
//              if (realTimeGameLoop != null)        realTimeGameLoop.act(delta);
                ((RealTimeGameLoop) Eidolons.game.getGameLoop()).act(delta);
            }

            if (backTexture != null) {
                if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
                    TextureManager.drawFromSpriteCache(TextureManager.getBackgroundId());
                } else {
                    guiStage.getBatch().begin();
                    float colorBits = GdxColorMaster.WHITE.toFloatBits();
                    if (guiStage.getBatch().getColor().toFloatBits() != colorBits)
                        guiStage.getBatch().setColor(colorBits); //damned alpha...
                    guiStage.getBatch().draw(backTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    guiStage.getBatch().end();
                }
            }

            gridStage.draw();


            guiStage.draw();

            if (dialogsStage != null) {
                dialogsStage.act(delta);
                if (dialogsStage.isDone()) {
                    final ChainedStage dialogsStage = this.dialogsStage;
                    this.dialogsStage = null;
                    dialogsStage.dispose();
                    updateInputController();
                } else {
                    dialogsStage.draw();
                }
            }

            if (gridPanel.getFpsLabel() != null)
                if (gridPanel != null)
                    gridPanel.getFpsLabel().setText(new Float(1 / delta) + "");

            try {
                soundMaster.doPlayback(delta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cameraShift() {
        if (cameraDestination != null)
            if (cam != null && velocity != null && !velocity.isZero()) {
                try {
                    float x =velocity.x>0
                     ? Math.min( cameraDestination.x, cam.position.x+velocity.x * Gdx.graphics.getDeltaTime())
                     : Math.max( cameraDestination.x, cam.position.x+velocity.x * Gdx.graphics.getDeltaTime());
                    float y =velocity.y>0
                     ? Math.min( cameraDestination.y, cam.position.y+velocity.y * Gdx.graphics.getDeltaTime())
                     : Math.max( cameraDestination.y, cam.position.y+velocity.y * Gdx.graphics.getDeltaTime());
                    cam.position.set(x, y, 0f);
                    float dest = cam.position.dst(cameraDestination.x, cameraDestination.y, 0f) / getCameraDistanceFactor();
                    Vector2 velocityNow = new Vector2(cameraDestination.x - cam.position.x, cameraDestination.y - cam.position.y).nor().scl(Math.min(cam.position.dst(cameraDestination.x, cameraDestination.y, 0f), dest));
                    if (CoreEngine.isGraphicTestMode()) {
                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity:" + velocity);
                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocityNow:" + velocityNow);
                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity.hasOppositeDirection(velocityNow):" + velocity.hasOppositeDirection(velocityNow));
                    }
                    if (velocity.hasOppositeDirection(velocityNow)) {
                        cameraStop();
                    }
                } catch (Exception exp) {
                    if (CoreEngine.isGraphicTestMode())
                        Gdx.app.log("DungeonScreen::cameraShift()", "-- exp:" + exp);
                }
                cam.update();
            }
    }

    public void cameraStop() {
        if (velocity != null)
            velocity.setZero();
    }

    @Override
    public void resize(int width, int height) {
/*        animationEffectStage.getViewport().update(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);*/
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public InputController getController() {
        return controller;
    }

    public Stage getGridStage() {
        return gridStage;
    }


    public RealTimeGameLoop getRealTimeGameLoop() {
        return realTimeGameLoop;
    }

    public void setRealTimeGameLoop(RealTimeGameLoop realTimeGameLoop) {
        this.realTimeGameLoop = realTimeGameLoop;
    }
}
