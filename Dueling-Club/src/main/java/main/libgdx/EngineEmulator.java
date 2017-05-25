package main.libgdx;

import main.libgdx.screens.MainMenuScreenData;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.EngineEventManager;
import main.system.EngineEventType;
import main.system.GuiEventManager;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static main.system.GuiEventType.SCREEN_LOADED;

public class EngineEmulator {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private boolean isFirstRun = true;

    public EngineEmulator() {
        EngineEventManager.bind(EngineEventType.SWITCH_SCREEN, obj -> {
            executorService.schedule(() ->

                            GuiEventManager.trigger(SCREEN_LOADED, null)
                    , 1000, TimeUnit.MILLISECONDS);
        });

        EngineEventManager.bind(EngineEventType.LOAD_MAIN_SCREEN, obj -> {
            MainMenuScreenData data = isFirstRun ?
                    new MainMenuScreenData("", IntroSceneFactory.getIntroStage())
                    : new MainMenuScreenData("");

            data.setNewGames(Arrays.asList(new ScreenData(ScreenType.HEADQUARTERS, "demo")));

            executorService.schedule(() -> {
                GuiEventManager.trigger(SCREEN_LOADED, data);
            }, 1000, TimeUnit.MILLISECONDS);
        });

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

    public void init(Runnable onDone) {
        executorService.submit(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            } finally {
                onDone.run();
            }
        });
    }

    public void load(ScreenData meta) {
        executorService.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            } finally {
                GuiEventManager.trigger(SCREEN_LOADED, null);
            }
        });
    }

    public void exit() {

    }

    public void onFail(Consumer<OnEngineFail> onFail) {

    }
}
