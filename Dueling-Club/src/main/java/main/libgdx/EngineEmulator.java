package main.libgdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EngineEmulator {
    ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    public ScreenData getMeta(String name) {
        ScreenData meta = null;
        switch (name) {
            case "demo":
                meta = new ScreenData(ScreenType.HEADQUARTERS, name);
                break;
            default:
                break;
        }
        return meta;
    }

    public void load(ScreenData meta, Runnable onDone) {
        executorService.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            } finally {
                onDone.run();
            }
        });
    }

    public void exit() {

    }

    public void onFail(Consumer<OnEngineFail> onFail) {

    }
}
