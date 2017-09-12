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

import java.util.Stack;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExploreGameLoop extends GameLoop implements RealTimeGameLoop {
    private static final int REAL_TIME_LOGIC_PERIOD = 150;
    private static Thread realTimeThread;
    private ExplorationMaster master;
    private Stack<ActionInput> playerActionQueue = new Stack<>();

    public ExploreGameLoop(DC_Game game) {
        super(game);
        master = game.getDungeonMaster().getExplorationMaster();

    }

    protected static void startRealTimeLogic() {
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
        game.getManager().reset();
        return false;
    }

    @Override
    protected Boolean makeAction() {
        //time to wait?
        //need another thread...


        //check ai pending actions!
        if (master.getAiMaster().isAiActs()) {

            while (!master.getAiMaster().getAiActionQueue().isEmpty()) {
                //sort? change display?
                // active unit?
                try {
                    ActionInput input = master.getAiMaster().getAiActionQueue().pop();
                    activateAction(input);
                    master.getTimeMaster().aiActionActivated(input.getAction().getOwnerObj().getAI(), input.getAction());
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
        if (playerActionQueue.isEmpty()) {
            game.getMovementManager().promptContinuePath(activeUnit);
            return false;
        }


        master.getCrawler().reset();
        master.getAiMaster().reset();
        master.getResetter().setResetNeeded(true);
        //recheck?!
        ActionInput playerAction = playerActionQueue.pop();
        if (checkActionInputValid(playerAction)) {
            game.getMovementManager().cancelAutomove(activeUnit);
            activateAction(playerAction);
            master.getActionHandler().playerActionActivated(playerAction.getAction());
            VisionManager.refresh();
            waitForAnimations();
            GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
        }
        waitForPause();
        return false; //check unit!
    }

    private boolean checkActionInputValid(ActionInput playerAction) {
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
            Loop loop = new Loop(20);
            while (loop.continues() && AnimMaster.getInstance().isDrawingPlayer()) {
                WaitMaster.WAIT(100);
            }
        }
    }

    @Override
    public void actionInput(ActionInput actionInput) {
        if (playerActionQueue.size() > 1)
            return;
        if (playerActionQueue.contains(actionInput))
            return;
        if (playerActionQueue.size() > 0) {
            new Thread(() -> {
                Loop loop = new Loop(20);
                while (loop.continues()) {
                    WaitMaster.WAIT(100);
                    if (playerActionQueue.isEmpty()) {
                        //check validity
                        tryPushToPlayerActions(actionInput);
                        break;
                    }
                }

            }, "Player ActionInput Thread").start();
        } else
            tryPushToPlayerActions(actionInput);
    }

    private void tryPushToPlayerActions(ActionInput actionInput) {

        playerActionQueue.push(actionInput);
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
