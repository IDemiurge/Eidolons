package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.libgdx.stage.LoadingStage;
import main.libgdx.stage.MainMenuStage;

public class MainMenuScreen implements Screen {

    private final Viewport viewport;
    private LoadingStage loadingStage;
    private MainMenuStage menuStage;

    public MainMenuScreen(Viewport viewport) {
        this.viewport = viewport;
    }


    @Override
    public void show() {
        loadingStage = new LoadingStage();
        loadingStage.setViewport(viewport);

        menuStage = new MainMenuStage();
        menuStage.setViewport(viewport);
        Gdx.input.setInputProcessor(new InputMultiplexer(menuStage));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //camera.update();

        menuStage.act(delta);
        menuStage.draw();
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
