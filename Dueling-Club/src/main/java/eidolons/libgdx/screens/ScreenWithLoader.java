package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.LoadingStage;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions;
import eidolons.system.text.TipMaster;
import main.system.EventCallbackParam;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;

public abstract class ScreenWithLoader extends ScreenAdapter {
    public static final String ASSET_LOADING = "ASSET LOADING";
    protected CustomSpriteBatch batch;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected ChainedStage introStage;
    protected EventCallbackParam param;
    protected PostProcessController postProcessing;
    private float tooltipTimer = getTooltipPeriod();
    private boolean loadingAtlases;
    private int assetLoadTimer = getAssetLoadTimeLimit();

    protected LoadingStage loadingStage;
    protected boolean loading = true;
    protected boolean loaded;
    protected boolean waitingForInput;
    protected float timeWaited;
    protected Label tooltipLabel;
    protected Label waitingLabel;


    protected SpriteAnimation backgroundSprite;

    public ScreenWithLoader() {
        waitingLabel = new Label("Press any key to Continue...",
                StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 22));
        waitingLabel.pack();
        waitingLabel.setPosition(GdxMaster.centerWidth(waitingLabel),
                getWaitY());
        tooltipLabel = new Label("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 20));

        if (CoreEngine.isLiteLaunch() || !CoreEngine.isIDE())
            if (OptionsMaster.getPostProcessingOptions().getBooleanValue(
                    PostProcessingOptions.POST_PROCESSING_OPTIONS.ALL_OFF))
                return;

        try {
            postProcessing = getPostProcessController();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            FileLogManager.streamMain("Shader loader failed!");
            OptionsMaster.getPostProcessingOptions().setValue(PostProcessingOptions.POST_PROCESSING_OPTIONS.ALL_OFF, true);
            OptionsMaster.saveOptions();
        }


    }

    protected void preLoad() {
        try {
            MusicMaster.getInstance().startLoop();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (data.getDialogViews().size() > 0) {
            introStage = new ChainedStage(viewPort, getBatch(), data.getDialogViews());
            introStage.setOnDoneCallback(() -> {
                if (!loading) {
                    updateInputController();
                }
            });
        }

    }

    public void loadDone(EventCallbackParam param) {
        this.param = param;

        if (param.get() instanceof BFDataCreatedEvent)
            if (Assets.isOn()) {
                Chronos.mark(ASSET_LOADING);
                if (Assets.preloadAll(((BFDataCreatedEvent) param.get()).getObjects())) {
                    setLoadingAtlases(true);
                    return;
                }

            }
        loadingAssetsDone(param);
        GdxMaster.setDefaultCursor();

    }

    public void loadingAssetsDone(EventCallbackParam param) {
        Chronos.logTimeElapsedForMark(ASSET_LOADING);
        if (isWaitForInput()) {
            setWaitingForInput(true);
            this.param = param;
            updateInputController();
            GdxMaster.setDefaultCursor();
        } else done(this.param);
    }

    protected void renderMain(float delta) {

    }
    protected void renderLoader(float delta) {
        if (introStage != null && !introStage.isDone()) {
            introStage.act(delta);
            introStage.draw();
        } else if (loading) {
            renderLoaderAndOverlays(delta);

        } else
            renderMain(delta);

    }

    protected boolean isWaitForInput() {
        return !(CoreEngine.isIDE() || CoreEngine.isMacro());
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
        data.setParam(param);
        this.hideLoader();
        afterLoad();
        updateInputController();
        GdxMaster.setDefaultCursor();
        triggerInitialEvents();
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
        if (introStage != null)
            if (introStage.isDone()) {
                updateInputController();
            }

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
        waited(delta);
        checkShaderReset();
        if (isLoadingAtlases()) {
            if (assetLoadTimer <= 0 || Assets.get().getManager().update()) {
                setLoadingAtlases(false);
                loadingAssetsDone(param);
                return;
            }
            if (isAssetLoadTimerOn())
                assetLoadTimer -= delta;
        }
    }

    private boolean isAssetLoadTimerOn() {
        return false;
    }

    protected void checkShaderReset() {
    }

    protected void resetShader() {


    }

    protected void waited(float delta) {
        timeWaited += delta;

        //

        //    TODO     tooltipLabel.draw(batch, 1f);

        if (isWaitingForInput()) {

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


    private float getWaitY() {
        return GdxMaster.getHeight() / 20 + 35;
    }

    private float getTipY() {
        return GdxMaster.getHeight() / 20;
    }

    public CustomSpriteBatch getBatch() {
        if (batch == null) {
            batch = CustomSpriteBatch.getMainInstance();
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
        if (loading)
            return true;
        return false;
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
    }


    public void backToLoader() {
        loading = true;
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    public void setWaitingForInput(boolean waitingForInput) {
        main.system.auxiliary.log.LogMaster.log(1, "waitingForInput from " +
                this.waitingForInput +
                " to " + waitingForInput);
        this.waitingForInput = waitingForInput;

    }

    public float getTimeWaited() {
        return timeWaited;
    }

    protected boolean canShowScreen() {
        boolean isIntroFinished = true;
        if (introStage != null && !introStage.isDone()) {
            isIntroFinished = false;
        }

        return !loading && isIntroFinished;
    }

    public void setData(ScreenData data) {
        this.data = data;
        preLoad();
        updateInputController();
    }

    public void setViewPort(ScreenViewport viewPort) {
        this.viewPort = viewPort;
        loadingStage.setViewport(viewPort);
    }

    protected InputProcessor createInputController() {
        if (isWaitingForInput())
            return getWaitForInputController(param);
        return introStage != null ?
                new InputMultiplexer(loadingStage, introStage) :
                new InputMultiplexer(loadingStage);
    }

    public void updateInputController() {
        GdxMaster.setInputProcessor(
                createInputController());
    }

    public void initLoadingStage(ScreenData meta) {
        this.loadingStage = new LoadingStage(meta);
        loadingStage.setViewport(new ScreenViewport(new OrthographicCamera()));

    }

    public void reset() {

    }

    public boolean isLoadingAtlases() {
        return loadingAtlases;
    }

    public void setLoadingAtlases(boolean loadingAtlases) {
        assetLoadTimer = getAssetLoadTimeLimit();
        this.loadingAtlases = loadingAtlases;
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
