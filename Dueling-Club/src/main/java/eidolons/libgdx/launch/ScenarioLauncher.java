package eidolons.libgdx.launch;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.SCREEN_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/22/2017.
 */
public class ScenarioLauncher {
    public static final String DEFAULT = "Pride and Treachery";
    public static final String CRAWL = "Mistfall";//"Dwarven Gold";
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
                launch(CRAWL);
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
        if (!Eidolons.initScenario(master)) {
            return;
        }

        ScreenData data = new ScreenData(SCREEN_TYPE.BATTLE,
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
