package main.libgdx.screens;

import main.libgdx.DialogScenario;

import java.util.List;

public class MainMenuScreenData extends ScreenData {
    private List<ScreenData> savedGames;
    private List<ScreenData> newGames;

    public MainMenuScreenData(String name) {
        super(ScreenType.MAIN_MENU, name);
    }

    public MainMenuScreenData(String name, List<DialogScenario> dialogScenarios) {
        super(ScreenType.MAIN_MENU, name, dialogScenarios);
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
