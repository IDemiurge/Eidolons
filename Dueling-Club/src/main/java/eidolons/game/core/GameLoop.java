package eidolons.game.core;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingRunner;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.rules.combat.misc.ChargeRule;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.gui.generic.GearActor;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.auxiliary.secondary.Bools;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;


/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {

    protected Lock lock = new ReentrantLock();
    protected Condition waiting = lock.newCondition();

    protected Unit activeUnit;
    protected DC_Game game;
    protected DC_ActiveObj activatingAction;
    protected boolean paused;
    protected boolean aiFailNotified;
    protected boolean aftermath;
    protected boolean skippingToNext;
    protected boolean nextLevel;
    protected boolean exited;
    protected DequeImpl<ActionInput> playerActionQueue = new DequeImpl<>();
    protected Thread thread;
    protected boolean started;
    private boolean stopped;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public GameLoop() {

    }

    public void start() {
        stopped = false;
        if (!CoreEngine.isGraphicsOff()) {
            WaitMaster.waitForInputIfNotWaiting(WAIT_OPERATIONS.GUI_READY);
        }

        WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_STARTED, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GAME_LOOP_STARTED);

        while (true) {
            //for JUnit
            if (exited)
                break;
            if (game.getUnits().isEmpty()) {
                continue;
            }
            try {
                if (!roundLoop())
                    break;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
        loopExited();
        return;
    }

    protected void loopExited() {
        LogMaster.log(1, "Game Loop exit " + this);
        if (AiTrainingRunner.running) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, false);
        }

        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GAME_LOOP_STARTED);
        main.system.auxiliary.log.LogMaster.log(1, this + " exited!");
        setExited(false);

        if (game.getLoop() == this)
            SpecialLogger.getInstance().appendSpecialLog(
             SPECIAL_LOG.EXCEPTIONS, "game loop exits without new loop running!");
    }

    public Thread startInNewThread() {
        if (thread != null) {
            LogMaster.log(1, "Game Loop ALREADY started " + this);
            try {
                thread.interrupt();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        thread = new Thread(() -> {
            start();
        }, getThreadName());

        thread.start();
        LogMaster.log(1, "Game Loop started " + this);

        started = true;
        return thread;
    }

    protected String getThreadName() {
        return "Game Loop";
    }

    protected boolean roundLoop() {
        game.getStateManager().newRound();
        while (true) {
            if (stopped) {
                lock();
            }
            if (exited)
                return false;
            if (ExplorationMaster.isExplorationOn()) {
                main.system.auxiliary.log.LogMaster.log(1, "COMBAT LOOP EXITS FROM EXPLORE!");
                lock();
            }
            Boolean result = game.getTurnManager().nextAction();
            if (result == null) {
                break;
            }
            if (!result) {
                if (DC_Engine.isAtbMode()) {
                    getGame().getManager().reset();
                }
                continue;
            }
            //            if (!retainActiveUnit)
            //                activeUnit = game.getTurnManager().getActiveUnit();
            //            retainActiveUnit = false;

            if (activeUnit == null) {
                break;
            }
            if (!started) {
                VisionManager.refresh();
                started = true;
            }
            result = makeAction();
            if (!aftermath)
                if (game.getBattleMaster().getOutcomeManager().checkOutcomeClear()) {
                    return false;
                }
            if (result == null) {
                continue;
            }
            if (result) {
                //end round
                break;
            }
        }
        return game.getManager().endRound();
    }


    public DC_Game getGame() {
        return game;
    }

    /**
     * @return true if round must end, null if active unit is to be retained
     */
    protected Boolean makeAction() {
        if (exited)
            return true;
        Boolean result = null;
        ActionInput action = null;
        boolean channeling = false;
        if (!playerActionQueue.isEmpty()) {
            action = (playerActionQueue.removeLast());
        } else if (activeUnit.getHandler().getChannelingSpellData() != null) {
            action = activeUnit.getHandler().getChannelingSpellData();
            channeling = true;
        } else if (activeUnit.isAiControlled()) {
            //SHOWCASE SECURITY
            try {
                action = (waitForAI());
                AI_Manager.setOff(false);
            } catch (Exception e) {
                AI_Manager.setOff(true);
                if (!aiFailNotified) {
                    main.system.auxiliary.log.LogMaster.log(1, ("AI failed!!!!"));
                    aiFailNotified = true;
                    return false;
                }
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else {
            action = (waitForPlayerInput());
        }
        if (channeling)
            ChannelingRule.channelingResolves(activeUnit);

        waitForAnimations(action);

        result =
         activateAction(action);
        if (exited)
            return true;
        waitForPause();
        return result;
    }


    protected void waitForPause() {
        if (paused) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE);
            //      not very smart      paused = false;
        }
    }

    protected void waitForAnimations(ActionInput action) {
        if (isMustWaitForAnim(action)) {
            int maxTime = getMaxAnimWaitTime(action);
            int minTime = getMinAnimWaitTime(action);
            //*speed ?
            int period = getAnimWaitPeriod();
            int waitTime = 0;
            while (waitTime < minTime || (isMustWaitForAnim(action) &&  waitTime< maxTime)){
                WaitMaster.WAIT(period);
                waitTime += period;
                main.system.auxiliary.log.LogMaster.log(1, toString() + " waited for anim to draw: " + waitTime);

            }
        }
    }

    protected int getMinAnimWaitTime(ActionInput action) {
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MIN_ANIM_WAIT_TIME_COMBAT);
    }

    protected int getMaxAnimWaitTime(ActionInput action) {
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME_COMBAT);
    }

    protected int getAnimWaitPeriod() {
        return 50;
    }

    protected boolean isMustWaitForAnim(ActionInput action) {
        return AnimMaster.getInstance().isDrawing();
    }

    protected void waitForAnimationsOld() {
        Integer MAX_ANIM_TIME =
         OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
        if (MAX_ANIM_TIME != null) {
            if (MAX_ANIM_TIME > 0) {
                if (AnimMaster.getInstance().isDrawing()) {
                    WaitMaster.waitForInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, MAX_ANIM_TIME);
                }
            }
        }
    }

    protected Boolean activateAction(ActionInput input) {
        if (input == null) {
            return true;
        }
        if (input.getContext().getSourceObj().isDead())
            return false;
        boolean result;
        try {
            activatingAction = input.getAction();
            activatingAction.setTargetObj(input.getContext().getTargetObj());
            activatingAction.setTargetGroup(input.getContext().getGroup());
            result = input.getAction().getHandler().activateOn(input.getContext());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return null;
        } finally {
            activatingAction = null;
        }
        if (!result) {
            return false;
        }
        return checkEndRound(input);
    }

    protected Boolean checkEndRound(ActionInput input) {
        int timeCost = input.getAction().getHandler().getTimeCost();
        Boolean endTurn = getGame().getRules().getTimeRule().
         actionComplete(input.getAction(), timeCost);
        if (!endTurn) {
            game.getManager().reset();
            if (ChargeRule.checkRetainUnitTurn(input.getAction())) {
                endTurn = null;
            }
        }

        if (Bools.isTrue(endTurn)) {
            return true;
        } else {
            game.getTurnManager().
             resetInitiative(false);
        }
        return endTurn;
    }

    protected ActionInput waitForAI() {
        Action aiAction =
         game.getAiManager().getAction(game.getManager().getActiveObj());
        boolean failed = false;
        if (aiAction == null)
            failed = true;
        else if (!aiAction.getActive().isChanneling())
            if (!aiAction.canBeTargeted()) {
                {
                    AI_Manager.getBrokenActions().add(aiAction.getActive());
                    failed = true;
                }

            }
        if (failed)
            aiAction = game.getAiManager().getDefaultAction(getActiveUnit());
        return new ActionInput(aiAction.getActive(), new Context(aiAction.getRef()));
    }

    protected ActionInput waitForPlayerInput() {
        ActionInput input = (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_INPUT);
        System.out.println("Player action input: " + input);
        return input;
    }

    public DC_ActiveObj getActivatingAction() {
        return activatingAction;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        setPaused(paused, false);
    }

    public void setPaused(boolean paused, boolean logged) {
        if (!game.isStarted()) {
            return;
        }
        if (logged)
            if (CoreEngine.isIDE())
                game.getLogManager().log(paused ? "Game paused" : "Game resumed");
        if (paused)
            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Game Paused");
        else GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Game Resumed");
        GearActor.setPaused(paused);
        this.paused = paused;
        SuperActor.setAlphaFluctuationOn(!paused);
        if (!paused)
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE, true);
    }


    public void queueActionInput(ActionInput actionInput) {
        playerActionQueue.add(actionInput);
    }

    public void actionInput(ActionInput actionInput) {
        if (AI_Manager.isRunning())
            EUtils.showInfoText(RandomWizard.random() ? "The enemy has the initiative!" : "The enemy has initiative...");
        if (isPaused()) {
            EUtils.showInfoText(RandomWizard.random() ? "The game is Paused!" : "Game is paused now...");
            return;
        }

        WaitMaster.receiveInputIfWaiting(WAIT_OPERATIONS.ACTION_INPUT, actionInput, false);
    }

    public Unit getActiveUnit() {
        if (activeUnit == null)
            return Eidolons.getMainHero();
        return activeUnit;
    }

    public void setActiveUnit(Unit activeUnit) {
        this.activeUnit = activeUnit;
        if (activeUnit != null)
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);

    }

    protected boolean checkNextLevel() {
        if (nextLevel) {
            nextLevel = false;
            return true;
        }
        if (!(this instanceof ExploreGameLoop)) {
            if (!game.isDebugMode())
                return false;
        }
        Coordinates c = game.getPlayer(true).getHeroObj().getCoordinates();
        Location location = (Location) game.getDungeonMaster().getDungeonWrapper();
        if (location.getMainExit() != null)
            if (location.getMainExit().getCoordinates().equals(c)) {
                //check party
                return true;
            }
        if (game.isDebugMode())
            if (location.getMainEntrance() != null)
                if (location.getMainEntrance().getCoordinates().equals(c)) {
                    return true;
                }
        return false;
    }

    public void togglePaused() {
        setPaused(!isPaused(), true);
    }

    public Float getTime() {
        return game.getDungeonMaster().getExplorationMaster().getTimeMaster().getTime();
    }

    public boolean isExited() {
        return exited;
    }

    public void setExited(boolean exited) {
        this.exited = exited;
        if (exited) {
            main.system.auxiliary.log.LogMaster.log(1, this + " interrupting thread... ");
            WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GAME_LOOP_STARTED);
            try {
                thread.interrupt();
                main.system.auxiliary.log.LogMaster.log(1, this + " interrupted thread!");
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            if (this instanceof ExploreGameLoop) {

            } else {
                actionInput(null);
            }
        }
    }

    public Thread getThread() {
        return thread;
    }

    public void stop() {
        stopped = true;
        playerActionQueue.clear();

    }

    public void resume() {
        stopped = false;
        signal();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void signal() {
        lock.lock();
        waiting.signal();
        lock.unlock();

    }

    public void lock() {
        lock.lock();
        try {
            waiting.await();
        } catch (InterruptedException ie) {
            //ignored
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean isStarted() {
        return started;
    }

}
