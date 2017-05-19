package main.libgdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EngineEmulator implements Engine {
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void init(Runnable onDone) {
        executorService.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            } finally {
                onDone.run();
            }
        });
    }

    @Override
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

    @Override
    public void load(ScreenData meta, Runnable onDone) {
        executorService.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            } finally {
                onDone.run();
            }
        });
    }

    @Override
    public void exit() {

    }

    @Override
    public void onFail(Consumer<OnEngineFail> onFail) {

    }
}
