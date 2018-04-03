package eidolons.libgdx.screens.menu;

import com.badlogic.gdx.Gdx;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainMenuHandler {

    private final MainMenu menu;

    public MainMenuHandler(MainMenu mainMenu) {
        menu = mainMenu;
    }

    public Boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case CRAWL:
            case STANDOFF:
            case SKIRMISH:
                CoreEngine.setMacro(false);
                GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                 DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
                  StringMaster.getWellFormattedString(item.toString())));

                return null;
            case ADVENTURE:
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
                Gdx.app.exit();
                break;
        }

        return true;
    }
}
