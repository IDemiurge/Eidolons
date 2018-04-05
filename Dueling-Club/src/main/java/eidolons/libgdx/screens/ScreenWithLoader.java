package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.LoadingStage;
import eidolons.system.audio.MusicMaster;
import main.system.EventCallbackParam;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 11/28/2017.
 */
public abstract class ScreenWithLoader extends ScreenAdapter {
    protected LoadingStage loadingStage;
    protected boolean hideLoader = false;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected ChainedStage introStage;
    protected Batch batch;
    protected Label waitingLabel;
    protected EventCallbackParam param;
    protected float timeWaited;
    private boolean waitingForInput;

    public ScreenWithLoader() {
        waitingLabel = new Label("Press any key to Continue...", StyleHolder.getDefaultLabelStyle());
        waitingLabel.setPosition(GdxMaster.centerWidth(waitingLabel),
         50);
    }

    public Batch getBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        return batch;
    }

    protected void preLoad() {
        try {
            MusicMaster.getInstance().startLoop();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (data.getDialogScenarios().size() > 0) {
            introStage = new ChainedStage(viewPort, getBatch(), data.getDialogScenarios());
            introStage.setOnDoneCallback(() -> {
                if (hideLoader) {
                    updateInputController();
                }
            });
        }

    }

    public void loadDone(EventCallbackParam param) {
        if (Assets.isOn()) {
            while (!Assets.get().getManager().update()) {
                //loading...
            }
        }
        if (isWaitForInput()) {
            setWaitingForInput(true);
            this.param = param;
            updateInputController();
        } else done(param);
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
        initCursor();
        triggerInitialEvents();
    }

    protected void triggerInitialEvents() {
    }

    protected void initCursor() {
        Gdx.graphics.setSystemCursor(SystemCursor.Ibeam);
    }

    protected void initLoadingCursor() {

//         cursor = Gdx.graphics.newCursor(myPixmap, 0, 0);
//        Gdx.graphics.setCursor(cursor);
        Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
//        cursor.dispose();
    }

    protected abstract void afterLoad();

    protected void hideLoader() {
        this.hideLoader = true;
        loadingStage.done();
        if (introStage != null)
            if (introStage.isDone()) {
                updateInputController();
            }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        checkShader();
        renderLoader(delta);
        if (isWaitingForInput()) {
            waited(delta);
        }
        checkShaderReset();
    }

    protected void checkShaderReset() {
    }

    protected void checkShader() {


    }

    protected void waited(float delta) {
        timeWaited += delta;
        batch.begin();
        float alpha = (timeWaited / 3) % 1;
        alpha = (alpha >= 0.5f) ? 1.5f - (alpha)
         : alpha * 2 + 0.15f;
        waitingLabel.draw(batch, alpha % 1);
        batch.end();
    }

    protected void renderLoader(float delta) {

        if (introStage != null && !introStage.isDone()) {
            introStage.act(delta);
            introStage.draw();
        } else if (!hideLoader) {
            loadingStage.act(delta);
            loadingStage.draw();
        } else
            renderMain(delta);
    }

    protected void renderMain(float delta) {

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

        return hideLoader && isIntroFinished;
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

    protected InputMultiplexer getInputController() {
        if (isWaitingForInput())
            return getWaitForInputController(param);
        return introStage != null ?
         new InputMultiplexer(loadingStage, introStage) :
         new InputMultiplexer(loadingStage);
    }

    public void updateInputController() {
        GdxMaster.setInputProcessor(getInputController());
    }

    public void initLoadingStage(ScreenData meta) {
        this.loadingStage = new LoadingStage(meta);
        initLoadingCursor();
    }

    public boolean isLoadingDone() {
        return hideLoader;
    }
}
