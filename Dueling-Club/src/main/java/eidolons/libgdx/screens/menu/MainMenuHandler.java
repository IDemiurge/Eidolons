package eidolons.libgdx.screens.menu;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.global.persist.Loader;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
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
                scenarioTypes.removeIf(type ->
                {
                    if (type.isGenerated()) {
                        return true;
                    }
                    LOCATION_TYPE locationType =
                     new EnumMaster<LOCATION_TYPE>().retrieveEnumConst(LOCATION_TYPE.class, type.getName());
                    return !isRngLocationSupported(locationType);
                });
                //                MainLauncher.presetNumbers.add(0, RandomWizard.getRandomInt(scenarioTypes.size()));
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

    private static boolean isRngLocationSupported(LOCATION_TYPE locationType) {
        switch (locationType) {
            case CEMETERY:
            case CRYPT:
            case CAVE:
            case DUNGEON:
            case TOWER:
                return true;

            case TEMPLE:
                break;
            case CASTLE:
                break;
        }
        return false;
    }

    public static List<ObjType> getScenarioTypes() {
        return getScenarioTypes(getScenarioGroup(false));
    }

    private static String getScenarioGroup(boolean rng) {
        //        return "Crawl";
        return rng ? "Random" : "Beta";
    }

    public static List<ObjType> getScenarioTypes(String scenarioGroup) {
        return DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
         StringMaster.getWellFormattedString(scenarioGroup));
    }

    public Boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case NEXT_SCENARIO:
                return startMicro(getScenarioTypes(),
                 false);
            case RANDOM_SCENARIO:
                return startMicro(getScenarioTypes(getScenarioGroup(true)),
                 true);
            case SELECT_SCENARIO:
            case PLAY:
                //          TODO   case STANDOFF:
                //            case SKIRMISH:
                if (!DC_Engine.isRngSupported())
                    return startMicro(getScenarioTypes(),
                     null);
                break;
            case MAP_PREVIEW:
                AdventureInitializer.launchAdventureGame(null);
                return null;
            case LOAD:
                try {
                    Loader.load();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    CoreEngine.setMacro(false);
                    Eidolons.exitToMenu();
                }
                break;
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

}
