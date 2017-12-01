package main.test.frontend;

import main.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import main.libgdx.DialogScenario;
import main.libgdx.launch.DemoLauncher;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.EventCallbackParam;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroTestLauncher extends DemoLauncher {
    public static boolean running;
    static String testData = "Test";
    private Supplier<List<DialogScenario>> factory;

    private IntroTestLauncher(Supplier<List<DialogScenario>> factory) {
        this.factory = factory;
    }

    public static void main(String[] args) {
        running = true;
        new IntroTestLauncher(new SceneFactory(testData)).start();
    }

    @Override
    protected void engineInit()   {
        ScreenData data = new ScreenData(ScreenType.BATTLE, "Loading...", factory);
        screenSwitcher(new EventCallbackParam(data));
    }
}
