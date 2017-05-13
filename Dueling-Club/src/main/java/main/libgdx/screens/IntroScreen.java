package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import main.libgdx.DialogScenario;
import main.libgdx.stage.ChainedStage;

import java.util.Arrays;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class IntroScreen implements Screen {

    private static final String javaLogoPath = "big/java_logo.png";
    private static final String libgdxLogoPath = "big/libgdx_logo.png";
    private static final String backIntroPath = "big/ravenwood.jpg";
    private static final String portratePath = "mini/unit/Darkness/dungeon/drone.jpg";
    private static final String notSkipableMessage = "This message you cant skip, 3 sec.";
    private static final String skipableMessage = "This message you may skip, press any key blablabla...";
    private Runnable onDone;
    private ChainedStage logoStage;
    private ChainedStage introStage;

    public IntroScreen(Runnable onDone) {
        this.onDone = onDone;

        DialogScenario javaScenario = new DialogScenario(500, false, getOrCreateR(javaLogoPath), null, null);
        DialogScenario libgdxScenario = new DialogScenario(500, false, getOrCreateR(libgdxLogoPath), null, null);
        DialogScenario introScenario = new DialogScenario(500, false, getOrCreateR(backIntroPath), notSkipableMessage, getOrCreateR(portratePath));
        DialogScenario introScenario2 = new DialogScenario(500, true, getOrCreateR(backIntroPath), skipableMessage, null);

        logoStage = new ChainedStage(Arrays.asList(javaScenario, libgdxScenario));
        introStage = new ChainedStage(Arrays.asList(introScenario, introScenario2));

        Gdx.input.setInputProcessor(new InputMultiplexer(logoStage, introStage));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (!logoStage.isDone()) {
            logoStage.act(delta);
            logoStage.draw();
        } else if (!introStage.isDone()) {
            introStage.act(delta);
            introStage.draw();
        } else {
            if (onDone != null) {
                onDone.run();
                onDone = null;
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        logoStage.dispose();
        introStage.dispose();
    }
}
