package eidolons.libgdx.screens.menu;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.global.persist.Loader;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;

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
        scenarioTypes= (List<ObjType>) SortMaster.sortByValue(scenarioTypes, G_PROPS.ID, false);

//        if (CoreEngine.isLiteLaunch()) {
//            scenarioTypes =
//                    new ListMaster<ObjType>().getList(DataManager.getType("Hall of Lord", DC_TYPE.SCENARIOS));
//        }

        if (random_preset_select == null) {

        } else {
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
        Flags.setMacro(false);
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL, scenarioTypes);
        return null;
    }

    private static boolean isRngLocationSupported(LOCATION_TYPE locationType) {
        if (locationType==null ){
            return true;
        }
        switch (locationType) {
//            case CEMETERY:
            case CRYPT:
            case CAVE:
            case DUNGEON:
//            case TOWER:
//            case TEMPLE:
//                return true;

            case CASTLE:
                break;
        }
        return false;
    }

    public static List<ObjType> getScenarioTypes() {
        return getScenarioTypes(getScenarioGroup(false));
    }

    private static String getScenarioGroup(Boolean test) {
        // if (test==null )
        //     return "Demo";
        //        return "Crawl";
        return test ? "Test" : "Quest";
    }

    public static List<ObjType> getScenarioTypes(String scenarioGroup) {
        return DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
         StringMaster.format(scenarioGroup));
    }

    private void startDemo() {
        startMicro(getScenarioTypes(getScenarioGroup(null )), null );
    }
    public Boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case TEST_MODE:
                return startMicro(getScenarioTypes(getScenarioGroup(true)),
                        null );
            // case CAMPAIGN:
            //     EidolonsGame.DEMO=true;
            //     IGG_Launcher.start(()-> startDemo());
            //     return null ;
            case PLAY:
                if (item.getItems().length>0) {
                    break;
                }
            case RANDOM_SCENARIO:
                return startMicro(getScenarioTypes(getScenarioGroup(false)),
                 true);
            case SELECT_SCENARIO:
                return startMicro(getScenarioTypes(getScenarioGroup(false)),
                        null  );
            // case CUSTOM_DUNGEON:
            //     return startMicro(getScenarioTypes(),
            //      null);


            case MAP_PREVIEW:
                AdventureInitializer.launchAdventureGame(null);
                return null;
            case LOAD:
                try {
                    Loader.load();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    Flags.setMacro(false);
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
