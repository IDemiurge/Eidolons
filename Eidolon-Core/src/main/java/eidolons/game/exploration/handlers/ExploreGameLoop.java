package eidolons.game.exploration.handlers;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.handlers.active.Activator;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.api.ScreenApi;
import main.elements.targeting.SelectiveTargeting;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExploreGameLoop extends GameLoop implements RealTimeGameLoop {
    protected RealTimeThread realTimeThread;
    protected ExplorationMaster master;
    protected boolean resetRequired;
    private float blockTimer;

    public ExploreGameLoop(DC_Game game) {
        super(game);
        master = game.getDungeonMaster().getExplorationMaster();
        // macroTimeMaster = MacroTimeMaster.getInstance();
    }


    protected ScreenApi getGdxScreen() {
        return GdxAdapter.getInstance().getDungeonScreen();
    }

    @Override
    public void resume() {
        super.resume();
        startRealTimeThread();
    }

    @Override
    public Thread startInNewThread() {
        if (!CoreEngine.isGraphicsOff()) {
            if (getGdxScreen() == null)
                WaitMaster.waitForInput(getOperationToWaitFor());//, 2000);
            getGdxScreen().setRealTimeGameLoop(this);
        }
        if (!Flags.isJUnit()) {
            //            if (realTimeThread == null)
            startRealTimeThread();

        }
        return super.startInNewThread();
    }

    protected void startRealTimeThread() {
        if (realTimeThread == null || realTimeThread.isDone()) {
            realTimeThread = new RealTimeThread(this);
            realTimeThread.start();
        } else {
            main.system.auxiliary.log.LogMaster.log(1, "realTimeThread already running! ");
        }
    }

    public Thread getRealTimeThread() {
        return realTimeThread;
    }

    @Override
    public void start() {
        //        game.getManager().reset();
        super.start();
    }

    protected WAIT_OPERATIONS getOperationToWaitFor() {
        return WAIT_OPERATIONS.GUI_READY;
    }

    @Override
    protected Boolean checkEndRound(ActionInput input) {
        if (!input.getContext().getSourceObj().isMine()) {
                return false;
        }
        game.getManager().reset();
        //TODO only for player actions?
        if (skippingToNext)
            return true;

        return false;
    }


    @Override
    public void setExited(boolean exited) {
        super.setExited(exited);
        signal();
    }

    @Override
    protected String getThreadName() {
        return "Explore Game Loop";
    }


    @Override
    protected Boolean makeAction() {
        if (exited)
            return true;
        if (isStopped())
            lock();
        // if (playerActionQueue.isEmpty()) {
        //     if (!master.getAiMaster().isAiActs()) {
        //         lock();
        //     }
        // }
        if (resetRequired) {
            game.getManager().reset();
            resetRequired = false;
        }


        //        if (activeUnit.getHandler().getChannelingSpellData() != null) {
        //            ActionInput data = activeUnit.getHandler().getChannelingSpellData();
        //            ChannelingRule.channelingResolves(activeUnit);
        //             activateAction(data);
        //        }
        if (playerActionQueue.isEmpty()) {
            game.getMovementManager().checkContinueMove(); //why here?
            return null;
        }
        ActionInput playerAction = playerActionQueue.removeFirst();

        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, getActiveUnit());
        //recheck?!
        if (checkActionInputValid(playerAction)) {
            if (playerAction.getAction().isTurn()) {
                playerAction(playerAction);
            } else
                playerAction(playerAction);
            if (!CoreEngine.TEST_LAUNCH)
                if (playerAction.isAuto()) {
                    float coef=0.2f;
                    blockTimer =coef* ExplorationActionHandler.calcBlockingTime(playerAction.getAction());
                    lock();
                }
        } else {
            return exited;
        }
        waitForPause();
        return exited;
    }

    //To-Cleanup - some old stuff here
    protected void playerAction(ActionInput playerAction) {
        //TODO - quick fix for firstMove in void maze..
        getGame().getDungeonMaster().getPuzzleMaster().playerActionDone(playerAction.getAction());

        activateAction(playerAction);
        waitForAnimations(playerAction);
        boolean result = playerAction.getAction().getHandler().isResult();
        master.getActionHandler().playerActionActivated(playerAction.getAction(), result);
        master.getTimeMaster().setGuiDirtyFlag(true);
        getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().killVisibilityResetTimer(); //still relevant?

    }

    protected boolean checkActionInputValid(ActionInput playerAction) {
        if (!playerAction.getAction().canBeActivated(playerAction.getContext(), true))
            return false;
        if (playerAction.getAction().getTargeting() instanceof SelectiveTargeting)
            if (playerAction.getContext().getTarget() != null)
                return playerAction.getAction().canBeTargeted(playerAction.getContext().getTarget());
        return true;
    }

    @Override
    public void queueActionInput(ActionInput actionInput) {
        if (playerActionQueue.size() > 0) {
            main.system.auxiliary.log.LogMaster.log(1, actionInput + " won't be done; playerActionQueue=  " + playerActionQueue);
            return;
        }
        blockTimer = ExplorationActionHandler.calcBlockingTime(actionInput.getAction());
        tryAddPlayerActions(actionInput);
    }

    @Override
    public void actionInputManual(ActionInput actionInput) {
        game.getMovementManager().cancelAutomove(activeUnit);
        actionInput(actionInput);
    }

    @Override
    public void actionInput(ActionInput actionInput) {
        actionInput(actionInput, false);
    }

    public void actionInput(ActionInput actionInput, boolean waitForTimer) {
        if (blockTimer > 0) { //override?
            if (waitForTimer)
                WaitMaster.WAIT((int) (blockTimer * 1000));
            else {
                notReady(actionInput.getAction());
                return;
            }
        }
        actionInput_(actionInput);
    }

    private void notReady(ActiveObj action) {
        Activator.cannotActivate_(action, "Not ready! (" +
                NumberUtils.formatFloat(2, blockTimer) +
                "s.)");
    }

    public void actionInput_(ActionInput actionInput) {
        if (isStopped()) {
            main.system.auxiliary.log.LogMaster.log(1, "action input in stopped ");
            return;
        }
        if (isPaused()) {
            EUtils.showInfoText(
                    RandomWizard.random() ?
                            "The game is Paused!" :
                            "Game is paused now...");
            return;
        }
        if (ExplorationMaster.isWaiting()) {
            ExplorationMaster.setWaiting(false);
            return;
        }
        if (actionInput != null)
            lastActionInput = actionInput;
        queueActionInput(actionInput);
        signal();
        GdxAdapter.getInstance().inputPass();


    }

    public void tryAddPlayerActions(ActionInput actionInput) {
        playerActionQueue.add(actionInput);
    }


    @Override
    protected void loopExited() {
        //            if (ExplorationMaster.checkExplorationSupported(game)) {
        //                WaitMaster.receiveInput(WAIT_OPERATIONS.BATTLE_FINISHED, false);
        //        master.switchExplorationMode(false);
        if (ExplorationMaster.isExplorationOn()) //TODO refactor!
            super.loopExited();
        else {
            SpecialLogger.getInstance().appendAnalyticsLog(
                    SPECIAL_LOG.EXCEPTIONS, "Explore game loop failed to exit");
        }

    }

    @Override
    protected boolean roundLoop() {
        while (true) {
            if (activeUnit != Core.getMainHero()) {
                setActiveUnit(Core.getMainHero());
            }

            Boolean result = makeAction();
            if (exited)
                return false;
            if (result != null) {
                //TODO - NF explore
                // if (game.getMissionMaster().getOutcomeManager().checkOutcomeClear()) {
                //     break;
                // }
                // if (checkNextFloor()) {
                //     game.getMissionMaster().getOutcomeManager().next();
                //     game.getVisionMaster().refresh();
                //     break;
                // }
                if (result) {
                    break;
                }
            }
            if (!ExplorationMaster.isExplorationOn()) {
                lock();
            }
        }
        return true;
    }

    private boolean checkNextFloor() {
        return getGame().getDungeonMaster().getTransitHandler().checkNextFloor();
    }

    @Override
    public void end() {
        getGdxScreen().setRealTimeGameLoop(null);
    }

    @Override
    public void act(float delta) {
        if (isPaused())
            return;
        if (blockTimer > 0) {
            blockTimer -= delta;
            if (blockTimer <= 0) {
                signal();
            }
        }

        master.getTimeMaster().act(delta);
        //        macroTimeMaster.setSpeed(master.getTimeMaster().getTime());
        // macroTimeMaster.act(delta); //
    }

    public Float getTime() {
        return master.getTimeMaster().getTime();
    }

    public void clearPlayerActions() {
        playerActionQueue.clear();
    }
}
