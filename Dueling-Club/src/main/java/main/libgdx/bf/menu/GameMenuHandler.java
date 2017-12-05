package main.libgdx.bf.menu;

import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.options.OptionsMaster;
import main.system.text.HelpMaster;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenuHandler {
    public Boolean clicked(GAME_MENU_ITEM sub) {
        switch (sub) {
            case QUICK_HELP:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getHelpText());
                break;
            case HERO_INFO:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getWelcomeText());
                break;
            case EXIT:
//                DC_Game.game.getBattleMaster().getOutcomeManager().next();
//                DC_Game.game.exit(true);
                try {
                    DC_Game.game.getMetaMaster().gameExited();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
//                Gdx.input.setInputProcessor(new InputAdapter());
                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                 new ScreenData(ScreenType.MAIN_MENU, "Loading..."));
                Eidolons.gameExited();
                GameMenu.menuOpen = false;
                return false;
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
        return null;
    }
}
