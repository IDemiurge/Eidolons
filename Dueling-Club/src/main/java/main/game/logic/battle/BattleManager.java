package main.game.logic.battle;

import main.client.battle.gui.EndDialogue;
import main.client.cc.CharacterCreator;
import main.content.PARAMS;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.PartyManager;
import main.game.logic.macro.travel.Encounter;
import main.game.logic.macro.travel.EncounterMaster;
import main.game.logic.macro.travel.LootMaster;
import main.system.audio.MusicMaster;
import main.system.audio.MusicMaster.MUSIC_MOMENT;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.LinkedList;

public class BattleManager {
    public static final float VICTORY_GLORY_FACTOR = 1.5f;
    public static final int DEFAULT_GLORY_BONUS = 25;
    private static final int DEFEAT_PENALTY = 2;
    private static final int MIN_SURRENDER_GLORY_PENALTY = 1;
    private static float GLORY_FACTOR = 1;
    Boolean outcome;
    Battle battle;
    private DC_Game game;
    private int glory;
    private DequeImpl<Unit> slainUnits = new DequeImpl<>();
    private DequeImpl<Unit> ratedEnemyUnitsSlain = new DequeImpl<>();
    private DequeImpl<Unit> fallenHeroes = new DequeImpl<>();
    private DequeImpl<Unit> slainPlayerUnits = new DequeImpl<>();
    private Encounter encounter;

    public BattleManager(DC_Game game) {
        this.game = game;
        this.battle = game.getArenaManager().getBattle();
    }

    public static float getGloryFactor() {
        return GLORY_FACTOR;
    }

