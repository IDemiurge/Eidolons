package main.test.frontend;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import main.game.core.Eidolons;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.EventCallbackParam;

public class BattleSceneLauncher extends DemoLauncher {

    public static void main(String[] args) {
        Eidolons.setApplication(new LwjglApplication(new BattleSceneLauncher(), getConf()));
        if (fullscreen
         ) {
            Eidolons.setFullscreen(true);
        }
    }

    @Override
    protected void initEngine() {
       ScreenData data = new ScreenData(ScreenType.BATTLE, "Loading...");
        screenSwitcher(new EventCallbackParam(data));


    }

}
