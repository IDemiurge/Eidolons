package eidolons.test.frontend;

import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.libgdx.DialogScenario;
import eidolons.libgdx.launch.DemoLauncher;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.SCREEN_TYPE;
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
    protected void engineInit() {
        ScreenData data = new ScreenData(SCREEN_TYPE.BATTLE, "Loading...", factory);
        screenSwitcher(new EventCallbackParam(data));
    }
}
