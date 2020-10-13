package eidolons.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.misc.ChargeRule;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.gui.generic.GearActor;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.text.DC_LogManager;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.auxiliary.secondary.Bools;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;


/**
 * Created by JustMe on 3/23/2017.
 */
public abstract class GameLoop {

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
    protected boolean stopped;
    protected ActionInput lastActionInput;
    private boolean firstActionDone;
    private DC_ActiveObj lastAction;
    private DC_ActiveObj lastActionEvent;
    private boolean logicWaiting;
    private boolean visualLock;

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

        while (true) {//for JUnit

            if (exited)
                break;
            if (game.getUnits().isEmpty()) {
                continue;
            }
            try {
                if (!roundLoop())
                    break;
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }

        }
        loopExited();
    }

    protected void loopExited() {
        LogMaster.log(1, "Game Loop exit " + this);
        WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GAME_LOOP_STARTED);
        LogMaster.log(1, this + " exited!");
        setExited(false);

        if (game.getLoop() == this)
            SpecialLogger.getInstance().appendAnalyticsLog(
                    SPECIAL_LOG.EXCEPTIONS, "game loop exits without new loop running!");
    }

    public Thread startInNewThread() {
        if (thread != null) {
            LogMaster.log(1, "Game Loop ALREADY started " + this);
            try {
                thread.interrupt();
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }
        }
        thread = new Thread(this::start, getThreadName());

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
            if (isVisualLock()){
                setLogicWaiting(true);
                lock();
            }
            setVisualLock(true);
            if (stopped) {
                lock();
            }
            if (exited)
                return false;
            if (ExplorationMaster.isExplorationOn()) {
                LogMaster.log(1, "COMBAT LOOP EXITS FROM EXPLORE!");
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
                VisionHelper.refresh();
                started = true;
            }
            if (RuleKeeper.isRuleOn(RuleEnums.RULE.INTENTS))
                try {
                    getGame().getAiManager().getActionManager().initIntents();
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            Chronos.mark(activeUnit + "'s action");
            result = makeAction();
            Chronos.logTimeElapsedForMark(activeUnit + "'s action");
            if (!aftermath)
                if (game.getMissionMaster().getOutcomeManager().checkOutcomeClear()) {
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
        //TODO refactor - extract
        if (!game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.UNIT_TURN_READY, getActiveUnit().getRef()))) {
            return false;
        }
        Boolean result;
        ActionInput action;
        boolean channeling = false;
        if (!playerActionQueue.isEmpty()) {
            action = (playerActionQueue.removeLast());
        } else if (activeUnit.getHandler().getChannelingSpellData() != null) {
            action = activeUnit.getHandler().getChannelingSpellData();
            channeling = true;
        } else if (activeUnit.isAiControlled()) {
            //SHOWCASE SECURITY
            //            try {
            GuiEventManager.trigger(GuiEventType.WAITING_ON);
            action = (waitForAI());
            AI_Manager.setOff(false);
            waitForAnimations(action);
            GuiEventManager.trigger(GuiEventType.WAITING_OFF);

            //            } catch (Exception e) {
            //                AI_Manager.setOff(true);
            //                if (!aiFailNotified) {
            //                    main.system.auxiliary.log.LogMaster.log(1, ("AI failed!!!!"));
            //                    aiFailNotified = true;
            //                    return false;
            //                }
            //                main.system.ExceptionMaster.printStackTrace(e);
            //            }
        } else {
            action = (waitForPlayerInput());
        }
        if (channeling)
            ChannelingRule.channelingResolves(activeUnit);

        result =
                activateAction(action);

        //        waitForAnimations(action);
        if (exited)
            return true;
        waitForPause();
        return result;
    }


    protected void waitForPause() {
        if (paused) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_RESUMED);
            //      not very smart      paused = false;
        }
    }

    protected void waitForAnimations(ActionInput action) {
        AnimMaster.waitForAnimations(action);
    }


    public Boolean activateAction(ActionInput input) {
        if (input == null) {
            return true;
        }
        if (input.getContext().getSourceObj().isDead())
            return false;
        boolean result;
        try {
            activatingAction = input.getAction();
            setLastAction(activatingAction);
            activatingAction.setTargetObj(input.getContext().getTargetObj());
            activatingAction.setTargetGroup(input.getContext().getGroup());
            result = input.getAction().getHandler().activateOn(input.getContext());
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
            return null;
        } finally {
            activatingAction = null;
        }
        firstActionDone = true;
        if (!result) {
            return false;
        }
        return checkEndRound(input);
    }

    protected Boolean checkEndRound(ActionInput input) {
        Boolean endTurn = false;

        game.getManager().reset();
        if (ChargeRule.checkRetainUnitTurn(input.getAction())) {
            endTurn = null;
        }

        if (Bools.isTrue(endTurn)) {
            return true;
        } else {
            game.getTurnManager().
                    resetInitiative(false);
        }
        return endTurn;
    }

    public int getWaitOnStartTime() {
        return 0;
    }

    protected ActionInput waitForAI() {
        if (!firstActionDone) {
            Chronos.mark("First ai action");
        }
        Action aiAction =
                game.getAiManager().getAction(game.getManager().getActiveObj());
        if (!firstActionDone) {
            Long time = getWaitOnStartTime() - Chronos.getTimeElapsedForMark("First ai action");
            if (time > 0)
                WaitMaster.WAIT(Math.toIntExact(time));
            firstActionDone = true;
        }
        boolean failed = false;
        if (aiAction == null)
            failed = true;
        else if (!aiAction.getActive().isChanneling())
            if ( aiAction.isOrder()) //TODO AI check
                if (!aiAction.canBeTargeted()) {
                    {
                        LogMaster.log(1, "**************** AI CANNOT TARGET THE activatingAction!!! " + activatingAction);
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
        setPaused(paused, logged, false);
    }

    public void setPaused(boolean paused, boolean logged, boolean manual) {
        // if (!ExplorationMaster.isExplorationOn())
        // {
        //     EUtils.showInfoText("Cannot pause in combat");
        //     return;
        // }
        if (!game.isStarted()) {
            return;
        }
        if (logged)
            if (Flags.isIDE())
                game.getLogManager().log(paused ? "Game paused" : "Game resumed");
        if (paused)
            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Game Paused");
        else GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Game Resumed");
        GearActor.setPaused(paused);
        this.paused = paused;
        Fluctuating.setAlphaFluctuationOn(!paused);
        int vol = manual ? 100 : 50;
        if (paused) {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__PAUSE, vol, 0);
        } else {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__RESUME, vol, 0);
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_RESUMED, true);
        }
    }


    public void queueActionInput(ActionInput actionInput) {
        playerActionQueue.add(actionInput);
    }

    public  void actionInput(ActionInput actionInput) {
    }
    public void actionInput(ActionInput actionInput, boolean wait) {
    }
    public void actionInput_(ActionInput actionInput) {
    }
    public void actionInputManual(ActionInput actionInput) {
        if (AI_Manager.isRunning())
            EUtils.showInfoText(RandomWizard.random() ? "The enemy has the initiative!" : "The enemy has initiative...");
        if (isPaused()) {
            EUtils.showInfoText(RandomWizard.random() ? "The game is Paused!" : "Game is paused now...");
            return;
        }
        DungeonScreen.getInstance().getController().inputPass();
        WaitMaster.receiveInputIfWaiting(WAIT_OPERATIONS.ACTION_INPUT, actionInput, false);
        if (actionInput != null)
            lastActionInput = actionInput;
    }

    public Unit getActiveUnit() {
        if (activeUnit == null)
            return Eidolons.getMainHero();
        return activeUnit;
    }

    public void setActiveUnit(Unit activeUnit) {
        if (activeUnit == this.activeUnit)
            return;
        this.activeUnit = activeUnit;
        if (activeUnit != null) {
            getGame().getLogManager().log(DC_LogManager.UNIT_TURN_PREFIX
                    + activeUnit.getNameIfKnown());
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
        }

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
        Location location = game.getDungeonMaster().getFloorWrapper();
        //        game.getDungeonMaster().getDungeonLevel().getExitCoordinates()
        //        IGG_XmlMaster.getEntrancesData()


        if (location.getMainExit() != null)
            if (location.getMainExit().getCoordinates().equals(c)) {
                //check party
                return true;
            }
        if (Flags.isIDE())
            if (!Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))
                if (game.isDebugMode() || (Flags.isLevelTestMode() && !Eidolons.getMainHero().getLastCoordinates().equals(c)))
                    if (location.getMainEntrance() != null)
                        return location.getMainEntrance().getCoordinates().equals(c);
        return false;
    }

    public void togglePaused() {
        setPaused(!isPaused(), true, true);
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
            LogMaster.log(1, this + " interrupting thread... ");
            WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.GAME_LOOP_STARTED);
            if (thread != null)
                try {
                    thread.interrupt();
                    LogMaster.log(1, this + " interrupted thread!");
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }

            if (this instanceof ExploreGameLoop) {

            } else {
                actionInputManual(null);
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
        firstActionDone = false;
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

    public boolean checkThreadIsRunning() {
        if (thread != null) {
            if (thread.isAlive()) {
                return true;
            }
        }
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equalsIgnoreCase(getThreadName())) {
                return true;
            }
        }
        return false;
    }

    public void setLastActionInput(ActionInput lastActionInput) {
        this.lastActionInput = lastActionInput;
    }

    public ActionInput getLastActionInput() {
        return lastActionInput;
    }

    public void activateMainHeroAction(String name) {
        activateAction(new ActionInput(Eidolons.getMainHero().getActionOrSpell(name), new Context(Eidolons.getMainHero().getRef())));
    }

    public DC_ActiveObj getLastAction() {
        return lastAction;
    }

    public void setLastAction(DC_ActiveObj lastAction) {
        this.lastAction = lastAction;
    }

    public void setLastActionEvent(DC_ActiveObj lastActionEvent) {
        this.lastActionEvent = lastActionEvent;
    }

    public DC_ActiveObj getLastActionEvent() {
        return lastActionEvent;
    }

    public boolean isLogicWaiting() {
        return logicWaiting;
    }

    public void setLogicWaiting(boolean logicWaiting) {
        this.logicWaiting = logicWaiting;
    }
    public boolean isVisualLock() {
        return visualLock;
    }

    public void setVisualLock(boolean visualLock) {
        this.visualLock = visualLock;
        if (!visualLock){
            if (isLogicWaiting())
                signal();
        }
    }

}
