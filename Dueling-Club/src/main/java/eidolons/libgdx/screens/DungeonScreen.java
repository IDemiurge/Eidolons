package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.macro.MacroGame;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static eidolons.system.audio.MusicMaster.MUSIC_SCOPE.ATMO;
import static main.system.GuiEventType.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends GameScreenWithTown {
    protected static DungeonScreen instance;

    protected ParticleManager particleManager;
    protected StageX gridStage;
    protected GridPanel gridPanel;
    private boolean blocked;
    private GridCellContainer stackView;
    private boolean firstShow;
    private boolean gridFirstDraw;

    public static DungeonScreen getInstance() {
        return instance;
    }

    public DungeonScreen() {
        super();
    }

    public void moduleEntered(Module module) {
        int w = module.getWidth();
        int h = module.getHeight();
        gridPanel = new GridPanel(w, h);
        if (Eidolons.getGame().getDungeonMaster().getBuilder() instanceof LocationBuilder) {
            ((LocationBuilder) Eidolons.getGame().getDungeonMaster().getBuilder()).initModuleZoneLazily(module);

//            gridPanel.init(units);
        }
//        grids.put(module, gridPanel);
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
            if (CoreEngine.isIggDemo())
                return;
            if (!CoreEngine.isLiteLaunch())
                if (isSpriteBgTest()) {
                    setBackground(Sprites.BG_DUNGEON);
                    return;
                }
            final String path = (String) param.get();
            setBackground(path);
        });

        EmitterPools.preloadDefaultEmitters();

        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);

        GdxMaster.setLoadingCursor();
    }


    protected void bindEvents() {
        GuiEventManager.bind(BATTLE_FINISHED, param -> {
            DC_Game.game.getLoop().stop(); //cleanup on real exit
            DC_Game.game.getMetaMaster().gameExited();
            if (MacroGame.getGame() != null) {
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                        new ScreenData(SCREEN_TYPE.MAP));

                MacroGame.getGame().getLoop().combatFinished();
                main.system.auxiliary.log.LogMaster.log(1, " returning to the map...");
            }
        });
        GuiEventManager.bind(GuiEventType.SHOW_TOWN_PANEL, p -> {
            try {
                showTownPanel(p);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                showTownPanel(null);
                Eidolons.exitToMenu();
            }
        });

        GuiEventManager.bind(GuiEventType.CAMERA_LAPSE_TO, p -> {
            getCam().position.set(GridMaster.getCenteredPos((Coordinates) p.get()), 0);

        });
    }

    @Override
    public void dispose() {
        super.dispose();
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    }

    protected void checkGraphicsUpdates() {
        if (backTexture == null && backgroundSprite == null) {
            String path = null;
            try {
                path = DC_Game.game.getDungeonMaster().getDungeonWrapper().getMapBackground();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            main.system.auxiliary.log.LogMaster.dev("Setting dc background " + path);
            Chronos.mark("bg");
            setBackground(path);
            Chronos.logTimeElapsedForMark("bg");
        }
    }

    private void setBackground(String path) {
        if (path == null) {
            return;
        }
        if (!TextureCache.isImage(path)) {
            if (path.endsWith(".txt")) {
                backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(path, false);
            }
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
        Gdx.gl20.glEnable(GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        setCam((OrthographicCamera) viewPort.getCamera());
        particleManager = new ParticleManager();

        soundMaster = new DC_SoundMaster(this);

        MusicMaster.getInstance().scopeChanged(ATMO);

        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH());
        controller = new DungeonInputController(getCam());
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

        cameraMan.centerCameraOnMainHero();
        selectionPanelClosed();
        checkInputController();
        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);


        if (CoreEngine.isIggDemo())
            initBackground();
    }

    private void initBackground() {
        String path = IGG_Images.getBackground();
        setBackground(path);
    }


    public void updateGui() {
        getGuiStage().getBottomPanel().update();
        checkGraphicsUpdates();
    }

    @Override
    protected InputProcessor createInputController() {
//        if (GdxMaster.isVisibleEffectively(selectionPanel)){ //TODO for 'aesthetic' choice after death
//                if (selectionPanel.getStage()==guiStage) {
//            return GdxMaster.getMultiplexer(guiStage, controller);
//            }
//        }
        if (isWaitingForInput())
            return getWaitForInputController(param);
        if (guiStage.isDialogueMode()) {
            return GdxMaster.getMultiplexer(guiStage, controller);
        }
        if (canShowScreen()) {  //town is considered 'loading phase' still
            return GdxMaster.getMultiplexer(guiStage, controller, gridStage);
        } else {
            if (TownPanel.getActiveInstance() != null || CoreEngine.isIggDemoRunning()) {
                return GdxMaster.getMultiplexer(guiStage, super.createInputController());
            } else {
                return super.createInputController();
            }
        }
    }

    protected void checkInputController() {
        if (guiStage.isDialogueMode() != GdxMaster.hasController(Gdx.input.getInputProcessor(), gridStage)) {
            updateInputController();
        }
    }

    protected void selectionPanelClosed() {
        if (TownPanel.getActiveInstance() != null)
            return;//TODO fix late events in auto-load
        getOverlayStage().setActive(false);
        super.selectionPanelClosed();
    }

    protected void triggerInitialEvents() {
        //        DC_Game.game.getVisionMaster().triggerGuiEvents();
        GuiEventManager.trigger(UPDATE_SHADOW_MAP);
    }


    protected boolean canShowScreen() {
//        if (selectionPanel != null) TODO igg demo fix
//            if (selectionPanel.isVisible()) {
//                return false;
//            }
        boolean show = super.canShowScreen();
        if (show) {
            if (!firstShow) {
                firstShow = true;
//                toBlack();
//                blackout(5, 0);
                blackout(5, 1, true);
            }
        }
        return show;
    }

    @Override
    protected void doBlackout() {
        if (getCamera() != null) {
//            float h = Gdx.graphics.getHeight() * getCam().zoom;
//            float w = Gdx.graphics.getWidth() * getCam().zoom;
//            getBatch().blackSprite.setBounds(getCam().position.x - w / 2,
//                    getCam().position.y - h/ 2, 1920 , 1050); //center?}
            Camera camera = guiStage.getViewport().getCamera();
            camera.update();
            batch.setProjectionMatrix(camera.combined);
        }
        super.doBlackout();
    }

    @Override
    protected void renderLoaderAndOverlays(float delta) {
        setBlocked(checkBlocked());
        super.renderLoaderAndOverlays(delta);
    }

    @Override
    public void render(float delta) {

        Gdx.gl20.glEnable(GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        batch.shaderFluctuation(delta);
        if (speed != null) {
            delta = delta * speed;
        }
        if (CoreEngine.isDevEnabled() && !Cinematics.ON)
            if (DC_Game.game != null) {
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
                        DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
                    } else {
                        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                            CoreEngine.setCinematicMode(!CoreEngine.isFootageMode());
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

    public void renderMain(float delta) {

        checkInputController();
//        stages.for
        guiStage.act(delta);
        if (isShowingGrid())
//            if (isDrawGrid())
            gridStage.act(delta);
        setBlocked(checkBlocked());
        cameraMan.act(delta);
        if (!canShowScreen()) {
            getCam().position.x = 0;
            getCam().position.y = 0;
            if (postProcessing != null)
                postProcessing.begin();
            batch.begin();
            drawBg(delta);
//            selectionPanel.setPosition(0, 0);
            selectionPanel.setPosition(selectionPanel.getWidth() / 2, selectionPanel.getY());
            selectionPanel.draw(batch, 1);
            batch.end();
            if (postProcessing != null) {
                batch.resetBlending();
                postProcessing.end();
            }
        } else {
            if (DC_Game.game != null)
                if (DC_Game.game.getLoop() instanceof RealTimeGameLoop) {
                    if (!isBlocked())
                        ((RealTimeGameLoop) Eidolons.game.getLoop()).act(delta);
                }
            if (postProcessing != null)
                postProcessing.begin();
            drawBg(delta);
            if (isDrawGrid()) {
                gridStage.draw();
            }
            if (postProcessing != null) {
                batch.resetBlending();
                postProcessing.end();
            }

            guiStage.draw();


            try {
                soundMaster.doPlayback(delta);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    private boolean isDrawGrid() {
        if (!gridFirstDraw){
            gridFirstDraw=true;
            return true;
        }
        return !isOpaque();
    }

    private void drawBg(float delta) {
        updateBackground(delta);
        if (backTexture != null) {
            Batch batch = guiStage.getCustomSpriteBatch();
            batch.begin();
            float colorBits = GdxColorMaster.WHITE.toFloatBits();
            if (batch.getColor().toFloatBits() != colorBits)
                batch.setColor(colorBits); //gotta reset the alpha...

            int w = backTexture.getRegionWidth();
            int h = backTexture.getRegionHeight();
            int x = (GdxMaster.getWidth() - w) / 2;
            int y = (GdxMaster.getHeight() - h) / 2;
            batch.draw(backTexture, x, y, w, h);
            if (backTexture == null)
                if (backgroundSprite != null) {
                    drawSpriteBg(batch);
                }
            batch.end();

        }

    }

    private void drawSpriteBg(Batch batch) {
        backgroundSprite.setOffsetY(-
                Gdx.graphics.getHeight() / 2);
        backgroundSprite.setOffsetX(-Gdx.graphics.getWidth() / 2);
        backgroundSprite.setSpeed(0.5f);
        backgroundSprite.setOffsetY(getCam().position.y);
        backgroundSprite.setOffsetX(getCam().position.x);
        backgroundSprite.draw(batch);
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


    private boolean isShowingGrid() {
        if (HqPanel.getActiveInstance() != null) {
            if (HqPanel.getActiveInstance().getColor().a == 1) {
                return false;
            }
        }
        return true;
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
            }
            backTexture = backgroundSprite.getCurrentFrame();

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

    private boolean isSpriteBgTest() {
        return EidolonsGame.TUTORIAL_MISSION;
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

    protected void resetShader() {

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
