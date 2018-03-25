package main.game.core;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.rules.mechanics.WaitRule;
import main.game.core.AtbController.AtbUnit;
import main.game.core.game.DC_Game;
import main.game.logic.battle.turn.TurnManager;
import main.system.GuiEventManager;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;
import static main.system.GuiEventType.UPDATE_UNIT_ACT_STATE;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public class DC_TurnManager implements TurnManager, Comparator<Unit> {
    private static boolean visionInitialized;
    boolean atbMode = DC_Engine.isAtbMode();
    AtbController atbController;
    private DequeImpl<Unit> unitQueue;
    private DequeImpl<Unit> displayedUnitQueue;
    private DC_Game game;
    private Unit activeUnit;
    private boolean retainActiveUnit;
    private boolean started;
    private DequeImpl<Unit> unitGroup;

    public DC_TurnManager(DC_Game game) {
        this.game = game;
        // init();
        atbController = new AtbController(this);
    }

    public static boolean isVisionInitialized() {
        return visionInitialized;
    }

    public static void setVisionInitialized(boolean visionInitialized) {
        DC_TurnManager.visionInitialized = visionInitialized;
    }

    public void setUnitGroup(DequeImpl<Unit> unitGroup) {
        this.unitGroup = unitGroup;
    }

    public void init() {
        initQueue();
        // resetQueue();
    }

    private void initQueue() {
        setUnitQueue(new DequeImpl<>());
        setDisplayedUnitQueue(new DequeImpl<>());
        setActiveUnit(unitQueue.peek());
        // getActiveUnit().setActiveSelected(true);
    }

    public Unit getActiveUnit(boolean vision) {
        if (!vision || visionInitialized) {
            return activeUnit;
        }

        for (Unit unit : getUnitQueue()) {
            if (unit.isMine()) {
                return unit;
            }
        }
        return activeUnit;
    }

    private boolean playerHasActiveUnits() {
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
//TODO if (OptionsMaster.getEngineOptions().getBooleanValue(ENGINE_OPTION.RESET_COSTS))
//        game.getActionManager().resetCostsInNewThread();

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
        if (atbMode)
            return;
        for (Unit unit : getUnits()) {
            if (unit.getAI().isOutsideCombat())
                continue;
            resetInitiative(unit, first);
        }
    }

    private void resetDisplayedQueue() {
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
//                GuiEventManager.trigger(UPDATE_UNIT_VISIBLE, new ImmutablePair<>(unit, true));
            } else {
//                GuiEventManager.trigger(UPDATE_UNIT_VISIBLE, new ImmutablePair<>(unit, false));
            }
        }


    }

    private void resetQueue() {
        unitQueue.clear();
        try {
            WaitRule.checkMap();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        for (Unit unit : getUnits()) {
            final boolean actNow = unit.getAI().isOutsideCombat() ? false : unit.canActNow();
            if (actNow) {
                if (getGame().isDummyMode() && getGame().isDummyPlus() && !unit.isMine()) {
                    //?
                } else {
                    unitQueue.add(unit);
                    if (atbMode)
                        atbController.addUnit(unit);
                }
            } else {
                if (atbMode) atbController.removeUnit(unit);
            }
            if (!unit.getAI().isOutsideCombat())
                GuiEventManager.trigger(UPDATE_UNIT_ACT_STATE, new ImmutablePair<>(unit, actNow));
        }
        if (atbMode) {
            atbController.processAtbRelevantEvent();
        }
        ArrayList<Unit> list = new ArrayList<>(getUnitQueue());
        Collections.sort(list,
         atbMode ? atbController :
          this);
        setUnitQueue(new DequeImpl<>(list));

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

    private boolean chooseUnit() {
        if (atbMode) {
            AtbUnit unit = atbController.step();
            while (unit == null) {
                //TODO integrate properly!
                unit = atbController.step();
            }
            if (unit != null) {
                setActiveUnit(unit.getUnit());
                if (!game.getManager().activeSelect(getActiveUnit())) {
                    return false;
                }
                return true;
            } else {
//                setActiveUnit(atbController);
                return false;
            }
        }


        setActiveUnit(unitQueue.peek());
//        for (Unit sub : unitQueue) { TODO tried to avoid same-initiative on display...
//            if (sub != getActiveUnit())
//                if (sub.getIntParam(PARAMS.C_INITIATIVE) == getActiveUnit().getIntParam(PARAMS.C_INITIATIVE)) {
//                    getActiveUnit().modifyParameter(PARAMS.C_INITIATIVE, 1, null, true);
//
//                }
//            break;
//        }
        try {
            if (!game.getManager().activeSelect(getActiveUnit())) {
                return false;
            }
            if (CoreEngine.isSwingOn()) {
                if (game.getManager().getInfoObj() == null) {
                    game.getManager().infoSelect(activeUnit);
                }
            }

            LogMaster.gameInfo(StringMaster.getStringXTimes(50 - getActiveUnit().toString().length(), ">")
             + "Active unit: " + getActiveUnit());
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return true;
    }

    @Override
    public int getTimeModifier() {
        return game.getRules().getTimeRule().getTimePercentageRemaining();
    }

    public void newRound() {
        if (atbMode) {
            atbController.newRound();

        } else {
            resetInitiative(true);
        }
//        resetQueue();
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


    private boolean isStarted() {
        return game.getState().getRound() > 1;
    }

    private void resetInitiative(Unit unit, boolean first) {
        if (first) {
            int amplitude = unit.getIntParam(PARAMS.INITIATIVE_BONUS);
            int initiativeBonus = RandomWizard.getRandomInt(amplitude);
            initiativeBonus += unit.getIntParam(PARAMS.C_INITIATIVE_TRANSFER);
            unit.setParam(PARAMS.C_INITIATIVE_TRANSFER, 0);// TODO percent?
            unit.setParam(PARAMS.C_INITIATIVE_BONUS, initiativeBonus);

        }
        unit.recalculateInitiative();
    }

    @Override
    public int compare(Unit u1, Unit u2) {
        int a1 = u1.getIntParam(PARAMS.C_INITIATIVE);
        int a2 = u2.getIntParam(PARAMS.C_INITIATIVE);
        if (a1 > a2) {
            return -1;
        }
        return 1;
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

    public AtbController getAtbController() {
        return atbController;
    }
}
