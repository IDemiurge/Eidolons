package main.test.frontend;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class BattleSceneLauncher extends DemoLauncher {

    public static void main(String[] args) {
        new LwjglApplication(new BattleSceneLauncher(), getConf());
    }

    @Override
    protected void initEngine() {
/*        ScreenData data = new ScreenData(ScreenType.BATTLE, "name");
        screenSwitcher(new EventCallbackParam(data));*/
    }
}
