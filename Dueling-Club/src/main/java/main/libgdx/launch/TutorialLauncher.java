package main.libgdx.launch;

import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.tutorial.TutorialMetaMaster;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TutorialLauncher {
    public static void main(String[] args) {
        DemoLauncher.main(null);
        DC_Engine.mainMenuInit();
        TutorialMetaMaster master = new TutorialMetaMaster("");
        master.init();

        ScreenData data = new ScreenData(ScreenType.BATTLE, "name"
//         , new SceneFactory("Tutorial Intro")
        );
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        DC_Engine.gameStartInit();
        master.preStart();
//       master.getGame().init( );
        master.getGame().dungeonInit();
        master.getGame().battleInit();
        master.getGame().start(true);

        //DungeonScreen.getInstance().hideLoader();
    }
}
