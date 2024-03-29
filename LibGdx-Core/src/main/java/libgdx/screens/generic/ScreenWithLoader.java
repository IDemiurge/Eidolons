package libgdx.screens.generic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.assets.Assets;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.post.PostProcessController;
import libgdx.stage.LoadingStage;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions;
import eidolons.system.text.TipMaster;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

public abstract class ScreenWithLoader extends ScreenWithAssets {
    protected CustomSpriteBatch batch;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected EventCallbackParam param;
    protected PostProcessController postProcessing;

    private boolean loadingFinalAtlases;
    private int assetLoadTimer = getAssetLoadTimeLimit();

    protected LoadingStage loadingStage;
    protected boolean loading = true;
    protected boolean loaded;
    private float tooltipTimer = getTooltipPeriod();
    protected boolean waitingForInput;
    protected float timeWaited;
    protected Label tooltipLabel;
    protected Label waitingLabel;

    protected Label statusLabel;
    protected Label loadingLabel;
    protected Label buildLabel;


    protected SpriteAnimation backgroundSprite;

    protected FloatAction blackoutAction = new FloatAction();
    private float blackout;
    private boolean blackoutBack;
    private boolean whiteout;

    public ScreenWithLoader() {
        initBlackout();
        initPostProcessing();
        initLabels();
    }

    protected void initPostProcessing() {
        if (Flags.isIggDemo()) {
            return;
        }
        if (!OptionsMaster.getPostProcessingOptions().getBooleanValue(
                PostProcessingOptions.POST_PROCESSING_OPTIONS.ENABLED))
            return;
        try {
            postProcessing = getPostProcessController();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            FileLogManager.streamMain("Shader loader failed!");
            OptionsMaster.getPostProcessingOptions().setValue(
                    PostProcessingOptions.POST_PROCESSING_OPTIONS.ENABLED, false);
            OptionsMaster.saveOptions();
        }

    }

    protected void initBlackout() {
        blackout = 1;
        blackout(4, 0);
    }

    protected void preLoad() {
    }

    protected void initLabels() {
        waitingLabel = new Label("Press any key to Continue...",
                StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 22));
        waitingLabel.pack();
        waitingLabel.setPosition(GdxMaster.centerWidth(waitingLabel),
                getWaitY());
        tooltipLabel = new Label("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 20));

        statusLabel = new Label("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 20));
        statusLabel.setPosition(125,                90);
        GuiEventManager.bind(GuiEventType.UPDATE_LOAD_STATUS,
                p -> statusLabel.setText(p.get().toString()));

        loadingLabel = new Label("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 20));
        loadingLabel.setPosition(0,                GdxMaster.getHeight()-100);

        buildLabel = new Label("Build "+ CoreEngine.VERSION, StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 14));
        buildLabel.pack();
        buildLabel.setPosition(GdxMaster.right(buildLabel)-9,                 10);

    }

    public void loadTick() {
        loadingLabel.setText( loadingLabel.getText()+".");

    }
    public void loadDone(EventCallbackParam param) {
        this.param = param;
        loadingAssetsDone(param);
        GdxMaster.setDefaultCursor();

    }

    public void loadingAssetsDone(EventCallbackParam param) {
        loadingLabel.setText("");
        Chronos.logTimeElapsedForMark("ASSET_LOADING");
        if (isWaitForInputSupported()) {
            setWaitingForInput(true);
            this.param = param;
            updateInputController();
        } else
            done(this.param);
    }

    protected void renderMain(float delta) {

    }

    protected void renderLoader(float delta) {
        if (loading) {
            renderLoaderAndOverlays(delta);
        } else
            renderMain(delta);

    }

    protected boolean isWaitForInputSupported() {
        return true;
    }

    protected InputMultiplexer getWaitForInputController(EventCallbackParam param) {
        return new InputMultiplexer() {
            @Override
            public boolean keyTyped(char character) {
                done(param);
                setWaitingForInput(false);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                done(param);
                setWaitingForInput(false);
                return true;
            }
        };
    }

    protected void done(EventCallbackParam param) {
        updateInputController();
        Object o = param.get();
        if (isLoaded()) {
            main.system.auxiliary.log.LogMaster.devLog(" FIX DOUBLE LOAD!!!! " + toString() + o);
            return;
        }
        data.setParam(param);
        this.hideLoader();
        afterLoad();
        GdxMaster.setDefaultCursor();
        triggerInitialEvents();
        main.system.auxiliary.log.LogMaster.devLog(toString() + " LOADed with " + o);
    }

    protected void triggerInitialEvents() {
    }


    protected abstract void afterLoad();

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    protected void hideLoader() {
        this.loading = false;
        setLoaded(true);
        loadingStage.done();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        loadingStage.getRoot().setSize(width, height);
        loadingStage.getViewport().update(width, height);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);


        resetShader();

        if (postProcessing != null)
            postProcessing.act(delta);
        if (postProcessing != null)
            if (isPostProcessingOn())
                if (isPostProcessingDefault())
                    postProcessing.begin();
        renderLoader(delta);
        if (postProcessing != null)
            if (isPostProcessingDefault()) {
                getBatch().resetBlending();
                postProcessing.end();
            }
        loadWaited(delta);
        checkShaderReset();
        if (isLoadingFinalAtlases()) {
            if (Assets.get().getManager().update()) {
                setLoadingFinalAtlases(false);
                loadingAssetsDone(param);
            } else if (isAssetLoadTimerOn())
                assetLoadTimer -= delta;

        }

        doBlackout();
        
        renderMeta();
    }

    protected void renderMeta() {
        getBatch().begin();
        buildLabel.draw(getBatch(), 1f);
        getBatch().end();
    }

