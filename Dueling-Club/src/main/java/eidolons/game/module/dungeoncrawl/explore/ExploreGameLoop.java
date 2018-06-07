package eidolons.game.module.dungeoncrawl.explore;

import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.MacroTimeMaster;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GameScreen;
import main.elements.targeting.SelectiveTargeting;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExploreGameLoop extends GameLoop implements RealTimeGameLoop {
    private Thread realTimeThread;
    private MacroTimeMaster macroTimeMaster;
    private ExplorationMaster master;
    private boolean resetRequired;

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

    private void startRealTimeThread() {

        realTimeThread = new RealTimeThread(this);
        realTimeThread.start();
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
        if (actionQueue.isEmpty()) {
            if (!master.getAiMaster().isAiActs()) {
                lock();

            }
        }
        if (resetRequired) {
            game.getManager().reset();
            resetRequired = false;
        }
        if (master.getAiMaster().isAiActs()) {

            while (!master.getAiMaster().getAiActionQueue().isEmpty()) {
                //sort? change display?
                // active unit?
                try {
                    ActionInput input = master.getAiMaster().getAiActionQueue().removeLast();
                    activateAction(input);
                    master.getTimeMaster().aiActionActivated(input.getAction().getOwnerObj().getAI(), input.getAction());
                    master.getPartyMaster().reset();

                    if (input.getAction().getOwnerObj().getAI().isLeader()) {
//                        master.getEnemyPartyMaster().setGroupAI();
                        master.getEnemyPartyMaster().leaderActionDone(input);
                    }

                    if (master.getResetter().isAggroCheckNeeded(input)) {
//                        game.getVisionMaster().getVisionRule().
//                         fullReset(input.getAction().getOwnerObj());
                        game.getManager().reset();

                        getGame().getDungeonMaster().getExplorationMaster()
                         .getAggroMaster().checkStatusUpdate();
                        if (!ExplorationMaster.isExplorationOn()) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            master.getAiMaster().setAiActs(false);
        }

//        if (activeUnit.getHandler().getChannelingSpellData() != null) {
//            ActionInput data = activeUnit.getHandler().getChannelingSpellData();
//            ChannelingRule.channelingResolves(activeUnit);
//             activateAction(data);
//        }
        if (actionQueue.isEmpty()) {
            game.getMovementManager().promptContinuePath(activeUnit);
            return null;
        }

        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, getActiveUnit());
        master.getAiMaster().reset();
        master.getResetter().setResetNeeded(true);
        //recheck?!
        ActionInput playerAction = actionQueue.removeLast();
        if (checkActionInputValid(playerAction)) {
            game.getMovementManager().cancelAutomove(activeUnit);
            activateAction(playerAction);
            master.getActionHandler().playerActionActivated(playerAction.getAction());
            master.getTimeMaster().setGuiDirtyFlag(true);
            master.getPartyMaster().leaderActionDone(playerAction);


            waitForAnimations();

        }
        waitForPause();
        return exited;
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

    @Override
    protected void waitForAnimations() {
        if (AnimMaster.getInstance().isDrawingPlayer()) {
            int maxTime = 1000;
            Loop loop = new Loop(20);
            int waitTime = 0;
            while (
             waitTime < maxTime &&
              loop.continues() &&
              (
               DungeonScreen.getInstance().getGridPanel()
                .getViewMap().get(activeUnit).getActions().size > 0 ||
                AnimMaster.getInstance().isDrawingPlayer()
              )) {
                WaitMaster.WAIT(100);
                waitTime += 100;
                main.system.auxiliary.log.LogMaster.log(1,"Explore loops waited for anim to draw: " +waitTime);
            }
        }
    }

    @Override
    public void queueActionInput(ActionInput actionInput) {
        if (actionQueue.size() > 0)
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
        if (isStopped()){
            main.system.auxiliary.log.LogMaster.log(1,"action input in stopped " );
            return ;
        }
        if (isPaused())
        {
            EUtils.showInfoText(
             RandomWizard.random()?
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

    private void tryAddPlayerActions(ActionInput actionInput) {
        actionQueue.add(actionInput);
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
                if (checkNextLevel()) {
                    game.getBattleMaster().getOutcomeManager().next();
                    game.getVisionMaster().refresh();
                    break;
                }
                if (result) {
                    break;
                }
            }
            if (!ExplorationMaster.isExplorationOn())
            {
                lock();
            }
        }
        return true;
    }

    @Override
    public void end() {
        getGdxScreen().setRealTimeGameLoop(null);
    }

    @Override
    public void act(float delta) {
        if (isPaused())
            return;
        master.getTimeMaster().timePassed(delta);
//        macroTimeMaster.setSpeed(master.getTimeMaster().getTime());
        macroTimeMaster.act(delta * 5);
    }

    public Float getTime() {
        return master.getTimeMaster().getTime();
    }

}
