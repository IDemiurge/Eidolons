package main.game.module.dungeoncrawl.explore;

import main.elements.targeting.SelectiveTargeting;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.core.ActionInput;
import main.game.core.Eidolons;
import main.game.core.GameLoop;
import main.game.core.game.DC_Game;
import main.libgdx.anims.AnimMaster;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.auxiliary.Loop;
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
    private static final int REAL_TIME_LOGIC_PERIOD = 150;
    private static Thread realTimeThread;
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();
    private ExplorationMaster master;

    public ExploreGameLoop(DC_Game game) {
        super(game);
        master = game.getDungeonMaster().getExplorationMaster();

    }

    protected static void startRealTimeLogic() {
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getPartyMaster().reset();
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();

        while (true) {
            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);
            if (Eidolons.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            Eidolons.getGame().getDungeonMaster().getExplorationMaster().
             getTimeMaster().checkTimedEvents();
        }
    }

    @Override
    public Thread startInNewThread() {
        if (DungeonScreen.getInstance()==null )
            WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY, 2000);
        DungeonScreen.getInstance().setRealTimeGameLoop(this);
        if (realTimeThread == null) {
            realTimeThread = new Thread(() -> {
                startRealTimeLogic();
            }, "RT Thread");
            realTimeThread.start();
        }

        return super.startInNewThread();
    }

    @Override
    protected Boolean checkEndRound(ActionInput input) {
        if (!input.getContext().getSourceObj().isMine()) {
            if (!master.getResetter().isResetNeeded())
                return false;
        }
        game.getManager().reset();
        //TODO only for player actions?
        VisionManager.refresh();
        master.getResetter().setResetNeeded(false);
        return false;
    }

    @Override
    protected Boolean makeAction() {
        //time to wait?
        //need another thread...

        if (actionQueue.isEmpty()) {
            if (!master.getAiMaster().isAiActs()) {
                lock.lock();
//                processing = false;
                try {
                    waiting.await();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
//processing = true;
        //check ai pending actions!
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
                } catch (Exception e) {
                    e.printStackTrace();
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
            return false;
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
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
        }
        waitForPause();
        return false; //check unit!
    }

    protected boolean checkActionInputValid(ActionInput playerAction) {
        if (!playerAction.getAction().canBeActivated(playerAction.getContext(), true))
            return false;
        if (playerAction.getAction().getTargeting() instanceof SelectiveTargeting)
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
                .getUnitMap().get(activeUnit).getActions().size > 0 ||
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
        master.switchExplorationMode(false);
    }

    @Override
    protected boolean roundLoop() {
        activeUnit = (Unit) game.getPlayer(true).getHeroObj();
        game.getManager().setSelectedActiveObj(activeUnit);
        GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);

        while (true) {
            Boolean result = makeAction();
            if (result) {
                break;
            }
            if (!ExplorationMaster.isExplorationOn())
                return false;
        }
        return true;
    }

    @Override
    public void end() {
        DungeonScreen.getInstance().setRealTimeGameLoop(null);
    }

    @Override
    public void act(float delta) {
        if (isPaused())
            return;
        master.getTimeMaster().act(delta);
    }


}
