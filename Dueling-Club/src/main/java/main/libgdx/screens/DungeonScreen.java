package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.BFDataCreatedEvent;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridMaster;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.bf.mouse.DungeonInputController;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.launch.GenericLauncher;
import main.libgdx.shaders.DarkShader;
import main.libgdx.stage.BattleGuiStage;
import main.libgdx.stage.ChainedStage;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.audio.DC_SoundMaster;
import main.system.launch.CoreEngine;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends GameScreen {
    private static float FRAMERATE_DELTA_CONTROL =
     new Float(1) / GenericLauncher.FRAMERATE * 3;
    private static DungeonScreen instance;
    private static boolean cameraAutoCenteringOn = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.AUTO_CAMERA);


    private ParticleManager particleManager;
    private Stage gridStage;
    private BattleGuiStage guiStage;
    private GridPanel gridPanel;

    public static void setFramerateDeltaControl(float framerateDeltaControl) {
        FRAMERATE_DELTA_CONTROL = framerateDeltaControl;
    }

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
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GUI_READY);
        super.preLoad();
        gridStage = new Stage(viewPort, getBatch());

        guiStage = new BattleGuiStage(null, getBatch());

        initGl();

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
            if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
                TextureManager.initBackgroundCache(backTexture);
            }

        });

        initDialogue();

        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    }


    @Override
    public void dispose() {
        super.dispose();
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    }

    protected void checkGraphicsUpdates() {
        if (backTexture == null) {
            String path = null;
            try {
                path = DC_Game.game.getDungeonMaster().getDungeonWrapper().getMapBackground();
                backTexture = getOrCreateR(path);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
//        TODO Unit obj = DC_Game.game.getManager().getActiveObj();
//        if (obj.isMine()) {
//            if (guiStage.getBottomPanel().getY()< ){
//
//            }
//        }
    }

    @Override
    protected void afterLoad() {

        cam = (OrthographicCamera) viewPort.getCamera();
        controller = new DungeonInputController(cam);
        particleManager = new ParticleManager();

        soundMaster = new DC_SoundMaster(this);
        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH()).init(param.getObjects());
        gridStage.addActor(gridPanel);
        gridStage.addActor(particleManager.getEmitterMap());
        try {
            controller.setDefaultPos();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        bindEvents();

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
            main.system.ExceptionMaster.printStackTrace(e);
        }


        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }

    private void bindEvents() {


    }

    public void updateGui() {
//        gridPanel.updateGui();
        guiStage.getBottomPanel().update();
        checkGraphicsUpdates();
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
        return 220; //TODO if too close to the edge also
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
            current.addProcessor(new GestureDetector(controller));
        } else {
            current = super.getInputController();
        }

        return current;
    }

    protected void selectionPanelClosed() {
        super.selectionPanelClosed();
        getOverlayStage().setActive(false);
    }

    protected void triggerInitialEvents() {
//        DC_Game.game.getVisionMaster().triggerGuiEvents();
        GuiEventManager.trigger(UPDATE_LIGHT);
    }

    public void renderMain(float delta) {

        guiStage.act(delta);
        gridStage.act(delta);


        cameraShift();
        //cam.update();
        if (canShowScreen()) {
            if (DC_Game.game != null)
                if (DC_Game.game.getGameLoop() instanceof RealTimeGameLoop) {
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
                    guiStage.getBatch().draw(backTexture, 0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
                    guiStage.getBatch().end();
                }
            }
            gridStage.setDebugAll(false);
            gridStage.draw();


            guiStage.setDebugAll(false);
            guiStage.draw();

            if (dialogsStage != null) {
                dialogsStage.setDebugAll(false);
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
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    @Override
    public void render(float delta) {


        if (delta > FRAMERATE_DELTA_CONTROL) {
            try {
                Thread.sleep((long) (delta - FRAMERATE_DELTA_CONTROL));
            } catch (InterruptedException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            delta = FRAMERATE_DELTA_CONTROL;
        }
        if (speed != null) {
            delta = delta * speed;
        }
        if (DC_Game.game != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
            } else {
                if (Gdx.input.isKeyJustPressed(Keys.CONTROL_RIGHT)) {
                    DC_Game.game.getVisionMaster().refresh();
                    GuiEventManager.trigger(UPDATE_GUI);
                }
            }
            super.render(delta);

        }
    }
    protected void checkShaderReset() {
        if (batch.getShader() == DarkShader.getShader())
            batch.setShader(bufferedShader);
    }

    protected void checkShader() {

        if (batch.getShader() != DarkShader.getShader())
            if (isBlocked() || ExplorationMaster.isWaiting()) {
                bufferedShader = batch.getShader();
                batch.setShader(DarkShader.getShader());
            }

    }

    public boolean isBlocked() {
        return OptionsMaster.isMenuOpen() || GameMenu.menuOpen;
    }

    private void cameraShift() {
        if (cameraDestination != null)
            if (cam != null && velocity != null && !velocity.isZero()) {
                try {
                    float x = velocity.x > 0
                     ? Math.min(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime())
                     : Math.max(cameraDestination.x, cam.position.x + velocity.x * Gdx.graphics.getDeltaTime());
                    float y = velocity.y > 0
                     ? Math.min(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime())
                     : Math.max(cameraDestination.y, cam.position.y + velocity.y * Gdx.graphics.getDeltaTime());
                    cam.position.set(x, y, 0f);
                    float dest = cam.position.dst(cameraDestination.x, cameraDestination.y, 0f) / getCameraDistanceFactor();
                    Vector2 velocityNow = new Vector2(cameraDestination.x - cam.position.x, cameraDestination.y - cam.position.y).nor().scl(Math.min(cam.position.dst(cameraDestination.x, cameraDestination.y, 0f), dest));
//                    if (CoreEngine.isGraphicTestMode()) {
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- pos x:" + x);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- pos y:" + y);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity:" + velocity);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocityNow:" + velocityNow);
//                        Gdx.app.log("DungeonScreen::cameraShift()", "-- velocity.hasOppositeDirection(velocityNow):" + velocity.hasOppositeDirection(velocityNow));
//                    }
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
//     animationEffectStage.getViewport().update(width, height);

        gridStage.getRoot().setSize(width, height);
//        guiStage.getRoot().setSize(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public BattleGuiStage getGuiStage() {
        return guiStage;
    }

    public InputController getController() {
        return controller;
    }

    public Stage getGridStage() {
        return gridStage;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public void activeUnitSelected(BattleFieldObject hero) {
        if (isCameraAutoCenteringOn()) {
            Coordinates coordinatesActiveObj =
             hero.getCoordinates();
            Vector2 unitPosition = new Vector2(coordinatesActiveObj.x * GridConst.CELL_W + GridConst.CELL_W / 2, (gridPanel.getRows() - coordinatesActiveObj.y) * GridConst.CELL_H - GridConst.CELL_H / 2);
            cameraPan(unitPosition);
        }
    }
}
