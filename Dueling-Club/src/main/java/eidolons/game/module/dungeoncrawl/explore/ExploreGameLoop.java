package eidolons.game.module.dungeoncrawl.explore;

import com.badlogic.gdx.Gdx;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GameScreen;
import main.elements.targeting.SelectiveTargeting;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.auxiliary.Loop;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExploreGameLoop extends GameLoop implements RealTimeGameLoop {
    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    private static Thread realTimeThread;
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();
    private ExplorationMaster master;

    public ExploreGameLoop(DC_Game game) {
        super(game);
        master = game.getDungeonMaster().getExplorationMaster();

    }

    public ExploreGameLoop() {
        super();
    }

    protected static void startRealTimeLogic() {
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getPartyMaster().reset();
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();
        if (!CoreEngine.isGraphicsOff())
            Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().getAllies().forEach(unit -> {
                Gdx.app.postRunnable(() ->
                 {
                     try {
                         AnimMaster.getInstance().getConstructor().preconstructAll(unit);
                     } catch (Exception e) {
                         main.system.ExceptionMaster.printStackTrace(e);
                     }
                 }
                );
            });

        while (true) {

            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);
            if (Eidolons.getGame() == null)
                return;
            if (Eidolons.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            if (ExplorationMaster.isRealTimePaused()) continue;
            try {
                Eidolons.getGame().getDungeonMaster().getExplorationMaster().
                 getTimeMaster().checkTimedEvents();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    protected GameScreen getGdxScreen() {
        return DungeonScreen.getInstance();
    }

    @Override
    public Thread startInNewThread() {
        if (!CoreEngine.isGraphicsOff()) {
            if (getGdxScreen() == null)
                WaitMaster.waitForInput(getOperationToWaitFor());//, 2000);
            getGdxScreen().setRealTimeGameLoop(this);
        }
        if (realTimeThread == null) {
            realTimeThread = new Thread(() -> {
                startRealTimeLogic();
            }, "RT Thread");
            realTimeThread.start();
        }

        return super.startInNewThread();
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

    private boolean checkNextLevel() {
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
//test
                    return true;
                }
        return false;
    }

    @Override
    public void setExited(boolean exited) {
        super.setExited(exited);
        signal();
    }

    @Override
    protected Boolean makeAction() {
        if (exited)
            return true;
        if (actionQueue.isEmpty()) {
            if (!master.getAiMaster().isAiActs()) {
                lock.lock();
                try {
                    waiting.await();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
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
                        game.getVisionMaster().getVisionRule().
                         fullReset(input.getAction().getOwnerObj());

                        getGame().getDungeonMaster().getExplorationMaster()
                         .getCrawler().checkStatusUpdate();
                        if (!ExplorationMaster.isExplorationOn()) {
                            return true;
                        }
                        game.getManager().reset();
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
        if (isPaused())
            return;
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

    public void signal() {
        lock.lock();
        waiting.signal();
        lock.unlock();

    }

    @Override
    protected void loopExit() {
//            if (ExplorationMaster.checkExplorationSupported(game)) {
//                WaitMaster.receiveInput(WAIT_OPERATIONS.BATTLE_FINISHED, false);
//        master.switchExplorationMode(false);
        if (ExplorationMaster.isExplorationOn()) //TODO refactor!
            super.loopExit();

    }

    @Override
    protected boolean roundLoop() {
        while (true) {
            if (activeUnit != game.getPlayer(true).getHeroObj()) {
                activeUnit = (Unit) game.getPlayer(true).getHeroObj();
                GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
            }

            Boolean result = makeAction();
            if (exited)
                return false;
            if (result != null) {
                if (game.getBattleMaster().getOutcomeManager().checkOutcomeClear()) {
                    return false;
                }
                if (checkNextLevel()) {
                    game.getBattleMaster().getOutcomeManager().next();
                    game.getVisionMaster().refresh();
                    return false;
                }
                if (result) {
                    break;
                }
            }
            if (!ExplorationMaster.isExplorationOn())
                return true;
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
        master.getTimeMaster().act(delta);
    }

    public Float getTime() {
        return master.getTimeMaster().getTime();
    }

}
