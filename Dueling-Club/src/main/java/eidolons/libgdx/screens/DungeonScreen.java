package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.boss.BossTestGroup;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.libgdx.utils.ActTimer;
import eidolons.macro.MacroGame;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static eidolons.system.audio.MusicMaster.MUSIC_SCOPE.ATMO;
import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends GameScreenWithTown {
    protected static float FRAMERATE_DELTA_CONTROL =
            new Float(1) / GenericLauncher.FRAMERATE; //*3 ?
    protected static DungeonScreen instance;
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getControlOptions().
            getBooleanValue(CONTROL_OPTION.AUTO_CENTER_CAMERA_ON_HERO);

    private static boolean centerCameraOnAlliesOnly = OptionsMaster.getControlOptions().
            getBooleanValue(CONTROL_OPTION.CENTER_CAMERA_ON_ALLIES_ONLY);

    protected ParticleManager particleManager;
    protected StageX gridStage;
    protected GridPanel gridPanel;
    private boolean blocked;
    private ActTimer cameraTimer;
    private GridCellContainer stackView;
    private BossTestGroup testStage;
    boolean firstCenteringDone;

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

    public static void setCenterCameraOnAlliesOnly(boolean b) {
        centerCameraOnAlliesOnly = b;
    }

    @Override
    protected void preLoad() {
        instance = this;
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GUI_READY);
        super.preLoad();
        gridStage = new StageX(viewPort, getBatch());

        guiStage = new BattleGuiStage(null, null); //separate batch for PP

        initGl();

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            if (isSpriteBgTest()) {
                setBackground("atlas.txt");
                return;
            }
            final String path = (String) param.get();
            setBackground(path);


        });

        initDialogue();

        EmitterPools.preloadDefaultEmitters();

        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);

        GdxMaster.setLoadingCursor();
        GuiEventManager.bind(GuiEventType.SHOW_TOWN_PANEL, p -> {
            try {
                showTownPanel(p);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                showTownPanel(null);
                Eidolons.exitToMenu();
            }

        });
    }

    private boolean isSpriteBgTest() {
        return false;
    }

    @Override
    protected boolean isTooltipsOn() {
        if (TownPanel.getActiveInstance() != null) {
            return false;
        }
        return super.isTooltipsOn();
    }


    @Override
    public void reset() {
        super.reset();
        getOverlayStage().setActive(true);
    }

    protected void bindEvents() {

        GuiEventManager.bind(CAMERA_PAN_TO_UNIT, param -> {
            DungeonScreen.getInstance().centerCameraOn((BattleFieldObject) param.get());
        });
        GuiEventManager.bind(BATTLE_FINISHED, param -> {
            DC_Game.game.getLoop().stop(); //cleanup on real exit
            DC_Game.game.getMetaMaster().gameExited();
            if (MacroGame.getGame() != null) {
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                        new ScreenData(SCREEN_TYPE.MAP ));

                MacroGame.getGame().getLoop().combatFinished();
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

        if (!TextureCache.isImage(path)) {
            backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(path);
            return;
        }

        TextureRegion texture = getOrCreateR(path);
        if (texture.getTexture() != TextureCache.getEmptyTexture())
            backTexture = texture;

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
            TextureManager.initBackgroundCache(backTexture);
        }
    }

    @Override
    protected void afterLoad() {
        cam = (OrthographicCamera) viewPort.getCamera();
        particleManager = new ParticleManager();

        soundMaster = new DC_SoundMaster(this);

        MusicMaster.getInstance().scopeChanged(ATMO);

        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH());
        controller = new DungeonInputController(cam);
        //do not chain - will fail ...
        gridPanel.init(param.getObjects());

        gridStage.addActor(gridPanel);
        gridStage.addActor(particleManager);
        try {
            controller.setDefaultPos();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        bindEvents();
//TODO use simple float pair to make it dynamic
        cameraTimer = new ActTimer(OptionsMaster.getControlOptions().
                getIntValue(CONTROL_OPTION.CENTER_CAMERA_AFTER_TIME), () -> {
            if (!firstCenteringDone)
            {
                centerCameraOn(Eidolons.getMainHero());
                firstCenteringDone=true;
            }
            if (isCameraAutoCenteringOn())
                if (Eidolons.getGame().getManager().checkAutoCameraCenter()) {
                    if (!Eidolons.getMainHero().isDead()) //for Shade
                    centerCameraOn(Eidolons.getMainHero());
                }
        });
//        try {
//            Unit unit = null;
//            centerCameraOnMainHero();
//            if (Eidolons.game.getMetaMaster() == null) {
//                unit = Eidolons.game.getMetaMaster().
//                        getPartyManager().getParty().getLeader();
//            } else
//                unit = (Unit) Eidolons.game.getPlayer(true).getHeroObj();
//            Vector2 unitPosition =
//                    GridMaster.getCenteredPos(
//                            unit.getCoordinates());
//
//            cameraPan(unitPosition);
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }

        centerCameraOnMainHero();
        selectionPanelClosed();
        checkInputController();
        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }

    private void centerCameraOnMainHero() {
        centerCameraOn(Eidolons.getMainHero(), true);
    }


    public void updateGui() {
        //        gridPanel.updateGui();
        getGuiStage().getBottomPanel().update();
        checkGraphicsUpdates();

    }

    public void cameraStop() {
        super.cameraStop();
        cameraTimer.reset();
    }

    @Override
    protected InputProcessor createInputController() {
        InputMultiplexer current = null;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, gridStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
        } else {
            if (TownPanel.getActiveInstance() != null || CoreEngine.isIggDemoRunning()) {
                return new InputMultiplexer(guiStage, super.createInputController());
            } else {
                return super.createInputController();
            }
        }

        return current;
    }

    protected void checkInputController() {
        if (!GdxMaster.hasController(Gdx.input.getInputProcessor(), gridStage)) {
            updateInputController();
        }
    }

    protected void selectionPanelClosed() {
        if (TownPanel.getActiveInstance() != null) //TODO fix late events in auto-load
            return;
        getOverlayStage().setActive(false);
        super.selectionPanelClosed();
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

        checkInputController();
//        stages.for
        guiStage.act(delta);
        gridStage.act(delta);
        setBlocked(checkBlocked());
        cameraTimer.act(delta);
        cameraShift();
        if (canShowScreen()) {
            if (DC_Game.game != null)
                if (DC_Game.game.getGameLoop() instanceof RealTimeGameLoop) {
                    if (!isBlocked())
                        ((RealTimeGameLoop) Eidolons.game.getGameLoop()).act(delta);
                }

            postProcessing.begin();
            updateBackground(delta);
            if (backTexture != null) {
                guiStage.getBatch().begin();
                float colorBits = GdxColorMaster.WHITE.toFloatBits();
                if (guiStage.getBatch().getColor().toFloatBits() != colorBits)
                    guiStage.getBatch().setColor(colorBits); //gotta reset the alpha...
                guiStage.getBatch().draw(backTexture, 0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
                guiStage.getBatch().end();
            }
            gridStage.draw();
            postProcessing.end();

            guiStage.draw();

            //            postProcessing.disable(); //a failed boat!
            //            gridStage.draw();
            //            batch.flush();
            //            postProcessing.end();
            //            batch.flush();
            //            postProcessing.begin();
            //            postProcessing.disable();
            //            batch.setShader(null);
            //            batch.resetBlending();
            //            batch.getColor().a=1;
            //            guiStage.draw();
            //            batch.flush();
            //            postProcessing.enable();


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

            try {
                soundMaster.doPlayback(delta);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    private void updateBackground(float delta) {
        if (backgroundSprite != null) {
            backgroundSprite.act(delta);
            backTexture = backgroundSprite.getCurrentFrame();
            if (backgroundSprite.getCurrentFrameNumber() == backgroundSprite.getFrameNumber() - 1) {
                if (backgroundSprite.getPlayMode() == Animation.PlayMode.LOOP_REVERSED)
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP);
                else {
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
                }
                backTexture = backgroundSprite.getCurrentFrame();
            }

        }
    }

    @Override
    protected boolean isPostProcessingDefault() {
        return false;
    }

    @Override
    protected Stage getMainStage() {
        return guiStage;
    }

    @Override
    public void render(float delta) {
        batch.shaderFluctuation(delta);

        if (speed != null) {
            delta = delta * speed;
        }
        if (CoreEngine.isIDE())
            //            checkDebugToggle();
            if (DC_Game.game != null) {
                if (Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT)) {
                    if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
                        DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
                    } else {
                        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                            CoreEngine.setCinematicMode(!CoreEngine.isCinematicMode());
                        }
                        if (Gdx.input.isKeyPressed(Keys.TAB)) {
                            DC_Game.game.getVisionMaster().refresh();
                            GuiEventManager.trigger(UPDATE_GUI);
                        }
                    }
                }

            }
        super.render(delta);
    }

    @Override
    protected void renderLoaderAndOverlays(float delta) {
        setBlocked(checkBlocked());
        super.renderLoaderAndOverlays(delta);
    }

    protected void checkShaderReset() {
        if (batch.getShader() == DarkShader.getDarkShader()) {
            batch.shaderReset();
        }
        if (guiStage.getBatch().getShader() == DarkShader.getDarkShader()
                || guiStage.getBatch().getShader() == GrayscaleShader.getGrayscaleShader()
                || guiStage.getBatch().getShader() == GrayscaleShader.getGrayscaleShader()
        ) {
            guiStage.getCustomSpriteBatch().shaderReset();
        }
    }

    protected void checkShader() {

        batch.setShader(null);
//        guiStage .getCustomSpriteBatch().setShader(null);

        if (batch.getShader() != DarkShader.getDarkShader())
            if (isBlocked() || ExplorationMaster.isWaiting()) {
                bufferedShader = batch.getShader();
                batch.setFluctuatingShader(DarkShader.getInstance());
                guiStage.getCustomSpriteBatch().setShader(GrayscaleShader.getGrayscaleShader());
//                guiStage .getCustomSpriteBatch().setFluctuatingShader(GrayscaleShader.getGrayscaleShader());
            }
    }

    public boolean isBlocked() {
        return blocked;
    }

    private void setBlocked(boolean blocked) {
        if (this.blocked == blocked)
            return;
        this.blocked = blocked;
        if (blocked)
            EUtils.hideTooltip();
    }

    public boolean checkBlocked() {
        if (manualPanel != null)
            if (manualPanel.isVisible())
                return true;
        if (selectionPanel != null)
            if (selectionPanel.isVisible() && selectionPanel.getStage() != null)
                return true;
        return guiStage.isBlocked();
    }

    @Override
    public void resize(int width, int height) {
        //     animationEffectStage.getViewport().update(width, height);

        //        float aspectRatio = (float) width / (float) height;
        //        cam = new OrthographicCamera(width * aspectRatio, height) ;

        gridStage.getRoot().setSize(width, height);
        //        guiStage.getRoot().setSize(width, height);
        gridStage.getViewport().update(width, height);
        //        guiStage.setViewport(new ScreenViewport(new OrthographicCamera(width, height)));
        guiStage.getViewport().update(width, height);
        //        getGuiStage().getGuiVisuals().resized();

        //        BattleGuiStage.camera.viewportWidth=width;
        //        BattleGuiStage.camera.viewportHeight=height;
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
        centerCameraOn(hero, null);
    }

    public void centerCameraOn(BattleFieldObject hero, Boolean force) {
        if (!isCenterAlways())
            if (!Bools.isTrue(force))
                if (centerCameraOnAlliesOnly)
                    if (!hero.isMine())
                        return;

        Coordinates coordinatesActiveObj =
                hero.getCoordinates();
        Vector2 unitPosition = new Vector2(coordinatesActiveObj.x * GridMaster.CELL_W + GridMaster.CELL_W / 2, (gridPanel.getRows() - coordinatesActiveObj.y) * GridMaster.CELL_H - GridMaster.CELL_H / 2);
        cameraPan(unitPosition, force);


    }

    public void setCameraTimer(int intValue) {
        if (cameraTimer == null) {
            return;
        }
        cameraTimer.setPeriod(intValue);
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    @Override
    protected boolean isTownInLoaderOnly() {
        return true;
    }

    public GridCellContainer getStackView() {
        return stackView;
    }

    public void setStackView(GridCellContainer stackView) {
        this.stackView = stackView;
    }
}
