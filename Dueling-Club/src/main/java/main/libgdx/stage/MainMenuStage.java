package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.mainmenu.GameLoadingPanel;
import main.libgdx.gui.panels.mainmenu.NewGamePanel;
import main.libgdx.gui.panels.mainmenu.OptionsPanel;
import main.libgdx.gui.panels.mainmenu.StartMenuPanel;

import java.util.function.Consumer;

public class MainMenuStage extends Stage {
    private StartMenuPanel menu;
    private OptionsPanel options;
    private GameLoadingPanel load;
    private NewGamePanel newGame;

    public MainMenuStage(Consumer<String> menuCallback) {
        menu = new StartMenuPanel(true);
        options = new OptionsPanel();
        load = new GameLoadingPanel();
        newGame = new NewGamePanel();

        addActor(menu);
        addActor(options);
        addActor(load);
        addActor(newGame);

        menu.setNewGameCallback(() -> switchView(newGame));
        menu.setExitCallback(() -> System.exit(0));
        menu.setLoadGameCallback(() -> switchView(load));
        menu.setOptionsCallback(() -> switchView(options));

        options.setBackCallback(() -> switchView(menu));

        load.setBackCallback(() -> switchView(menu));

        newGame.setBackCallback(() -> switchView(menu));

        newGame.setStartDemoScenarioCallback(menuCallback);

        switchView(menu);
    }

    private void switchView(TablePanel next) {
        if (menu != next) {
            menu.setVisible(false);
        }

        if (options != next) {
            options.setVisible(false);
        }

        if (load != next) {
            load.setVisible(false);
        }

        if (newGame != next) {
            newGame.setVisible(false);
        }

        next.setVisible(true);
        recalcPos();
    }

    private void recalcPos() {
        menu.setPosition(
                Gdx.graphics.getWidth() / 2 - menu.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - menu.getPrefHeight() / 2
        );
        options.setPosition(
                Gdx.graphics.getWidth() / 2 - options.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - options.getPrefHeight() / 2
        );
        load.setPosition(
                Gdx.graphics.getWidth() / 2 - load.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - load.getPrefHeight() / 2
        );
        newGame.setPosition(
                Gdx.graphics.getWidth() / 2 - newGame.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - newGame.getPrefHeight() / 2
        );
    }

    public void updateViewPort(int width, int height) {
        getViewport().update(width, height, true);
        getCamera().update();
        recalcPos();
    }
}
