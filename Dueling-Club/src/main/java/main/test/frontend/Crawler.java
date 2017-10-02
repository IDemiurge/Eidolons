package main.test.frontend;

import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.battlecraft.DC_Engine;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.GameLauncher;
import main.game.core.launch.PresetLauncher;
import main.game.core.launch.TestLauncher.CODE;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;

/**
 * Created by JustMe on 9/21/2017.
 */
public class Crawler {


    public static void main(String[] args) {
    /*
    choose dungeon from crawl folder?
    generate mission per dungeon?
         */
//        launchFastDc();

//        FAST_DC.TEST_MODE=true;
        FAST_DC.FAST_MODE=false;
        ExplorationMaster.setTestMode(true);
        DC_Engine.jarInit();
        BattleSceneLauncher.main(null);
        DC_Engine.mainMenuInit();
//        Eidolons.mainGame.getMetaMaster().preStart();
//        master = new ScenarioMetaMaster(typeName);
//        Eidolons.initScenario(master);

        String dungeon = new FileChooser(PathFinder.getDungeonLevelFolder()).launch("", "");
        dungeon=StringMaster.removePreviousPathSegments(dungeon, PathFinder.getDungeonLevelFolder());
        ScreenData data = new ScreenData(ScreenType.BATTLE,
         dungeon
        );
        GameLauncher launcher = new GameLauncher(GAME_SUBCLASS.TEST);
        Ref ref = new Ref();
        Condition conditions = new Conditions();
        launcher.PLAYER_PARTY = ListChooser.chooseType(DC_TYPE.CHARS, ref, conditions);
        launcher.setDungeon(dungeon);
        launcher.PARTY_CODE = CODE.PRESET;
        launcher.ENEMY_CODE = CODE.NONE;
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        DC_Engine.gameStartInit();
        DC_Game game = launcher.initDC_Game();
        game.start(true);
    }

    private static void launchFastDc() {

        DC_Engine.jarInit();
        FontMaster.init();
        GuiManager.init();
        String dungeon = new FileChooser(PathFinder.getDungeonLevelFolder()).launch("", "");
        FAST_DC.DEFAULT_DUNGEON = (
         dungeon);

        Ref ref = new Ref();
        Condition conditions = new Conditions();
        FAST_DC.PLAYER_PARTY = ListChooser.chooseType(DC_TYPE.CHARS, ref, conditions);

        FAST_DC.main(new String[]{String.valueOf(PresetLauncher.OPTION_NEW)}
        );

    }


}
