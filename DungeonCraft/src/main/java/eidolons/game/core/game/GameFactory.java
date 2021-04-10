package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;

/**
 * Created by JustMe on 5/9/2017.
 */
public class GameFactory {

    public static DC_Game createGame(GAME_SUBCLASS subclass) {
        switch (subclass) {
            case SCENARIO:
                return new DC_Game() {
                    @Override
                    protected DungeonMaster createDungeonMaster() {
                        return new LocationMaster(this);
                    }

                    @Override
                    protected MissionMaster createBattleMaster() {
                        return new QuestMissionMaster(this);
                    }
                };
            case TEST:
                return new DC_Game();

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
