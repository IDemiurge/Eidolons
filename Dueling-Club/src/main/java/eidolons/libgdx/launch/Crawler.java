package eidolons.libgdx.launch;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import eidolons.game.core.launch.GameLauncher;
import eidolons.game.core.launch.PresetLauncher;
import eidolons.game.core.launch.PresetLauncher.LAUNCH;
import eidolons.game.core.launch.TestLauncher.CODE;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.test.frontend.FAST_DC;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 9/21/2017.
 */
public class Crawler {


    public static final String launchDataPath = PathFinder.getXML_PATH() + "crawler last.txt";
    public static String launchData = "";
    private static String[] launch_options = {
     "Last", "New", "Random", "Standard", "Test"
    };

    public static void main(String[] args) {
        FAST_DC.FAST_MODE = false;
        ExplorationMaster.setTestMode(true);
        DC_Engine.jarInit();
        FontMaster.init();
        GuiManager.init();
        int index = DialogMaster.optionChoice(launch_options,
         "Choose the type of Eidolons game you want to launch...");
        List<String> parts = null;
        if (index == 0) {
            String data = FileManager.readFile(launchDataPath);
            parts = StringMaster.openContainer(data);
            index = 1;
        }
        if (index == 2) {
//    random  = true;
//    parts = Collections.nCopies(2, "");
        } else if (index == 3) {
            FAST_DC.main(new String[]{
             "" +
              (Arrays.asList(
               PresetLauncher.LAUNCH_OPTIONS)).
               indexOf(StringMaster.getWellFormattedString(LAUNCH.EXPLORATION.toString()))
            });

            return;
        } else if (index == 4) {
            FAST_DC.main(new String[]{
             "" +
              (Arrays.asList(
               PresetLauncher.LAUNCH_OPTIONS)).
               indexOf(StringMaster.getWellFormattedString(LAUNCH.EXPLORATION_TEST.toString()))
            });
            return;
        }
        DemoLauncher.main(null);
        DC_Engine.mainMenuInit();
        String dungeon = parts == null ? "crawl" + new FileChooser(PathFinder.getDungeonLevelFolder() + "crawl").launch("", "")
         : parts.get(0);
        launchData += dungeon;
        dungeon = StringMaster.removePreviousPathSegments(dungeon, PathFinder.getDungeonLevelFolder());
        ScreenData data = new ScreenData(ScreenType.BATTLE,
         dungeon
        );
        GameLauncher launcher = new GameLauncher(GAME_SUBCLASS.TEST);
        Ref ref = new Ref();
        Condition conditions = new Conditions();
        launcher.PLAYER_PARTY = parts == null ? ListChooser.chooseType(DC_TYPE.CHARS, ref, conditions)
         : parts.get(1);
        launchData += StringMaster.SEPARATOR + launcher.PLAYER_PARTY;
        launcher.setDungeon(dungeon);
        launcher.PARTY_CODE = CODE.PRESET;
        launcher.ENEMY_CODE = CODE.NONE;
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        DC_Engine.gameStartInit();
        DC_Game game = launcher.initDC_Game();
        game.start(true);

        if (!StringMaster.isEmpty(launchData)) {
            FileManager.write(launchData, launchDataPath);

        }
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
