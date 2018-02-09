package main.libgdx.screens.menu;

import com.badlogic.gdx.Gdx;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.game.module.adventure.MacroManager;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainMenuHandler {

    public Boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case CRAWL:
            case STANDOFF:
            case SKIRMISH:
                GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                 DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
                  StringMaster.getWellFormattedString(item.toString())));

                return null  ;
            case ADVENTURE:

                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(
                 ScreenType.MAP, "Map"));

                return null  ;
            case OPTIONS:
                OptionsMaster.init( );
                OptionsMaster.openMenu();
            return false;
            case MANUAL:
                GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL , ""); //null closes!

                return null  ;
            case ABOUT:
                break;
            case EXIT:
                Gdx.app.exit();
                break;
        }

        return true;
    }
}
