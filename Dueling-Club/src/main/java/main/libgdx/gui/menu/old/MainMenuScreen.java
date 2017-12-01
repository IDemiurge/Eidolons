package main.libgdx.gui.menu.old;

import com.badlogic.gdx.InputMultiplexer;
import main.libgdx.screens.ScreenWithVideoLoader;
import main.system.EngineEventManager;
import main.system.EngineEventType;

public class MainMenuScreen extends ScreenWithVideoLoader {

    private MainMenuStage menuStage;

    @Override
    protected void preLoad() {
        super.preLoad();
        menuStage = new MainMenuStage();
        menuStage.setData(data);
    }

    @Override
    public void show() {
        loadingStage.setViewport(viewPort);
        menuStage.setViewport(viewPort);
    }

    @Override
    protected void afterLoad() {
        menuStage.setLoadGameCallback(s ->
                EngineEventManager.trigger(EngineEventType.LOAD_GAME, s)
        );
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
