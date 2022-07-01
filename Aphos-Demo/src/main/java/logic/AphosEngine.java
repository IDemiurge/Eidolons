package logic;

import logic.core.Game;
import logic.core.LaunchData;

public class AphosEngine {
    private static Game game;

    public static void start() {
        game = new Game(LaunchData.createDefaultData());
    }
}
