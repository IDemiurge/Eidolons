package eidolons.libgdx.gui.menu.old;

import eidolons.libgdx.gui.panels.mainmenu.NewGamePanel;
import eidolons.libgdx.gui.panels.mainmenu.StartMenuPanel;
import eidolons.libgdx.stage.DataStage;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.mainmenu.GameLoadingPanel;
import eidolons.libgdx.gui.panels.mainmenu.OptionsPanel;
import eidolons.libgdx.screens.ScreenData;

import java.util.function.Consumer;

public class MainMenuStage extends DataStage {
    private StartMenuPanel menu;
    private OptionsPanel options;
    private GameLoadingPanel load;
    private NewGamePanel newGame;

    public MainMenuStage() {
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

        switchView(menu);
    }

    public void setLoadGameCallback(Consumer<ScreenData> menuCallback) {
        newGame.setStartDemoScenarioCallback(menuCallback);
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
         -menu.getPrefWidth() / 2,
         -menu.getPrefHeight() / 2
        );
        options.setPosition(
         -options.getPrefWidth() / 2,
         -options.getPrefHeight() / 2
        );
        load.setPosition(
         -load.getPrefWidth() / 2,
         -load.getPrefHeight() / 2
        );
        newGame.setPosition(
         -newGame.getPrefWidth() / 2,
         -newGame.getPrefHeight() / 2
        );
    }

    @Override
    public void setData(ScreenData data) {
        super.setData(data);
        final MainMenuScreenData menuScreenData = (MainMenuScreenData) data;
        newGame.setUserObject(menuScreenData.getNewGames());
    }

    public void updateViewPort(int width, int height) {
        recalcPos();
    }

}
