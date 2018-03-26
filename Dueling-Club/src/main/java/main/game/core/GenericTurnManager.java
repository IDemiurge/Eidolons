package main.game.core;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.mechanics.WaitRule;
import main.game.core.game.DC_Game;
import main.game.logic.battle.turn.TurnManager;
import main.system.audio.DC_SoundMaster;
import main.system.datatypes.DequeImpl;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.ArrayList;
import java.util.List;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public class GenericTurnManager implements TurnManager  {
    protected DequeImpl<Unit> unitQueue;
    protected DequeImpl<Unit> displayedUnitQueue;
    protected DC_Game game;
    protected Unit activeUnit;
    protected DequeImpl<Unit> unitGroup;
    protected boolean retainActiveUnit;
    private static boolean visionInitialized;

    public GenericTurnManager(DC_Game game) {
        this.game = game;
    }

    public static void setVisionInitialized(boolean visionInitialized) {
        GenericTurnManager.visionInitialized = visionInitialized;
    }

    public static boolean isVisionInitialized() {
        return visionInitialized;
    }

    public void init() {
        setUnitQueue(new DequeImpl<>());
        setDisplayedUnitQueue(new DequeImpl<>());
        setActiveUnit(unitQueue.peek());
    }
 

    public Unit getActiveUnit(boolean vision) {
        if (!vision  ) {
            return activeUnit;
        }

        for (Unit unit : getUnitQueue()) {
            if (unit.isMine()) {
                return unit;
            }
        }
        return activeUnit;
    }

    protected boolean playerHasActiveUnits() {
        for (Unit u : getUnitQueue()) {
            if (u.isMine() || (u.isPlayerControlled() && !game.isOffline())) {
                return true;
            }
        }
        return false;
    }

    public Boolean nextAction() {
        //TODO retain active unit
        resetQueue();
        if (getUnitQueue().isEmpty()) {
            resetDisplayedQueue();
            return null;
        }

        boolean result = chooseUnit();

        resetDisplayedQueue();
        result &= activeUnit.turnStarted();
        return result;
    }

    public DequeImpl<Unit> getUnits() {
        if (unitGroup != null)
            return unitGroup;
        return game.getUnits();
    }

    public void resetInitiative(boolean first) {
        
    }

    protected void resetDisplayedQueue() {
        displayedUnitQueue.clear();
        if (retainActiveUnit) {
            if (game.getVisionMaster().checkDetectedEnemy(activeUnit)) {
                unitQueue.remove(activeUnit);
                unitQueue.addFirst(activeUnit);
            }
        }

        for (Unit unit : unitQueue) {
            if (game.getVisionMaster().checkDetectedEnemy(unit)) {
                displayedUnitQueue.add(unit);
            }  
        }


    }

    protected void resetQueue() {
        unitQueue.clear();
        try {
            WaitRule.checkMap();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        addToQueue();
        ArrayList<Unit> list = new ArrayList<>(getUnitQueue());
        sort(list);
        setUnitQueue(new DequeImpl<>(list));

    }

    protected void sort(List<Unit> list) {
    }


    protected void addToQueue() {
    }


    protected boolean chooseUnit() {
        return true;
    }

    @Override
    public int getTimeModifier() {
        return game.getRules().getTimeRule().getTimePercentageRemaining();
    }

    public void newRound() {
        
        game.getRules().getTimeRule().newRound();
        for (Unit sub : getGame().getUnits()) {
            if (getGame().getState().getRound() > 0)
                if (sub.getAI().getEngagementDuration() > 0)
                    sub.getAI().setEngagementDuration(sub.getAI().getEngagementDuration() - 1);

            if (game.isStarted()) {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.DEATH);
            } else {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.FIGHT);
            }
        }
    }


    protected boolean isStarted() {
        return game.getState().getRound() > 1;
    }


    public DC_Game getGame() {
        return game;
    }

    public Unit getActiveUnit() {
        return activeUnit;
    }

    public void setActiveUnit(Unit activeUnit) {
        this.activeUnit = activeUnit;
    }
    @Override
    public DequeImpl<Unit> getUnitQueue() {
        return unitQueue;
    }

    public void setUnitQueue(DequeImpl<Unit> unitQueue) {
        this.unitQueue = unitQueue;
    }

    @Override
    public DequeImpl<Unit> getDisplayedUnitQueue() {
        return displayedUnitQueue;
    }

    public void setDisplayedUnitQueue(DequeImpl<Unit> displayedUnitQueue) {
        this.displayedUnitQueue = displayedUnitQueue;
    }

    public String getTimeString() {
        return null;
    }
}