    public void removeBlack() {
        blackout = 0f;
        blackoutAction.setEnd(0);
        blackoutAction.setDuration(0);
    }

    public void toBlack() {
        blackout = 1f;
        blackoutAction.setEnd(1);
        blackoutAction.setDuration(0);
    }

    public void blackout(float dur, float to) {
        blackout(dur, to, false);
    }

    public void blackout(float dur, float to, boolean back) {
        if (back)
            blackoutBack = back;
        //gdx Review
        // main.system.auxiliary.log.LogMaster.dev(toString() + " BlackoutOld to " + to);
        blackoutAction.setDuration(dur);
        if (!whiteout)
            blackoutAction.setInterpolation(Interpolation.fade);
        else
            blackoutAction.setInterpolation(Interpolation.elastic);
        blackoutAction.setStart(blackout);
        blackoutAction.setEnd(to);
        blackoutAction.restart();
    }

    protected void doBlackout() {
        if (blackoutAction.getTime() >= blackoutAction.getDuration()) {
            if (blackoutBack) {
                //                main.system.auxiliary.log.LogMaster.dev("BlackoutOld BACK;" + " blackout==" + blackout);
                blackoutAction.setStart((blackout));
                blackoutAction.setEnd(0);
                blackoutAction.restart();
                blackoutBack = false;
            }
        }
        blackoutAction.act(Gdx.graphics.getDeltaTime());
        blackout = blackoutAction.getValue();
        if (blackout > 0) {
            getBatch().drawBlack(blackout, whiteout);
        }

    }

    private boolean isAssetLoadTimerOn() {
        return false;
    }

    protected void checkShaderReset() {
    }

    protected void resetShader() {


    }

    protected void loadWaited(float delta) {
        if (isWaitingForInputNow()) {
            timeWaited += delta;
            float alpha = (timeWaited / 3) % 1;
            alpha = (alpha >= 0.5f) ? 1.5f - (alpha)
                    : alpha * 2 + 0.15f;
            getBatch().begin();
            waitingLabel.draw(batch, alpha % 1);
            batch.end();
        } else {
            if (isTooltipsOn()) {
                getBatch().begin();
                tooltipTimer += delta;
                tooltipLabel.setVisible(true);
                if (tooltipTimer >= getTooltipPeriod()) { //support manual!
                    tooltipLabel.setText(getTooltipText());
                    tooltipLabel.pack();
                    tooltipLabel.setPosition(GdxMaster.centerWidth(tooltipLabel),
                            getTipY());
                    tooltipTimer = 0;
                }
                tooltipLabel.draw(batch, 1);
                statusLabel.draw(batch, 1);
                loadingLabel.draw(batch, 1);
                batch.end();
            } else tooltipLabel.setVisible(false);
        }

        //        batch.end();
    }


    private PostProcessController getPostProcessController() {
        PostProcessController postProcessing = new PostProcessController();
        //         PostProcessController.getInstance();
        postProcessing.reset();
        return postProcessing;
    }


    protected float getWaitY() {
        return getTipY() + 15;
    }

    protected float getTipY() {
        return 100;
    }

    public CustomSpriteBatch getBatch() {
        if (batch == null) {
            batch = GdxMaster.getMainBatch();
        }
        return batch;
    }

    protected float getTooltipPeriod() {
        return 5;
    }

    protected String getTooltipText() {
        return TipMaster.getTip();
    }

    protected boolean isTooltipsOn() {
        return loading;
    }


    protected boolean isPostProcessingDefault() {
        return true;
    }

    protected boolean isPostProcessingOn() {
        return true;
    }

    protected void renderLoaderAndOverlays(float delta) {
        loadingStage.act(delta);
        loadingStage.draw();
        loadWaited(delta);
    }


    public void backToLoader() {
        loading = true;

        loadingLabel.setText("");
    }

    public boolean isWaitingForInputNow() {
        return waitingForInput;
    }

    public void setWaitingForInput(boolean waitingForInput) {
        main.system.auxiliary.log.LogMaster.log(1, "waitingForInput from " +
                this.waitingForInput +
                " to " + waitingForInput);
        if (waitingForInput) {
            GdxMaster.setDefaultCursor();
        }
        this.waitingForInput = waitingForInput;

        updateInputController();
    }

    public float getTimeWaited() {
        return timeWaited;
    }

    protected boolean canShowScreen() {
        return !loading;
    }

    public void setData(ScreenData data) {
        this.data = data;
        preLoad();
        try {
            updateInputController();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    public void setViewPort(ScreenViewport viewPort) {
        this.viewPort = viewPort;
        loadingStage.setViewport(viewPort);
    }

    protected InputProcessor createInputController() {
        if (isWaitingForInputNow())
            return getWaitForInputController(param);
        return GdxMaster.getMultiplexer(loadingStage);
    }

    public void updateInputController() {
        GdxMaster.setInputProcessor(
                createInputController());
    }

    public void initLoadingStage(ScreenData meta) {
        this.loadingStage = new LoadingStage(meta, getLoadScreenPath());
        loadingStage.setViewport(new ScreenViewport(new OrthographicCamera()));

    }

    protected String getLoadScreenPath() {
        return null;
    }

    public void reset() {

    }

    public boolean isLoadingFinalAtlases() {
        return loadingFinalAtlases;
    }

    public void setLoadingFinalAtlases(boolean loadingAtlases) {
        assetLoadTimer = getAssetLoadTimeLimit();
        this.loadingFinalAtlases = loadingAtlases;
    }

    private int getAssetLoadTimeLimit() {
        return 20;
    }

    public PostProcessController getPostProcessing() {
        return postProcessing;
    }

    public void setupPostProcessing() {
        if (getPostProcessing() != null) {
            getPostProcessing().setup();
        }
    }
}
