package eidolons.game.core;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.WaitRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.game.logic.battle.turn.TurnManager;
import main.system.datatypes.DequeImpl;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public abstract class GenericTurnManager implements TurnManager {
    protected DequeImpl<Unit> unitQueue;
    protected DequeImpl<Unit> displayedUnitQueue;
    protected DC_Game game;
    protected Set<Unit> unitGroup;
    protected boolean retainActiveUnit;

    public GenericTurnManager(DC_Game game) {
        this.game = game;
    }

    public void init() {
        setUnitQueue(new DequeImpl<>());
        setDisplayedUnitQueue(new DequeImpl<>());
    }


    public Unit getActiveUnit(boolean vision) {
        if (!vision) {
            return getActiveUnit();
        }

        for (Unit unit : getUnitQueue()) {
            if (unit.isMine()) {
                return unit;
            }
        }
        return getActiveUnit();
    }

    public Boolean nextAction() {
        //TODO retain active unit
        resetQueue();
        if (getUnitQueue().isEmpty()) {
            resetDisplayedQueue();
            return null;
        }

        Boolean result = chooseUnit();
        if (result==null )
            return null;
        resetDisplayedQueue();

        if (getActiveUnit() !=null )
        {
            if (getActiveUnit().getBuff("channeling")==null) {
             result &= getActiveUnit().turnStarted();
            }
        }
        return result;
    }

    public Set<Unit> getUnits() {
        if (unitGroup != null)
            return unitGroup;
        return game.getUnits();
    }

    public void resetInitiative(boolean first) {

    }

    protected void resetDisplayedQueue() {
        displayedUnitQueue.clear();
        if (retainActiveUnit) {
            if (game.getVisionMaster().checkDetectedEnemy(getActiveUnit())) {
                unitQueue.remove(getActiveUnit());
                unitQueue.addFirst(getActiveUnit());
            }
        }

        //            if (game.getVisionMaster().checkDetectedEnemy(unit))
        displayedUnitQueue.addAll(unitQueue);


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


    protected Boolean chooseUnit() {
        return true;
    }


    public void newRound() {

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

    public abstract Float getTotalTime();

    @Override
    public Unit getActiveUnit() {
        return game.getLoop().getActiveUnit();
    }
    public void setActiveUnit(Unit activeUnit) {
          game.getLoop().setActiveUnit(activeUnit);
    }
}
