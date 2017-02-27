package main.game.logic.battle.turn;

import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.rules.mechanics.WaitRule;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.threading.Weaver;
import main.test.TestMaster;
import main.test.frontend.FAST_DC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public class DC_TurnManager implements TurnManager, Comparator<Unit> {
    private static boolean visionInitialized;
    private DequeImpl<Unit> unitQueue;
    private DequeImpl<Unit> displayedUnitQueue;
    private DC_Game game;
    private Unit activeUnit;
    private boolean retainActiveUnit;
    private boolean started;

    public DC_TurnManager(DC_Game game) {
        this.game = game;
        // init();

    }

    public static boolean isVisionInitialized() {
        return visionInitialized;
    }

    public static void setVisionInitialized(boolean visionInitialized) {
        DC_TurnManager.visionInitialized = visionInitialized;
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

    public boolean doCycle() {
        // game.getState().newRound();
        resetQueue();

        if (getUnitQueue().isEmpty()) {

            return false;
        }

        boolean result;

        result = chooseUnit();


        Weaver.inNewThread(new Runnable() {
            public void run() {
                resetCosts();
            }
        });

        resetDisplayedQueue();
        result &= activeUnit.turnStarted();
        if (!result) {
            return true; // if killed or immobilized...
        }
        // if (game.isStarted())
        // game.getManager().refreshGUI();
        // else

        return waitForUnitTurn();
    }

    private Boolean waitForUnitTurn() {
        Boolean result;
        // timer

        while (game.isPaused()) {
            WaitMaster.WAIT(100);
        }

        if (isUnitAI_Controlled(activeUnit)) {
            game.getAiManager().makeAction(activeUnit);
        } else {
            game.getMovementManager().promptContinuePath(activeUnit);
        }

        // else
        result = (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.TURN_CYCLE); // failsafe?

        // == INTERRUPTED ANOTHER_TURN NORMAL
        if (game.isStarted()) {
            resetInitiative(false);
            game.getManager().deselectActive();
        }

        return result;
    }

    private boolean isUnitAI_Controlled(Unit activeUnit2) {
        if (activeUnit.getMode().isBehavior()) {
            return true;
        }
        if (!game.getAiManager().getAI(activeUnit).getForcedActions().isEmpty()) {
            return true;
        }
        if (game.isRunning()) {
            if (activeUnit.isAiControlled()) {
                return true;
            }
        }
        return false;
    }

    public void resetCosts() {
        if (game.getManager().getActiveObj() == null) {
            return;
        }
        for (ACTION_TYPE key : game.getManager().getActiveObj().getActionMap()
                .keySet()) {
            for (DC_ActiveObj active : game.getManager().getActiveObj()
                    .getActionMap().get(key)) {
                active.initCosts();
            }
        }

        for (DC_ActiveObj active : game.getManager().getActiveObj().getSpells()) {
            active.initCosts();
        }

        for (DC_QuickItemObj item : game.getManager().getActiveObj().getQuickItems()) {
            if (item.getActive() != null) {
                item.getActive().initCosts();
            }
        }
    }

    private void resetInitiative(boolean first) {
        for (Unit unit : game.getUnits()) {resetInitiative(unit, first);
//            int before = unit.getIntParam(PARAMS.C_INITIATIVE);
//            int after = unit.getIntParam(PARAMS.C_INITIATIVE);
//            if (before == after) return;
//            int diff = before - after;
//            if (diff != 0) {
//                GuiEventManager.trigger(INITIATIVE_CHANGED,
//                 new EventCallbackParam(new ImmutablePair<>(unit, after)));
//            }
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
            if (game.getVisionMaster() .checkDetectedEnemy(unit)) {
                displayedUnitQueue.add(unit);
            }
        }

        try {
            game.getBattleField().refreshInitiativeQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetQueue() {

        unitQueue.clear();
        WaitRule.checkMap();

        for (Unit unit : game.getUnits()) {
            if (TestMaster.isSublevelFreezeOn()) {
                if (game.getMainHero() != null) {
                    if (game.getMainHero().getZ() != unit.getZ()) {
                        continue;
                    }
                }
            }
            if (unit.canActNow()) {
                unitQueue.add(unit);

            }
        }

        ArrayList<Unit> list = new ArrayList<>(getUnitQueue());
        Collections.sort(list, this);
        setUnitQueue(new DequeImpl<>(list));
    }

    public Unit getActiveUnit() {
        return activeUnit;
    }

    public void setActiveUnit(Unit activeUnit) {
        this.activeUnit = activeUnit;
    }

    private boolean chooseUnit() {

        setActiveUnit(unitQueue.peek());
        try {
            if (!game.getManager().activeSelect(getActiveUnit())) {
                return false;
            }
            if (CoreEngine.isSwingOn()) {
                if (game.getManager().getInfoObj() == null) {
                    game.getManager().infoSelect(activeUnit);
                }
            }

//            WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
            LogMaster.gameInfo(StringMaster.getStringXTimes(50 - getActiveUnit().toString().length(), ">")
                    + "Active unit: " + getActiveUnit());
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, new EventCallbackParam(activeUnit));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int getTimeModifier() {
        return game.getRules().getTimeRule().getTimePercentageRemaining();
    }

    @Override
    public boolean makeTurn() {
        resetInitiative(true);
        game.getRules().getTimeRule().newRound();
        Boolean result = false;
        if (game.isStarted()) {
            SoundMaster.playStandardSound(STD_SOUNDS.DEATH);
        } else {
            SoundMaster.playStandardSound(STD_SOUNDS.FIGHT);
        }
        if (isStarted()) {
            if (!playerHasActiveUnits()) {
                LogMaster
                        .log(1,
                                "************** GAME PAUSED WHILE NO UNITS UNDER PLAYER CONTROL **************");
            }

            while (!playerHasActiveUnits()) {
                WaitMaster.WAIT(1000);
                resetQueue();
            }
        }
        while (true) {
            try {
                result = doCycle();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                game.getManager().setActivatingAction(null);
            }
            if (result == null) {
                retainActiveUnit = true;
            } else {
                retainActiveUnit = false;
                if (!result) {
                    break;
                }
            }
        }

        try {
            game.getManager().endTurn();
        } catch (Exception e) {
            if (e instanceof ConcurrentModificationException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        try {
            game.getStateManager().newRound();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result; // game "loop" exits?
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
        if (game.getState().getRound() == 0) {
            if (FAST_DC.LEADER_MOVES_FIRST) {
                if (FAST_DC.isRunning()) {
                    if (u1.isMine() && u1.isMainHero()) {
                        return -1;
                    }
                    if (u1.isMine() && u2.isMainHero()) {
                        return 1;
                    }

                }
            }

        }
        int a1 = u1.getIntParam(PARAMS.C_INITIATIVE);
        int a2 = u2.getIntParam(PARAMS.C_INITIATIVE);
        // TODO re-random if match?
        // if (a1 == a2) {
        // while (a1 == a2) {
        //
        // resetInitiative(u1);
        // a1 = u1.getIntParam(DC_PARAMS.C_INITIATIVE);
        // }
        //
        // }
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

}
