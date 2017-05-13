package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.mainmenu.GameLoadingPanel;
import main.libgdx.gui.panels.mainmenu.MainMenuPanel;
import main.libgdx.gui.panels.mainmenu.OptionsPanel;

public class MainMenuStage extends Stage {
    private MainMenuPanel menu;
    private OptionsPanel options;
    private GameLoadingPanel load;

    public MainMenuStage() {
        menu = new MainMenuPanel();
        options = new OptionsPanel();
        load = new GameLoadingPanel();

        addActor(menu);
        addActor(options);
        addActor(load);

        menu.setExitCallback(() -> System.exit(0));
        menu.setLoadGameCallback(() -> switchView(load));
        menu.setOptionsCallback(() -> switchView(options));

        options.setBackCallback(() -> switchView(menu));

        load.setBackCallback(() -> switchView(menu));

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
    }

    public void updateViewPort(int width, int height) {
        getViewport().update(width, height, true);
        getCamera().update();
        recalcPos();
    }
}
