package eidolons.libgdx;

import eidolons.libgdx.gui.menu.old.MainMenuScreenData;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import main.system.EngineEventManager;
import main.system.EngineEventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;

public class EngineEmulator {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private boolean isFirstRun = true;

    public EngineEmulator() {
        EngineEventManager.bind(EngineEventType.LOAD_GAME, obj -> {
            ScreenData screenData = new ScreenData(((ScreenData) obj.get()), IntroSceneFactory::getDemoIntro);
            GuiEventManager.trigger(SWITCH_SCREEN, screenData);
            executorService.schedule(() ->
              GuiEventManager.trigger(SCREEN_LOADED, screenData)
             , 5000, TimeUnit.MILLISECONDS);
        });

        MainMenuScreenData data = isFirstRun ?
         new MainMenuScreenData("", IntroSceneFactory::getIntroStage)
         : new MainMenuScreenData("");

        scheduleLoad(data);

        executorService.submit(this::loop);
    }

    private void loop() {
        EngineEventManager.processEvents();
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        executorService.submit(this::loop);
    }

    private void scheduleLoad(ScreenData data) {
        executorService.submit(() -> {
            try {
                if (data.getType() == ScreenType.MAIN_MENU) {
                    ((MainMenuScreenData) data).setNewGames(Arrays.asList(new ScreenData(ScreenType.HEADQUARTERS, "demo")));
                }

                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            } finally {
                GuiEventManager.trigger(SCREEN_LOADED, data);
            }
        });
    }

    public void exit() {

    }

    public void onFail(Consumer<OnEngineFail> onFail) {

    }
}
