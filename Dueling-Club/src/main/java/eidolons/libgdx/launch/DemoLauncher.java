package eidolons.libgdx.launch;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import eidolons.client.cc.logic.items.ItemGenerator;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.EventCallbackParam;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import org.dizitart.no2.Nitrite;

public class DemoLauncher extends GenericLauncher {
    protected static Nitrite db;
    protected static String quickTypes =
     "units;bf obj;terrain;missions;places;scenarios;party;";


    public static void initQuickLaunch() {
        CoreEngine.setSelectivelyReadTypes(quickTypes);
        ItemGenerator.setGenerationOn(false);

    }

    public static void main(String[] args) {
        new DemoLauncher().start();
    }

    @Override
    public void start() {
        super.start();
    }

    public LwjglApplicationConfiguration getConf() {
//        Eidolons. getApplication().getGraphics().setFullscreenMode();
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = getTitle();
//        if (Gdx.graphics.isGL30Available())
        conf.useGL30 = true;
        conf.resizable = false;
        OptionsMaster.init();

        conf.fullscreen = false;
        fullscreen = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);
        conf.foregroundFPS = FRAMERATE;
        conf.backgroundFPS = isStopOnInactive() ? -1 : FRAMERATE;
        conf.vSyncEnabled = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VSYNC);
        initResolution(conf);
        initIcon(conf);

        return conf;
    }

    @Override
    protected void triggerLoaded(ScreenData meta) {

    }

    @Override
    protected void screenInit() {
        ScreenData data = new ScreenData(ScreenType.BATTLE, "Loading...");
        screenSwitcher(new EventCallbackParam(data));


    }

    @Override
    public void dispose() {
        try {
            SpecialLogger.getInstance().writeLogs();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.dispose();
    }

    @Override
    protected void engineInit() {

//        engine = new EngineEmulator();
    }


}
