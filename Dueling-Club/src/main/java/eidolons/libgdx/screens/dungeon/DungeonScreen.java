package eidolons.libgdx.screens.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.GridCreateData;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.GuiStage;
import eidolons.libgdx.stage.StageX;
import eidolons.macro.MacroGame;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static eidolons.system.audio.MusicMaster.MUSIC_SCOPE.ATMO;
import static main.system.GuiEventType.BATTLE_FINISHED;
import static main.system.GuiEventType.UPDATE_SHADOW_MAP;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends GameScreenWithTown {
    protected static DungeonScreen instance;

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

    @Override
    protected void preLoad() {
        instance = this;
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GUI_READY);
        super.preLoad();
        gridStage = new StageX(viewPort, getBatch());

        guiStage = createGuiStage(); //separate batch for PP

        initGl();
        preBindEvent();

        EmitterPools.preloadDefaultEmitters();

        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);

        GdxMaster.setLoadingCursor();
    }

    @Override
    protected void preBindEvent() {
        super.preBindEvent();

        GuiEventManager.bind(GuiEventType.INITIAL_LOAD_DONE, p -> {
            particleManager = new ParticleManager();
            final GridCreateData param = ((GridCreateData) p.get());
            createAndInitModuleGrid(param);
            gridStage.addActor(particleManager);
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    }

    public void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects) {
        super.moduleEntered(module, objects);
        particleManager.initModule(module);
        if (module.getPlatformData() != null)
            gridPanel.getPlatformHandler().init(module.getPlatformData());
    }
    @Override
    protected void afterLoad() {
        Gdx.gl20.glEnable(GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        super.afterLoad();
        controller = new DungeonInputController(getCamera());

        soundMaster = new DC_SoundMaster(this);

        MusicMaster.getInstance().scopeChanged(ATMO);

        try {
            getController().setDefaultPos();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        bindEvents();

        cameraMan.centerCameraOnMainHero();
        selectionPanelClosed();
        checkInputController();
        WaitMaster.receiveInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);

    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
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

    protected GuiStage createGuiStage() {
        return new BattleGuiStage(null, null);
    }

    protected GridPanel createGrid(GridCreateData param) {
        return new DC_GridPanel(param.getCols(), param.getRows(), param.getModuleWidth(), param.getModuleHeight());
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
                        ((RealTimeGameLoop) Eidolons.game.getLoop()).setLocked(false);
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

    public boolean isDrawGrid() {
        if (!gridFirstDraw) {
            gridFirstDraw = true;
            return true;
        }
        return !isOpaque();
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
            return HqPanel.getActiveInstance().getColor().a != 1;
        }
        return true;
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
        return getGuiStage().isBlocked();
    }

    public void updateGui() {
        getGuiStage().getBottomPanel().update();
        checkGraphicsUpdates();
    }

    @Override
    protected void renderLoaderAndOverlays(float delta) {

        setBlocked(checkBlocked());
        super.renderLoaderAndOverlays(delta);
    }

    protected void checkInputController() {
        if (getGuiStage().isDialogueMode() != GdxMaster.hasController(Gdx.input.getInputProcessor(), gridStage)) {
            updateInputController();
        }
    }

    @Override
    protected InputProcessor createInputController() {
//        if (GdxMaster.isVisibleEffectively(selectionPanel)){ //TODO for 'aesthetic' choice after death
//                if (selectionPanel.getStage()==guiStage) {
//            return GdxMaster.getMultiplexer(guiStage, controller);
//            }
//        }
        if (isWaitingForInputNow())
            return getWaitForInputController(param);
        if (getGuiStage().isDialogueMode()) {
            return GdxMaster.getMultiplexer(guiStage, controller);
        }
        if (canShowScreen()) {  //town is considered 'loading phase' still
            return GdxMaster.getMultiplexer(guiStage, controller, gridStage);
        } else {
            if (TownPanel.getActiveInstance() != null || CoreEngine.isIggDemoRunning() || selectionPanel instanceof HeroSelectionPanel) {
                return GdxMaster.getMultiplexer(guiStage, super.createInputController());
            } else {
                return super.createInputController()

                        ;
            }
        }
    }

    public DC_GridPanel getGridPanel() {
        return (DC_GridPanel) gridPanel;
    }

    public BattleGuiStage getGuiStage() {
        return (BattleGuiStage) guiStage;
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
