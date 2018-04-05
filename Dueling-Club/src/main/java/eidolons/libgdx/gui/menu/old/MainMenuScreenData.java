package eidolons.libgdx.gui.menu.old;

import eidolons.libgdx.DialogScenario;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;

import java.util.List;
import java.util.function.Supplier;

public class MainMenuScreenData extends ScreenData {
    private List<ScreenData> savedGames;
    private List<ScreenData> newGames;

    public MainMenuScreenData(String name) {
        super(ScreenType.MAIN_MENU, name);
    }

    public MainMenuScreenData(String name, Supplier<List<DialogScenario>> factory) {
        super(ScreenType.MAIN_MENU, name, factory);
    }

    public List<ScreenData> getSavedGames() {
        return savedGames;
    }

    public void setSavedGames(List<ScreenData> savedGames) {
        this.savedGames = savedGames;
    }

    public List<ScreenData> getNewGames() {
        return newGames;
    }

    public void setNewGames(List<ScreenData> newGames) {
        this.newGames = newGames;
    }
}
