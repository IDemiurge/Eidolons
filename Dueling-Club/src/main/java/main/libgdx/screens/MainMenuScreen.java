package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import main.libgdx.IntroSceneFactory;
import main.libgdx.stage.ChainedStage;
import main.libgdx.stage.MainMenuStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.LOAD_SCREEN;

public class MainMenuScreen extends ScreenWithLoader {

    private MainMenuStage menuStage;
    private ChainedStage introStage;

    public MainMenuScreen() {
        super();
    }

    @Override
    public void show() {
        loadingStage.setViewport(viewPort);

        menuStage = new MainMenuStage();
        menuStage.setViewport(viewPort);

        introStage = IntroSceneFactory.getIntroStage();
        introStage.setViewport(viewPort);

        Gdx.input.setInputProcessor(new InputMultiplexer(menuStage, introStage));
    }

    @Override
    protected void afterLoad() {
        menuStage.setLoadGameCallback(s -> GuiEventManager.trigger(LOAD_SCREEN, s));
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
    public void dispose() {

    }
}
