package main.libgdx.screens.menu;

import com.badlogic.gdx.Gdx;
import main.content.DC_TYPE;
import main.data.DataManager;
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

                return false ;
            case OPTIONS:
                OptionsMaster.init( );
                OptionsMaster.openMenu();
            return true;
            case MANUAL:
                GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL , ""); //null closes!

                return false ;
            case ABOUT:
                break;
            case EXIT:
                Gdx.app.exit();
                break;
        }

        return true;
    }
}
