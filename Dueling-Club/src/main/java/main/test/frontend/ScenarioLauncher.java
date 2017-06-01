package main.test.frontend;

import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 5/22/2017.
 */
public class ScenarioLauncher {
    private static final String DEFAULT = "Pride and Treachery";

    public static void main(String[] args) {
        String t= DEFAULT;
        if (t==null )
        t= args[0];
        lauch(t);
    }
        public static void lauch(String typeName) {
            BattleSceneLauncher.main(new String[]{});
        DC_Engine.mainMenuInit();
        Eidolons.initScenario(typeName);
        DC_Engine.gameStartInit();
        Eidolons.mainGame.getMetaMaster().gameStarted();
//        Eidolons.mainGame.getMetaMaster().getGame().init( );
        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit( );
        Eidolons.mainGame.getMetaMaster().getGame().battleInit( );
        Eidolons.mainGame.getMetaMaster().getGame().start(true);

            //DungeonScreen.getInstance().hideLoader();
    }
}
