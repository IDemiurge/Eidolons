package main.test.frontend;

import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import main.game.core.Eidolons;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TutorialLauncher {
    public static void main(String[] args) {
        BattleSceneLauncher.main(null);
        DC_Engine.mainMenuInit();
//        Eidolons.initScenario(typeName);

        ScreenData data = new ScreenData(ScreenType.BATTLE, "name", new SceneFactory("Test"));
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
