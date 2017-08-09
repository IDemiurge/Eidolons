package main.game.core.game;

import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;

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
            case SCENARIO:
                return new DC_Game(){
                    @Override
                    protected DungeonMaster createDungeonMaster() {
                        return new LocationMaster(this);
                    }

                    @Override
                    protected BattleMaster createBattleMaster() {
                        return new MissionBattleMaster(this);
                    }
                };
            case TEST:
                return new DC_Game();
            case ARENA:
                return new ArenaGame();
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
