package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import main.libgdx.stage.MainMenuStage;

import java.util.function.Consumer;

public class MainMenuScreen extends ScreenWithLoader {

    private MainMenuStage menuStage;
    private Consumer<String> onScreenDone;

    public MainMenuScreen(Consumer<String> onScreenDone) {
        super();
        this.onScreenDone = onScreenDone;
    }

    @Override
    public void show() {
        loadingStage.setViewport(viewPort);

        menuStage = new MainMenuStage(onScreenDone);
        menuStage.setViewport(viewPort);
        Gdx.input.setInputProcessor(new InputMultiplexer(menuStage));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (hideLoader) {
            menuStage.act(delta);
            menuStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println(width + ":" + height);
        menuStage.updateViewPort(width, height);
      /*  menuStage.getCamera().viewportWidth = width;
        menuStage.getCamera().viewportHeight = height;*/
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

    }
}
