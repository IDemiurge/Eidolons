package main.test.frontend;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.EventCallbackParam;

public class BattleSceneLauncher extends DemoLauncher {

    public static void main(String[] args) {
        new LwjglApplication(new BattleSceneLauncher(), getConf());
    }

    @Override
    protected void initEngine() {
       ScreenData data = new ScreenData(ScreenType.BATTLE, "Loading...");
        screenSwitcher(new EventCallbackParam(data));


    }
}
