package main.game.battlecraft.logic.battle.arena;

import main.game.battlecraft.logic.battle.universal.BattleOptionManager;
import main.game.battlecraft.logic.battle.universal.BattleOptions;
import main.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.logic.meta.universal.PartyHelper;

/**
 * Created by JustMe on 5/7/2017.
 */
public class ArenaOptionsMaster extends BattleOptionManager<ArenaBattle> {


    private static final String DEFAULT_LIVES = "2";
    private static final String DEFAULT_TURNS_BETWEEN_WAVES = "2";
    private static final String DEFAULT_TURNS_TO_PREPARE = "2";
    private static String DEFAULT_DIFFICULTY = DIFFICULTY.DISCIPLE.toString();
    private BattleOptions arenaOptions;

    public ArenaOptionsMaster(ArenaBattleMaster master) {
        super(master);
    }

    private void initArenaOptions() {
        // TODO
        arenaOptions = new BattleOptions();
        arenaOptions.setValue(ARENA_GAME_OPTIONS.NUMBER_OF_HEROES, ""
         +
          PartyHelper.getParty().getMembers() .size());

//        arenaOptions.setValue(ARENA_GAME_OPTIONS.PLAYER_STARTING_SIDE,
//         ArenaPositioner. DEFAULT_SIDE);

//        if (game.getDungeonMaster().getDungeon() != null)
//            arenaOptions.setValue(ARENA_GAME_OPTIONS.BACKGROUND, game.getDungeonMaster()
//                    .getDungeon().getMapBackground());
//        else
//            arenaOptions.setValue(ARENA_GAME_OPTIONS.BACKGROUND,
//                    (DEFAULT_BACKGROUND != null) ? DEFAULT_BACKGROUND : getRandomBackground());

        arenaOptions.setValue(ARENA_GAME_OPTIONS.DIFFICULTY, DEFAULT_DIFFICULTY);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.LIVES, DEFAULT_LIVES);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.TURNS_BETWEEN_WAVES, DEFAULT_TURNS_BETWEEN_WAVES);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.TURNS_TO_PREPARE, DEFAULT_TURNS_TO_PREPARE);
    }


}
