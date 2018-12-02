package eidolons.macro;

import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.map.MapScreen;
import eidolons.libgdx.screens.map.town.navigation.data.NavigationMaster;
import eidolons.macro.entity.town.Town;
import eidolons.macro.generation.ScenarioGenerator;
import eidolons.macro.global.persist.Saver;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.macro.map.Place;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MacroGameLoop extends GameLoop implements RealTimeGameLoop {

    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    private MacroTimeMaster timeMaster;
    private MacroGame macroGame;
    private Place lastEnteredPlace;

    public MacroGameLoop(MacroGame game) {
        super();
        this.macroGame = game;
        timeMaster = MacroTimeMaster.getInstance();
    }

    @Override
    public void setExited(boolean exited) {
        if (exited)
            if (OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.AUTOSAVE_ON)) {
                try {
                    Saver.autosave();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        super.setExited(exited);
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
        if (playerActionQueue.isEmpty()) {
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
        //        Place entered = checkBattleStarts();
        //        if (isAutoEnterCombat())
        //            if (entered != null) {
        //                enter(entered);
        //                return;
        //            }
        if (isAutoEnterCombat())
            for (Place sub : macroGame.getPlaces()) {
                if (tryEnter(sub))
                    return;
            }
        timeMaster.act(delta);
    }

    private boolean isAutoEnterCombat() {
        return CoreEngine.isFastMode();
    }

    public boolean tryEnter(Place sub) {
        Coordinates c = macroGame.getPlayerParty().getCoordinates();
        if (sub != lastEnteredPlace)
            if (AdventureInitializer.isTestMode()
             || (sub.getCoordinates().dst(c) < 100 && macroGame.getPlayerParty().isHasMoved())) {

                enter(sub);
                lastEnteredPlace = sub;
                return true;
            }
        return false;
    }

    public void enter(Place entered) {
        if (NavigationMaster.isTestOn()){
            GuiEventManager.trigger(GuiEventType.SHOW_NAVIGATION_PANEL, entered);
            return;
        }

        if (entered instanceof Town) {
            //            entered.getGame().get
            DC_Game.game.getMetaMaster().getTownMaster().enterTown((Town) entered, false);
            return;
        }
        Eidolons.onThisOrNonGdxThread(() -> {
            startBattle(entered);
            setPaused(true);
            stop();
        });
    }

    private void startBattle(Place entered) {
        ObjType type = ScenarioGenerator.generateScenarioType(entered);
        main.system.auxiliary.log.LogMaster.log(1, "gen Scenario for dungeon:" + type.getName());


        String name = type.getName();
        ScreenData data = new ScreenData(SCREEN_TYPE.BATTLE, name);
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        //when loaded, will init DC_Game properly
    }

    private Place checkBattleStarts() {
        Coordinates c = macroGame.getPlayerParty().getCoordinates();
        for (Place sub : macroGame.getPlaces()) {
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
