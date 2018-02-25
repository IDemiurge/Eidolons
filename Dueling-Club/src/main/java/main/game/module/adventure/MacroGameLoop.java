package main.game.module.adventure;

import main.game.core.ActionInput;
import main.game.core.GameLoop;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.screens.map.MapScreen;
import main.system.threading.WaitMaster;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MacroGameLoop extends GameLoop implements RealTimeGameLoop {

    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    private static Thread realTimeThread;
    MacroTimeMaster timeMaster;
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();
    MacroGame game;

    public MacroGameLoop(MacroGame game) {
        super();
        this.game = game;
        timeMaster = new MacroTimeMaster();
    }
    @Override
    protected Boolean makeAction() {
        if (exited)
            return true;
        if (actionQueue.isEmpty()) {
        }

//        ActionInput playerAction = actionQueue.removeLast();
//        if (checkActionInputValid(playerAction)) {
//            game.getMovementManager().cancelAutomove(activeUnit);
//
//        }

            return null ;
    }
    public void signal() {
        lock.lock();
        waiting.signal();
        lock.unlock();

    }
    @Override
    public void actionInput(ActionInput actionInput) {
        if (isPaused())
            return;
        //check blocked
//        queueActionInput(actionInput);
//        signal();
//        GuiEventManager.trigger(MapEvent.MAP_GUI_UPDATE);
    }

    @Override
    public void start() {

        startRealTimeLogic();
        while(true){
            makeAction();
        }
    }

    public    void startRealTimeLogic() {
        new Thread(() -> realTimeLogic(), "Map RT thread").start();
    }

    protected   void realTimeLogic() {

        while (true) {

            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);

            timeMaster.timedCheck();

//            if (Eidolons.getMacroGame().isPaused()) continue;

        }
    }

    public void combatFinished() {
        timeMaster.hoursPassed(12);
        setPaused(false);
    }

    public void enterCombat() {
        //some time will pass in combat
setPaused(true);
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    protected MapScreen getGdxScreen() {
        return MapScreen.getInstance();
    }

    @Override
    public void end() {

    }

    @Override
    public void act(float delta) {
        if (isPaused())
            return;
        timeMaster.act(delta);
    }

    public MacroTimeMaster getTimeMaster() {
        return timeMaster;
    }
}
