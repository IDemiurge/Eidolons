package eidolons.game.module.dungeoncrawl.explore;

import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GameScreen;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.elements.targeting.SelectiveTargeting;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExploreGameLoop extends GameLoop implements RealTimeGameLoop {
    protected RealTimeThread realTimeThread;
    protected MacroTimeMaster macroTimeMaster;
    protected ExplorationMaster master;
    protected boolean resetRequired;

    public ExploreGameLoop(DC_Game game) {
        super(game);
        master = game.getDungeonMaster().getExplorationMaster();
        macroTimeMaster = MacroTimeMaster.getInstance();
        GuiEventManager.bind(GuiEventType.GAME_RESET,
         d -> {
             resetRequired = true;
             signal();
         });
    }


    protected GameScreen getGdxScreen() {
        return DungeonScreen.getInstance();
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
        if (!CoreEngine.isJUnit()) {
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
            if (!master.getResetter().isResetNeeded())
                return false;
        }
        game.getManager().reset();
        //TODO only for player actions?
        if (skippingToNext)
            return true;

        master.getResetter().setResetNeeded(false);
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
        if (playerActionQueue.isEmpty()) {
            if (!master.getAiMaster().isAiActs()) {
                lock();

            }
        }
        if (resetRequired) {
            game.getManager().reset();
            resetRequired = false;
        }
        if (!handleAi())
            return true;


        //        if (activeUnit.getHandler().getChannelingSpellData() != null) {
        //            ActionInput data = activeUnit.getHandler().getChannelingSpellData();
        //            ChannelingRule.channelingResolves(activeUnit);
        //             activateAction(data);
        //        }
        if (playerActionQueue.isEmpty()) {
            game.getMovementManager().promptContinuePath(activeUnit);
            return null;
        }

        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, getActiveUnit());
        master.getAiMaster().reset();
        master.getResetter().setResetNeeded(true);
        //recheck?!
        ActionInput playerAction = playerActionQueue.removeLast();

        if (checkActionInputValid(playerAction)) {
            game.getMovementManager().cancelAutomove(activeUnit);
            activateAction(playerAction);
            boolean result =playerAction.getAction().getHandler().isResult();
            master.getActionHandler().playerActionActivated(playerAction.getAction(), result);
            master.getTimeMaster().setGuiDirtyFlag(true);
            master.getPartyMaster().leaderActionDone(playerAction);

            waitForAnimations(playerAction);
            getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().killVisibilityResetTimer();

        }
        waitForPause();
        return exited;
    }

    protected boolean handleAi() {
        if (master.getAiMaster().isAiActs()) {
            DequeImpl<ActionInput> queue = getAiActionQueue();
            while (!queue.isEmpty() ) {
//            while (queue.size()>3) {
                //sort? change display?
                // active unit?
                try {
                    ActionInput input = queue.removeLast();
                    activateAction(input);
                    master.getTimeMaster().aiActionActivated(input.getAction().getOwnerUnit().getAI(), input.getAction());
                    master.getPartyMaster().reset();

                    if (input.getAction().getOwnerUnit().getAI().isLeader()) {
                        //                        master.getEnemyPartyMaster().setGroupAI();
                        master.getEnemyPartyMaster().leaderActionDone(input);
                    }

                    if (master.getResetter().isAggroCheckNeeded(input)) {
                        //                        game.getVisionMaster().getVisionRule().
                        //                         fullReset(input.getAction().getOwnerUnit());
                        game.getManager().reset();

                        getGame().getDungeonMaster().getExplorationMaster()
                         .getAggroMaster().checkStatusUpdate();
                        if (!ExplorationMaster.isExplorationOn()) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            master.getAiMaster().setAiActs(false);
        }
        return true;
    }

    protected DequeImpl<ActionInput> getAiActionQueue() {
        if (AiBehaviorManager.isNewAiOn())
            return game.getDungeonMaster().getExplorationMaster().getAiMaster().getExploreAiManager().getBehaviorManager().getAiActionQueue();

        return master.getAiMaster().getAiActionQueue();
    }


    protected boolean checkActionInputValid(ActionInput playerAction) {
        if (!playerAction.getAction().canBeActivated(playerAction.getContext(), true))
            return false;
        if (playerAction.getAction().getTargeting() instanceof SelectiveTargeting)
            if (playerAction.getContext().getTarget() != null)
                if (!playerAction.getAction().canBeTargeted(playerAction.getContext().getTarget()))
                    return false;
        return true;
    }


    protected int getAnimWaitPeriod() {
        return 100;
    }

    protected int getMaxAnimWaitTime(ActionInput action) {
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
    }

    protected int getMinAnimWaitTime(ActionInput action) {
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MIN_ANIM_WAIT_TIME);
    }

    protected boolean isMustWaitForAnim(ActionInput action) {
        return DungeonScreen.getInstance().getGridPanel()
         .getViewMap().get(activeUnit).getActions().size > 0 || AnimMaster.getInstance().isDrawingPlayer();
    }

    @Override
    public void queueActionInput(ActionInput actionInput) {
        if (playerActionQueue.size() > 0)
            return;
        //        if (actionQueue.contains(actionInput))
        //            return;
        //        if (actionQueue.size() > 0) {
        //            new Thread(() -> {
        //                Loop loop = new Loop(20);
        //                while (loop.continues()) {
        //                    WaitMaster.WAIT(100);
        //                    if (actionQueue.isEmpty()) {
        //                        //check validity
        //                        tryAddPlayerActions(actionInput);
        //                        break;
        //                    }
        //                }
        //
        //            }, "Player ActionInput Thread").start();
        //        } else
        tryAddPlayerActions(actionInput);
    }

    @Override
    public void actionInput(ActionInput actionInput) {
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
        queueActionInput(actionInput);
        signal();

    }

    protected void tryAddPlayerActions(ActionInput actionInput) {
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
            SpecialLogger.getInstance().appendSpecialLog(
             SPECIAL_LOG.EXCEPTIONS, "Explore game loop failed to exit");
        }

    }

    @Override
    protected boolean roundLoop() {
        while (true) {
            if (activeUnit != Eidolons.getMainHero()) {
                setActiveUnit(Eidolons.getMainHero());
            }

            Boolean result = makeAction();
            if (exited)
                return false;
            if (result != null) {
                if (game.getBattleMaster().getOutcomeManager().checkOutcomeClear()) {
                    break;
                }
                if (checkNextLevel())
                    if (confirmExit()) {
                        game.getBattleMaster().getOutcomeManager().next();
                        game.getVisionMaster().refresh();
                        break;
                    }
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

    private boolean confirmExit() {
        EUtils.onConfirm("Leave this location? " +
          "Don't forget to check your achievements from the menu...", () ->
          WaitMaster.receiveInput(WAIT_OPERATIONS.CONFIRM, true),
         () ->
          WaitMaster.receiveInput(WAIT_OPERATIONS.CONFIRM, false));
        return (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM);
    }

    @Override
    public void end() {
        getGdxScreen().setRealTimeGameLoop(null);
    }

    @Override
    public void act(float delta) {
        if (isPaused())
            return;
        master.getTimeMaster().act(delta);
        //        macroTimeMaster.setSpeed(master.getTimeMaster().getTime());
        macroTimeMaster.act(delta); //
    }

    public Float getTime() {
        return master.getTimeMaster().getTime();
    }

}
