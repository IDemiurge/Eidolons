package main.game.core.game;

/**
 * Created by JustMe on 5/9/2017.
 */
public class GameFactory {

    public static DC_Game createAndInitGame(GAME_SUBCLASS subclass) {
        DC_Game game = createGame(subclass);
        game.init();
        return game;
    }

    public static DC_Game createGame(GAME_SUBCLASS subclass) {
        switch (subclass) {
            case TEST:
                return new DC_Game();
            case ARENA:
                return new ArenaGame();
            case SCENARIO:
                return new ScenarioGame();
            case ARCADE:
                return new ArcadeGame();
            case SKIRMISH:
//                return new DC_Game();
        }
        return new DC_Game();
    }

    public enum GAME_SUBCLASS {
        TEST,
        ARENA,
        SCENARIO,
        ARCADE,
        SKIRMISH
    }
}
