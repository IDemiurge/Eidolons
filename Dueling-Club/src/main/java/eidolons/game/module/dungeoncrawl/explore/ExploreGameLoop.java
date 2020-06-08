package eidolons.game.module.dungeoncrawl.explore;

import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
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
            game.getMovementManager().checkContinueMove();
            return null;
        }

        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, getActiveUnit());
        master.getAiMaster().reset();
        master.getResetter().setResetNeeded(true);
        //recheck?!
        ActionInput playerAction = playerActionQueue.removeLast();

        if (checkActionInputValid(playerAction)) {
            playerAction(playerAction);

        }
        waitForPause();
        return exited;
    }

    protected void playerAction(ActionInput playerAction) {

        //TODO igg demo fix
        activateAction(playerAction);
        waitForAnimations(playerAction);
        boolean result = playerAction.getAction().getHandler().isResult();
        master.getActionHandler().playerActionActivated(playerAction.getAction(), result);
        master.getTimeMaster().setGuiDirtyFlag(true);
        master.getPartyMaster().leaderActionDone(playerAction); //for members to follow or so..
        getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().killVisibilityResetTimer(); //still relevant?
        if (result) {
            getGame().getDungeonMaster().getPuzzleMaster().playerActionDone(playerAction.getAction());
        }

    }

    protected boolean handleAi() {
        if (master.getAiMaster().isAiActs()) {
            DequeImpl<ActionInput> queue = getAiActionQueue();
            while (!queue.isEmpty()) {
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
                return playerAction.getAction().canBeTargeted(playerAction.getContext().getTarget());
        return true;
    }


    protected int getAnimWaitPeriod() {
        return 100;
    }

    protected int getMaxAnimWaitTime(ActionInput action) {
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
    }


    protected boolean isMustWaitForAnim(ActionInput action) {
        return ScreenMaster.getDungeonGrid()
                .getViewMap().get(activeUnit).getActions().size > 0 || AnimMaster.getInstance().isDrawingPlayer();
    }

    @Override
    public void queueActionInput(ActionInput actionInput) {
        if (playerActionQueue.size() > 0)
        {
            main.system.auxiliary.log.LogMaster.log(1,actionInput+" won't be done; playerActionQueue=  " +playerActionQueue);
            return;
        }
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
    public void actionInputManual(ActionInput actionInput) {
        game.getMovementManager().cancelAutomove(activeUnit);
        actionInput_(actionInput);
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
        DungeonScreen.getInstance().getController().inputPass();

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
                if (game.getMissionMaster().getOutcomeManager().checkOutcomeClear()) {
                    break;
                }
                if (checkNextFloor()) {
                    game.getMissionMaster().getOutcomeManager().next();
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
        master.getTimeMaster().act(delta);
        //        macroTimeMaster.setSpeed(master.getTimeMaster().getTime());
        macroTimeMaster.act(delta); //
    }

    public Float getTime() {
        return master.getTimeMaster().getTime();
    }

}
