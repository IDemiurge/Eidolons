package main.libgdx.launch;

import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.Eidolons;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/22/2017.
 */
public class ScenarioLauncher {
    public static final String DEFAULT = "Pride and Treachery";
    public static final String CRAWL = "Into Darkness";
    public static int missionIndex = 0;
    public static boolean running;
    private static ScenarioMetaMaster master;


    public static void main(String[] args) {
        running = true;
        if (args.length > 2)
            DemoLauncher.initQuickLaunch();


        if (args.length > 1)
            missionIndex = StringMaster.getInteger(args[1]);
        if (args.length > 0) {
            if (args[0] != null)
                launch(args[0]);
            else
                launch(DEFAULT);
        } else {
            launch(DEFAULT);
        }
    }


    public static void launch(String typeName) {
        DC_Engine.jarInit();
        DemoLauncher.main(null);
        DC_Engine.mainMenuInit();
//        Eidolons.mainGame.getMetaMaster().preStart();
        master = new ScenarioMetaMaster(typeName);
        Eidolons.initScenario(master);

        ScreenData data = new ScreenData(ScreenType.BATTLE,
         master.getMissionName()
        );
        //new SceneFactory("Test")
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        DC_Engine.gameStartInit();
        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
        Eidolons.mainGame.getMetaMaster().getGame().start(true);

        //DungeonScreen.getInstance().hideLoader();
    }
}
