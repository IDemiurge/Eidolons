package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.logic.battle.player.Player;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.audio.MusicMaster;
import main.system.audio.MusicMaster.MUSIC_MOMENT;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

public class BattleOutcomeManager<E extends Battle> extends BattleHandler<E> {
    Boolean outcome;
    private Integer roundLimit = null;
    private Boolean timedOutcome = true;

    public BattleOutcomeManager(BattleMaster<E> master) {
        super(master);
    }

    public Boolean getOutcome() {
        return outcome;
    }

    public void restart() {

        getGame().getMetaMaster().next(null  );
    }

    public enum OUTCOME{
        SURRENDER,
        DEFEAT,
    VICTORY,
    RETREAT,
    ENEMY_RETREAT,
    TIMED_VICTORY,
    TIME_DEFEAT,
}

    public void end() {
        // battle.setOutcome(outcome);
        game.stop();
        GuiEventManager.trigger(GuiEventType. GAME_FINISHED, getGame());
        WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, outcome);
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
        outcome = false;
        // MusicMaster.playMoment(MUSIC_MOMENT.DEFEAT);
        if (end) {
            end();
        }
    }

    public void victory() {
        outcome = true;
        MusicMaster.playMoment(MUSIC_MOMENT.VICTORY);
        end();
        // final prize dialogue
        // stats screen - keep a log on everything party does!
        // unlock stuff for the party

    }

    public void next() {
        getGame().getMetaMaster().next(true);
    }

    public void setRoundLimit(Integer roundLimit) {
        this.roundLimit = roundLimit;
    }

    public void setTimedOutcome(Boolean timedOutcome) {
        this.timedOutcome = timedOutcome;
    }

    public Boolean checkTimedOutcome() {
        if (roundLimit != null)
            if (game.getState().getRound() >= roundLimit) {
                if (timedOutcome) {
                    victory();
                } else defeat();

                return timedOutcome;
            }

        return null;
    }

    public boolean checkOutcomeClear() {
        if (checkVictory()) {
            victory();
            return true;
        } else {
            if (checkDefeat()) {
                defeat();
                return true;
            }
        }

        return false;
    }

    protected boolean checkDefeat() {
        if (!OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.MANUAL_CONTROL))
            if (!game.isDebugMode())
       return  (game.getPlayer(true).getHeroObj().isDead());

        return checkNoPlayerUnitsLeft();
    }

    protected boolean checkVictory() {
        if (ExplorationMaster.isExplorationOn())
            return false;
        return checkNoEnemiesLeft();
    }


    public boolean checkNoEnemiesLeft() {
        return checkPlayerHasNoUnits(game.getPlayer(false));

    }

    public boolean checkNoPlayerUnitsLeft() {
        return checkPlayerHasNoUnits(game.getPlayer(true));

    }

    private boolean checkPlayerHasNoUnits(Player player) {
        for (Obj d : player.getControlledUnits()) {
            if (d instanceof Unit){
//                ((Unit) d).isAiControlled()

            }
//            if (player.isMe())
//                if (d.isai)
            if (!d.isDead()) {
                return false;
            }

            // panicked? preCheck ownership change?
        }
        return true;
    }


}
