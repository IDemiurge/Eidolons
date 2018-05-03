package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.particles.ParticleManager;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends GameScreen {
    protected static float FRAMERATE_DELTA_CONTROL =
     new Float(1) / GenericLauncher.FRAMERATE * 3;
    protected static DungeonScreen instance;
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.AUTO_CAMERA);


    protected ParticleManager particleManager;
    protected StageX gridStage;
    protected GridPanel gridPanel;

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
        gridStage = new StageX(viewPort, getBatch());

        guiStage = new BattleGuiStage(null, getBatch());

        initGl();

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            setBackground(path);


        });

        initDialogue();

        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    }

    @Override
    public void reset() {
        super.reset();
        getOverlayStage().setActive(true);
    }

    protected void bindEvents() {

        GuiEventManager.bind(BATTLE_FINISHED, param -> {
            DC_Game.game.getLoop().setExited(true); //cleanup on real exit
            if (!ExplorationMaster.isExplorationOn()) {
                DC_Game.game.getLoop().actionInput(null);
            }

            if (MacroGame.getGame() != null) {
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                 new ScreenData(ScreenType.MAP, ""));

                MacroGame.getGame().getLoop().setPaused(false);
                MacroGame.getGame().getLoop().signal();
                main.system.auxiliary.log.LogMaster.log(1, " returning to the map...");
            }
        });

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
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            setBackground(path);
        }
//        TODO Unit obj = DC_Game.game.getManager().getActiveObj();
//        if (obj.isMine()) {
//            if (guiStage.getBottomPanel().getY()< ){
//
//            }
//        }
    }

    private void setBackground(String path) {
        TextureRegion texture = getOrCreateR(path);
        if (texture.getTexture() != TextureCache.getEmptyTexture())
            backTexture = texture;

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
            TextureManager.initBackgroundCache(backTexture);
        }
    }

    @Override
    protected void afterLoad() {
        try {
            cam = (OrthographicCamera) viewPort.getCamera();
            controller = new DungeonInputController(cam);
            particleManager = new ParticleManager();

            soundMaster = new DC_SoundMaster(this);
            final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
            gridPanel = new GridPanel(param.getGridW(), param.getGridH()).init(param.getObjects());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        gridStage.addActor(gridPanel);
        gridStage.addActor(particleManager);
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


    public void updateGui() {
//        gridPanel.updateGui();
        getGuiStage().getBottomPanel().update();
        checkGraphicsUpdates();
    }

    @Override
    protected InputMultiplexer getInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, gridStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
            current.addProcessor(controller);//new GestureDetector(controller));
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

    @Override
    protected float getCameraDistanceFactor() {
        return 5f;
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

        }
        super.render(delta);
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
        return
         HqPanel.getActiveInstance()!=null || OptionsWindow.isActive()
         || GameMenu.menuOpen;
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
        return (BattleGuiStage) guiStage;
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

    public void centerCameraOn(BattleFieldObject hero) {
        if (isCameraAutoCenteringOn()) {
            Coordinates coordinatesActiveObj =
             hero.getCoordinates();
            Vector2 unitPosition = new Vector2(coordinatesActiveObj.x * GridMaster.CELL_W + GridMaster.CELL_W / 2, (gridPanel.getRows() - coordinatesActiveObj.y) * GridMaster.CELL_H - GridMaster.CELL_H / 2);
            cameraPan(unitPosition);
        }
    }
}
