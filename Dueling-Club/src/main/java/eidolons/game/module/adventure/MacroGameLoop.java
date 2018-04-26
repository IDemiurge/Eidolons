package eidolons.game.module.adventure;

import eidolons.game.core.ActionInput;
import eidolons.game.core.GameLoop;
import eidolons.game.module.adventure.global.ScenarioGenerator;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.libgdx.screens.map.MapScreen;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
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
        timeMaster =MacroTimeMaster.getInstance();
    }

    @Override
    protected Boolean makeAction() {
        if (exited)
            return true;
        lock.lock();
        try {
            waiting.await();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            lock.unlock();
        }
        if (actionQueue.isEmpty()) {
        }

//        ActionInput playerAction = actionQueue.removeLast();
//        if (checkActionInputValid(playerAction)) {
//            game.getMovementManager().cancelAutomove(activeUnit);
//
//        }

        return null;
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
        signal();
//        GuiEventManager.trigger(MapEvent.MAP_GUI_UPDATE);
    }

    @Override
    protected String getThreadName() {
        return "Macro " + super.getThreadName();
    }

    @Override
    public void start() {

        startRealTimeLogic();
        while (true) {
            makeAction();
        }
    }

    public void startRealTimeLogic() {
        new Thread(() -> realTimeLogic(), "Map RT thread").start();
    }

    protected void realTimeLogic() {

        while (true) {

            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);
            if (isPaused())
                continue;
            timeMaster.timedCheck();

//            if (Eidolons.getMacroGame().isPaused()) continue;

        }
    }

    public void combatFinished() {
        timeMaster.hoursPassed(12);
        setPaused(false);
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
        Place entered = checkBattleStarts();
        if (isAutoEnterCombat())
            if (entered != null) {
                combatStarts(entered);
                return;
            }
        timeMaster.act(delta);
    }

    private boolean isAutoEnterCombat() {
        return false;
    }

    public void tryEnter(Place sub) {
        Coordinates c = game.getPlayerParty().getCoordinates();
        if (sub.getCoordinates().dst(c) < 150) {
            combatStarts(sub);
        }
    }

    public void combatStarts(Place entered) {
        setPaused(true);// OR do it on logic thread
        new Thread(() -> {
            startBattle(entered);
        }, " battle start thread").start();
    }

    private void startBattle(Place entered) {
        //stop all the map related stuff
        /*
        we need
        1) dungeon level
        2) party data
        3)
         */

        // TODO blackout

        String name = ScenarioGenerator.generateScenarioType(entered).getName();

//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
//         new ScreenData(ScreenType.PRE_BATTLE, "Wait..."));
//        Eidolons.initScenario(new ScenarioMetaMaster(name));
        ScreenData data = new ScreenData(ScreenType.BATTLE,
         name//  entered.getName()
        );
        //new SceneFactory("Test")
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
//        DC_Engine.gameStartInit();
//        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
//        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
//        Eidolons.mainGame.getMetaMaster().getGame().start(true);
    }

    private Place checkBattleStarts() {
        Coordinates c = game.getPlayerParty().getCoordinates();
        for (Place sub : game.getPlaces()) {
            if (sub.getCoordinates().dst(c) < 150) {
                return sub;
            }
        }
        return null;
    }


    public MacroTimeMaster getTimeMaster() {
        return timeMaster;
    }
}
