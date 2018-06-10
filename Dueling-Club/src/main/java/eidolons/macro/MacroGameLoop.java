package eidolons.macro;

import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.macro.generation.ScenarioGenerator;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.macro.map.Place;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.libgdx.screens.map.MapScreen;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MacroGameLoop extends GameLoop implements RealTimeGameLoop {

    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    MacroTimeMaster timeMaster;
    MacroGame game;

    public MacroGameLoop(MacroGame game) {
        super();
        this.game = game;
        timeMaster =MacroTimeMaster.getInstance();
    }
    public void togglePaused() {
        setPaused(!isPaused(), false);
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
        signal();
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
        setPaused(true);
        stop();
        Eidolons.onThisOrNonGdxThread(() -> {
            startBattle(entered);
        });
    }

    private void startBattle(Place entered) {
        ObjType type = ScenarioGenerator.generateScenarioType(entered);
        main.system.auxiliary.log.LogMaster.log(1,"gen Scenario for dungeon:" +type.getName());


        String name =type.getName();
        ScreenData data = new ScreenData(ScreenType.BATTLE,name );
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        //when loaded, will init DC_Game properly
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
