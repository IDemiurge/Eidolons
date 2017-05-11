package main.game.battlecraft.logic.battle.arena;

import main.game.battlecraft.logic.battle.Battle;
import main.game.battlecraft.logic.battle.BattleMaster;
import main.game.battlecraft.logic.battle.BattleOutcomeManager;
import main.game.battlecraft.logic.meta.PartyManager;

/**
 * Created by JustMe on 5/8/2017.
 */
public class ArenaOutcomeManager<E extends Battle> extends BattleOutcomeManager<E> {

    public static final float VICTORY_GLORY_FACTOR = 1.5f;
    public static final int DEFAULT_GLORY_BONUS = 25;
    private static final int DEFEAT_PENALTY = 2;
    private static final int MIN_SURRENDER_GLORY_PENALTY = 1;

    public ArenaOutcomeManager(BattleMaster<E> master) {
        super(master);
    }
    public void defeat(boolean surrender, boolean end) {
        // TODO last level doesn't support saving! Disconnects perhaps, for the
        // dishonorable ones :)
//        glory = calculateGlory(false);
//        if (surrender) {
//            glory = Math.max(0, glory - getBattle().getLevel() * DEFEAT_PENALTY);
//            glory -= getBattle().getLevel() * MIN_SURRENDER_GLORY_PENALTY;
//        }
//        PartyManager.getParty().subtractGlory(glory);
//
//        if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//            game.getArenaArcadeMaster().levelLost();
//        }
//
//        CharacterCreator.getHeroManager(true).afterDefeatRewind();
//        saveParty();
//        outcome = false;
        // MusicMaster.playMoment(MUSIC_MOMENT.DEFEAT);
        if (end) {
            end();
        }
    }
    private int calculateGlory(boolean outcome) {
        int glory = 0;
//        for (Unit unit : game.getBattleManager().getSlainEnemies()) {
//            glory += unit.getIntParam(PARAMS.POWER);
//        }
//        for (Unit unit : game.getBattleManager().getSlainHeroes()) {
//            glory -= unit.getIntParam(PARAMS.POWER);
//        }
//
//        if (outcome) {
//            glory += DEFAULT_GLORY_BONUS;
//            glory *= VICTORY_GLORY_FACTOR;
//        }
//        if (!outcome) {
//            glory
//             // =Math.min(0, glory); Math.max(0, glory)
//             -= getBattle().getLevel() * DEFEAT_PENALTY;
//        }
//        game.getBattleManager().setGlory(glory);
        return glory;

    }

    public void victory() {
        int glory = calculateGlory(true); // set for battle?
//        if (CharacterCreator.isArcadeMode()) {
//            PartyManager.getParty().addGlory(glory);

//            PartyManager.getParty().addUnitsSlain(new LinkedList<>(slainUnits));
//            PartyManager.getParty().addFallenHeroes(new LinkedList<>(fallenHeroes));
//            CharacterCreator.getHeroManager(true).prebattleCleanSave();
//            if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//                game.getArenaArcadeMaster().levelWon();
//            } else {
//                game.getArcadeManager().dungeonComplete();
//            }
//            saveParty();
//        } else {
//            LootMaster.battleIsWon(game); // TODO
//
//        }
//        outcome = true;
//        MusicMaster.playMoment(MUSIC_MOMENT.VICTORY);
        end();
        // final prize dialogue
        // stats screen - keep a log on everything party does!
        // unlock stuff for the party

    }

    public void checkOutcomeClear() {
//        if (!DC_Game.game.isDebugMode()) {
//            if (checkNoEnemiesLeft()) {
//                if (game.getGameMode() != GAME_MODES.ARENA) {
//                    victory();
//                } else if (game.getBattleMaster().getSpawner().getScheduledWaves().isEmpty()) {
//                    if (!nextWaveGroup()) {
//                        victory();
//                    }
//                }
//            }
//        }
    }

    private boolean nextWaveGroup() {
//        if (!game.getBattleMaster().getBattleConstructor().construct()) {
//            return false;
//        }
//
//        game.getBattleMaster().getSpawner().waveCleared();
        return true;
    }
    private void saveParty() {
        PartyManager.saveParty();
    }


}
