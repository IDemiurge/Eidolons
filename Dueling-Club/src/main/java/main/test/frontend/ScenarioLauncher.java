package main.test.frontend;

import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/22/2017.
 */
public class ScenarioLauncher {
    public static final String DEFAULT = "Pride and Treachery";
    public static String missionIndex="0";

    public static void main(String[] args) {

        if (args.length>1)
        missionIndex = args[1];
        if (args.length>0){
            if (args[0]!=null )
            launch(args[0]);
            else
                launch(DEFAULT);
        }
        else{
            launch(DEFAULT);
        }
    }


    public static void launch(String typeName) {
        DC_Engine.jarInit();
        BattleSceneLauncher.main(null);
        DC_Engine.mainMenuInit();
        Eidolons.initScenario(typeName);

        ScreenData data = new ScreenData(ScreenType.BATTLE, "name");
         //new SceneFactory("Test")
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        DC_Engine.gameStartInit();
        Eidolons.mainGame.getMetaMaster().preStart();
//        Eidolons.mainGame.getMetaMaster().getGame().init( );
        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
        Eidolons.mainGame.getMetaMaster().getGame().battleInit();

        Eidolons.mainGame.getMetaMaster().getGame().start(true);

        //DungeonScreen.getInstance().hideLoader();
    }
}
