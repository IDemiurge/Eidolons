package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.Obj;
import main.game.logic.battle.player.Player;
import main.system.audio.MusicMaster;
import main.system.audio.MusicMaster.MUSIC_MOMENT;

public abstract class BattleOutcomeManager<E extends Battle> extends BattleHandler<E> {
    Boolean outcome;

    public BattleOutcomeManager(BattleMaster<E> master) {
        super(master);
    }

    public Boolean getOutcome() {
        return outcome;
    }


    public void end() {
        // battle.setOutcome(outcome);
        game.stop();
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


    public void checkOutcomeClear() {
        if (checkVictory())
            victory();
        else if (checkDefeat())
            defeat();
    }

    protected boolean checkDefeat() {
        return checkNoPlayerUnitsLeft();
    }

    protected boolean checkVictory() {
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
            if (!d.isDead()) {
                return false;
            }
            // panicked? preCheck ownership change?
        }
        return true;
    }


}
