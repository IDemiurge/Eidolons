package main.libgdx;

import main.libgdx.screens.MainMenuScreenData;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.EngineEventManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static main.system.GuiEventType.SCREEN_LOADED;

public class EngineEmulator {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private boolean isFirstRun = true;

    public EngineEmulator() {
/*        EngineEventManager.bind(EngineEventType.SWITCH_SCREEN, obj -> {
            executorService.schedule(() ->

                            GuiEventManager.trigger(SCREEN_LOADED, null)
                    , 1000, TimeUnit.MILLISECONDS);
        });*/

        MainMenuScreenData data = isFirstRun ?
                new MainMenuScreenData("", IntroSceneFactory::getIntroStage)
                : new MainMenuScreenData("");

        data.setNewGames(Arrays.asList(new ScreenData(ScreenType.HEADQUARTERS, "demo")));

        scheduleLoad(data);

        executorService.submit(this::loop);
    }

    private void loop() {
        EngineEventManager.processEvents();
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.submit(this::loop);
    }

    private void scheduleLoad(ScreenData meta) {
        executorService.submit(() -> {
            try {
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, meta);
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            } finally {
                GuiEventManager.trigger(SCREEN_LOADED, meta);
            }
        });
    }

    public void exit() {

    }

    public void onFail(Consumer<OnEngineFail> onFail) {

    }
}
