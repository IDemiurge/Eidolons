package main.libgdx.bf.menu;

import main.game.core.Eidolons;
import main.libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.options.OptionsMaster;
import main.system.text.HelpMaster;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenuHandler {
    public boolean clicked(GAME_MENU_ITEM sub) {
        switch (sub) {
            case HELP:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getHelpText());
                break;
            case HERO_INFO:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getWelcomeText());
                break;

            case RESTART:
                Eidolons.getGame().getMetaMaster().getBattleMaster().
                 getOutcomeManager().restart();
                break;
            case PASS_TIME:
                Eidolons.getGame().getDungeonMaster().getExplorationMaster()
                 .getTimeMaster().playerWaits();
                break;


            case RESUME:
                break;
            case OPTIONS:
                OptionsMaster.openMenu();

                return false;
        }
        return true;
    }
}
