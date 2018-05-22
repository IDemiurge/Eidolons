package eidolons.libgdx.screens.menu;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainMenuHandler {

    private final MainMenu menu;

    public MainMenuHandler(MainMenu mainMenu) {
        menu = mainMenu;
    }

    public static Boolean startMicro(List<ObjType> scenarioTypes, Boolean random_preset_select) {
        if (random_preset_select != null) {
            if (random_preset_select) {
                MainLauncher.presetNumbers.add(0, RandomWizard.getRandomInt(scenarioTypes.size()));
            } else {
                int n = OptionsMaster.getGameplayOptions().
                 getIntValue(GAMEPLAY_OPTION.NEXT_SCENARIO_INDEX);
                MainLauncher.presetNumbers.add(0, n);
            }
        }
        CoreEngine.setMacro(false);
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL, scenarioTypes);
        return null;
    }

    public Boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case NEXT_SCENARIO:
                return startMicro(getScenarioTypes(),
                 false);
            case RANDOM_SCENARIO:
                return startMicro(getScenarioTypes(),
                 true);
            case SELECT_SCENARIO:
            case PLAY:
//          TODO   case STANDOFF:
//            case SKIRMISH:
                return startMicro(getScenarioTypes(),
                 null );
            case MAP_PREVIEW:
                CoreEngine.setMacro(true);
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(
                 ScreenType.MAP, "Mistfall"));

                return null;
            case OPTIONS:
                OptionsMaster.init();
                menu.openOptionsMenu();
                return false;
            case MANUAL:
                GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL, ""); //null closes!

                return null;
            case ABOUT:
                break;
            case EXIT:
                Eidolons.exitGame();
                break;
        }

        return true;
    }
    public static List<ObjType> getScenarioTypes() {
        return getScenarioTypes(getScenarioGroup());
    }

    private static String getScenarioGroup() {
//        return "Crawl";
        return "Beta";
    }

    public static List<ObjType> getScenarioTypes(String scenarioGroup) {
        return DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
         StringMaster.getWellFormattedString(scenarioGroup));
    }

}