    public void end() {
        // battle.setOutcome(outcome);
        game.stop();
        if (EncounterMaster.isEncounterBeingResolved()) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.BATTLE_FINISHED, outcome);
        }// boolean override = (boolean) WaitMaster
        // .waitForInputIfWaiting(WAIT_OPERATIONS.BATTLE_FINISHED);
        // if (!override)
        else {
            initDialogue();
        }
    }

    public void exited() {
        defeat(true, false);
    }

    public void surrender() {
        defeat(true, true);
    }

    public void defeat() {
        defeat(false, true);
    }

    public void defeat(boolean surrender, boolean end) {
        // TODO last level doesn't support saving! Disconnects perhaps, for the
        // dishonorable ones :)
        glory = calculateGlory(false);
        if (surrender) {
            glory = Math.max(0, glory - getBattle().getLevel() * DEFEAT_PENALTY);
            glory -= getBattle().getLevel() * MIN_SURRENDER_GLORY_PENALTY;
        }
        PartyManager.getParty().subtractGlory(glory);

        if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
            game.getArenaArcadeMaster().levelLost();
        }

        CharacterCreator.getHeroManager(true).afterDefeatRewind();
        saveParty();
        outcome = false;
        // MusicMaster.playMoment(MUSIC_MOMENT.DEFEAT);
        if (end) {
            end();
        }
    }

    public void victory() {
        glory = calculateGlory(true); // set for battle?
        if (CharacterCreator.isArcadeMode()) {
            PartyManager.getParty().addGlory(glory);

            PartyManager.getParty().addUnitsSlain(new LinkedList<>(slainUnits));
            PartyManager.getParty().addFallenHeroes(new LinkedList<>(fallenHeroes));
            CharacterCreator.getHeroManager(true).prebattleCleanSave();
            if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
                game.getArenaArcadeMaster().levelWon();
            } else {
                game.getArcadeManager().dungeonComplete();
            }
            saveParty();
        } else {
            LootMaster.battleIsWon(game); // TODO

        }
        outcome = true;
        MusicMaster.playMoment(MUSIC_MOMENT.VICTORY);
        end();
        // final prize dialogue
        // stats screen - keep a log on everything party does!
        // unlock stuff for the party

    }

    private void award() {
        // if (game. )
        // levelUp();
        // game.getDungeonMaster().getDungeon().getParam(PARAMS.GOLD_REWARD);
        // difficulty?
        // loot? :) the simplest of custom-generated items would be great fun!
        // glory * 2 + glory*glory/(100*sqrt(glory))
        // PartyManager.awardXp();
    }

    private void saveParty() {
        PartyManager.saveParty();
    }

    private int calculateGlory(boolean outcome) {
        int glory = 0;
        for (Unit unit : game.getBattleManager().getSlainEnemies()) {
            glory += unit.getIntParam(PARAMS.POWER);
        }
        for (Unit unit : game.getBattleManager().getSlainHeroes()) {
            glory -= unit.getIntParam(PARAMS.POWER);
        }

        if (outcome) {
            glory += DEFAULT_GLORY_BONUS;
            glory *= VICTORY_GLORY_FACTOR;
        }
        if (!outcome) {
            glory
                    // =Math.min(0, glory); Math.max(0, glory)
                    -= getBattle().getLevel() * DEFEAT_PENALTY;
        }
        game.getBattleManager().setGlory(glory);
        return glory;

    }

    // private void applyResults() {
    // glory = calculateGlory(); // set for battle?
    // if (CharacterCreator.isArcadeMode()) {
    // PartyManager.getParty().addGlory(glory);
    // levelUp();
    // saveParty();
    // }
    //
    // }

    // private void saveParty() {
    // PartyManager.saveParty();
    // }
    //
    // private void levelUp() {
    // game.setSimulation(true);
    // PartyManager.levelUp();
    // game.setSimulation(false);
    //
    // }
    //
    // private int calculateGlory() {
    // int glory = 0;
    // for (DC_HeroObj unit : getSlainEnemies()) {
    // glory += unit.getIntParam(PARAMS.POWER) * GLORY_FACTOR;
    // }
    // glory += DEFAULT_GLORY_BONUS;
    // if (outcome)
    // glory *= VICTORY_GLORY_FACTOR;
    // return glory;
    //
    // }

    private void initDialogue() {
        new EndDialogue(this).show();
    }

    public DequeImpl<Unit> getSlainEnemies() {
        // TODO remember the killer too
        // filter enemy units?
        return slainUnits;
    }

    // private void initEndScreen(boolean b, int glory,
    // DequeImpl<DC_HeroObj> dequeImpl) {
    // EndScreen screen = new EndScreen(game, b, glory, dequeImpl);
    // // Launcher.resetView(screen.getPanel(), VIEWS.END);
    // }

    public void unitDies(Unit killed) {
        if (killed.getGame().isDummyMode()
                // TODO until fixed
                || true
                ) {
            return;
        }
        if (killed.getOriginalOwner().isMe()) {
            if (killed.isHero() && isRated(killed)) {
                if (!fallenHeroes.contains(killed)) {
                    fallenHeroes.add(killed);
                }
            } else {
                if (!slainPlayerUnits.contains(killed)) {
                    slainPlayerUnits.add(killed);
                }
            }
            if (checkNoPlayerUnitsLeft()) {
                defeat();
            }
        }
        // TODO temp
        if (!killed.getOriginalOwner().isMe()) {
            if (isRated(killed)) {
                if (!ratedEnemyUnitsSlain.contains(killed)) {
                    ratedEnemyUnitsSlain.add(killed);
                }
            }
            if (!slainUnits.contains(killed)) {
                slainUnits.add(killed);
            }
            checkClear();
        }

    }

    public DequeImpl<Unit> getRatedEnemyUnitsSlain() {
        return ratedEnemyUnitsSlain;
    }

    private boolean isRated(Unit killed) {
        // TODO not summoned
        return killed.getRef().getObj(KEYS.SUMMONER) == null;
    }

    private void checkClear() {
        if (!DC_Game.game.isDebugMode()) {
            if (checkNoEnemiesLeft()) {
                if (game.getGameMode() != GAME_MODES.ARENA) {
                    victory();
                } else if (game.getArenaManager().getSpawnManager().getScheduledWaves().isEmpty()) {
                    if (!nextWaveGroup()) {
                        victory();
                    }
                }
            }
        }
    }

    private boolean nextWaveGroup() {
        if (!game.getArenaManager().getBattleConstructor().construct()) {
            return false;
        }

        game.getArenaManager().getSpawnManager().waveCleared();
        // int n = game.getArenaManager().getArenaOptions()
        // .getIntValue(ARENA_GAME_OPTIONS.TURNS_BETWEEN_WAVES);
        // game.getArenaManager().getSpawnManager().setRoundsToWait(n);
        // game.getArenaManager().getSpawnManager().setFighting(false);
        return true;
    }

    public boolean checkNoEnemiesLeft() {
        return checkPlayerHasNoUnits(game.getPlayer(false));

    }

    public boolean checkNoPlayerUnitsLeft() {
        return checkPlayerHasNoUnits(game.getPlayer(true));

    }

    private boolean checkPlayerHasNoUnits(Player player) {
        for (Obj d : player.getControlledUnits()) {
            if (!d.isDead()) {
                return false;
            }
            // panicked? check ownership change?
        }
        return true;
    }

    public DC_Game getGame() {
        return game;
    }

    public Boolean getOutcome() {
        return outcome;
    }

    public int getGlory() {
        return glory;
    }

    public void setGlory(int glory) {
        this.glory = glory;
    }

    public Battle getBattle() {
        if (battle == null) {
            this.battle = game.getArenaManager().getBattle();
        }
        return battle;
    }

    public DequeImpl<Unit> getSlainHeroes() {
        return fallenHeroes;
    }

    public DequeImpl<Unit> getSlainPlayerUnits() {
        return slainPlayerUnits;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

}
