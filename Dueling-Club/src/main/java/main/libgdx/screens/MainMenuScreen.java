package main.libgdx.screens;

import com.badlogic.gdx.InputMultiplexer;
import main.libgdx.stage.MainMenuStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.LOAD_SCREEN;

public class MainMenuScreen extends ScreenWithLoader {

    private MainMenuStage menuStage;

    @Override
    protected void preLoad() {
        menuStage.setData(data);
    }

    @Override
    public void show() {
        loadingStage.setViewport(viewPort);

        menuStage = new MainMenuStage();
        menuStage.setViewport(viewPort);
    }

    @Override
    protected void afterLoad() {
        menuStage.setLoadGameCallback(s -> GuiEventManager.trigger(LOAD_SCREEN, s));
    }

    @Override
    protected InputMultiplexer getInputController() {
        return canShowScreen() ?
                new InputMultiplexer(menuStage) :
                super.getInputController();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (canShowScreen()) {
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
