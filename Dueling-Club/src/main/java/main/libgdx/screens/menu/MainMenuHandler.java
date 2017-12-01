package main.libgdx.screens.menu;

import com.badlogic.gdx.Gdx;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainMenuHandler {

    public boolean handle(MAIN_MENU_ITEM item) {
        switch (item) {
            case CRAWL:
            case STANDOFF:
            case SKIRMISH:
                GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                 DataManager.getTypesGroup(DC_TYPE.SCENARIOS,
                  StringMaster.getWellFormattedString(item.toString())));

                return true;
            case OPTIONS:
                break;
            case MANUAL:
                break;
            case ABOUT:
                break;
            case EXIT:
                Gdx.app.exit();
                break;
        }

        return true;
    }
}
