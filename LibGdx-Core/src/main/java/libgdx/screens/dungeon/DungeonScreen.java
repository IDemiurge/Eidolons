package libgdx.screens.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.content.consts.GridCreateData;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import libgdx.GdxMaster;
import libgdx.assets.Assets;
import libgdx.audio.DC_Playback;
import libgdx.bf.grid.DC_GridPanel;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.mouse.DungeonInputController;
import libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import libgdx.gui.panels.headquarters.HqPanel;
import libgdx.gui.panels.headquarters.town.TownPanel;
import libgdx.particles.EmitterPools;
import libgdx.particles.ambi.ParticleManager;
import libgdx.shaders.DarkShader;
import libgdx.shaders.GrayscaleShader;
import libgdx.stage.BattleGuiStage;
import libgdx.stage.GridStage;
import libgdx.stage.GuiStage;
import eidolons.system.audio.MusicMaster;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.lwjgl.opengl.GL11;

import static eidolons.system.audio.MusicEnums.MUSIC_SCOPE.ATMO;
import static main.system.GuiEventType.BATTLE_FINISHED;
import static main.system.GuiEventType.UPDATE_SHADOW_MAP;

/**
 * Created with IntelliJ IDEA. Date: 21.10.2016 Time: 23:55 To change this template use File | Settings | File
 * Templates.
 */
public class DungeonScreen extends GameScreenWithTown {
    protected static DungeonScreen instance;

    private boolean blocked;
    private boolean firstShow;
    private boolean gridFirstDraw;
    private DC_Playback soundPlayback;

    public static DungeonScreen getInstance() {
        return instance;
    }

    public DungeonScreen() {
        super();
        initLabels();
    }

    @Override
    protected void preLoad() {
        instance = this;
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GUI_READY);
        super.preLoad();
        gridStage = new GridStage(viewPort, getBatch());

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
            gridPanel.setParticleManager(particleManager);
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
        Gdx.gl20.glEnable(GL11.GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_POINT_SMOOTH_HINT, GL20.GL_NICEST);
        Gdx.gl20.glEnable(GL11.GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);
        Gdx.gl20.glEnable(GL11.GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL20.GL_NICEST);

        super.afterLoad();
        controller = new DungeonInputController(getCamera());

        soundPlayback = new DC_Playback(this);

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
        });
    }


    protected void selectionPanelClosed() {
        if (TownPanel.getActiveInstance() != null)
            return;//TODO fix late events in auto-load
        getOverlayStage().setActive(false);
        super.selectionPanelClosed();
    }

    protected void triggerInitialEvents() {
        GuiEventManager.trigger(UPDATE_SHADOW_MAP);
    }


    protected boolean canShowScreen() {
        boolean show = super.canShowScreen();
        if (show) {
            if (!firstShow) {
                firstShow = true;
                blackout(3, 1, true);
            }
        }
        return show;
    }

    @Override
    protected void doBlackout() {
        if (getCamera() != null) {
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
                if (!isBlocked())////TODO some dialogue windows will pause RT -thread and more?
                    Eidolons.game.getLoop().setVisualLock(false);
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
                soundPlayback.doPlayback(delta);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        renderMeta();
    }

    public boolean isDrawGrid() {
        if (!gridFirstDraw) {
            gridFirstDraw = true;
            gridPanel.firstDraw();
            return true;
        }
        return !isOpaque();
    }

    @Override
    public void resize(int width, int height) {
        gridStage.getRoot().setSize(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);
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

    @Override
    public void reset() {
        super.reset();
        getOverlayStage().setActive(true);
    }

    protected void resetShader() {
        batch.setShader(null);

        if (batch.getShader() != DarkShader.getDarkShader())
            if (isBlocked() || ExplorationMaster.isWaiting()) {
                bufferedShader = batch.getShader();
                // batch.setFluctuatingShader(DarkShader.getInstance());
                guiStage.getCustomSpriteBatch().setShader(GrayscaleShader.getGrayscaleShader());
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
        if (isWaitingForInputNow())
            return getWaitForInputController(param);
        if (getGuiStage().isDialogueMode()) {
            return GdxMaster.getMultiplexer(guiStage, controller);
        }
        if (canShowScreen()) {  //town is considered 'loading phase' still
            return GdxMaster.getMultiplexer(guiStage, controller, gridStage);
        } else {
            if (TownPanel.getActiveInstance() != null || Flags.isIggDemoRunning() || selectionPanel instanceof HeroSelectionPanel) {
                return GdxMaster.getMultiplexer(guiStage, super.createInputController());
            } else {
                return super.createInputController();
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

    protected String getLoadScreenPath() {
        return (loaded || waitingForInput) ? "main/art/MAIN_MENU.jpg"
                : "ui/main/logo fullscreen.png";
    }

    @Override
    public void loadDone(EventCallbackParam param) {
        this.param = param;
        //THIS MEANS THE LOGIC HAS DONE LOADING LEVEL!
        try {
            loadingStage.getFullscreenImage().setImage(
                    DC_Game.game.getDungeon().getMapBackground());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        loadingStage.getFullscreenImage().setImage(getLoadScreenPath());
        if (param.get() instanceof GridCreateData)
            if (Assets.isOn()) {
                Chronos.mark("ASSET_LOADING");
                if (Assets.preloadObjects(((GridCreateData) param.get()).getObjects())) {
                    setLoadingFinalAtlases(true);
                    return;
                }

            }
        super.loadDone(param);
    }

    @Override
    public void loadingAssetsDone(EventCallbackParam param) {
        super.loadingAssetsDone(param);
        loadingStage.getFullscreenImage().setImage(getLoadScreenPath());
    }

}
