package libgdx.bf.menu;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import eidolons.macro.global.persist.Saver;
import eidolons.system.text.HelpMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenuHandler {
    private final GameMenu menu;

    public GameMenuHandler(GameMenu gameMenu) {
        menu = gameMenu;
    }

    public Boolean clicked(GAME_MENU_ITEM sub) {
        switch (sub) {
            case QUICK_HELP:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getHelpText());
                break;
            case TUTORIAL_RECAP:
                DialogueManager.tutorialJournal();
                break;
            case MANUAL:
                GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL,
                 HelpMaster.getHelpText());
                break;
            case HERO_INFO:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getWelcomeText());
                break;
            case MAP_INFO:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 DC_Game.game.getMetaMaster().getDungeonInfo());
                break;
            case OUTER_WORLD:
                //TODO save and send log!
                Eidolons.exitGame();
            case EXIT:
            case MAIN_MENU:
                //                DC_Game.game.getBattleMaster().getOutcomeManager().next();
                //                DC_Game.game.exit(true);
                Eidolons.exitToMenu();
//                GuiEventManager.trigger(GuiEventType.DISPOSE_TEXTURES);
                return false;
            case RESTART:
                Eidolons.restart();
                break;
//            case QUESTS:
//                Eidolons.onThisOrNonGdxThread(() -> {
//                    if (QuestMaster.TEST_MODE) {
//                        Eidolons.getGame().getMetaMaster().getQuestMaster().initQuests();
//                        Eidolons.getGame().getMetaMaster().getQuestMaster().startedQuests();
//                        Eidolons.getGame().getMetaMaster().getQuestMaster().updateQuests();
//                    } else {
//                        GuiEventManager.trigger(GuiEventType.SHOW_QUESTS_INFO,
//                         Eidolons.getGame().getMetaMaster().getQuestMaster().getQuests()  );
//                    }
//                });
//                return null;
            case PASS_TIME:
                Eidolons.getGame().getDungeonMaster().getExplorationMaster()
                 .getTimeMaster().playerWaits();
                break;

//            case LOAD:
//                GameMenu.menuOpen = false;
//                Loader.load();
//                break;

            case SAVE:
                GameMenu.menuOpen = false;
                Saver.save();
                break;
            case ACHIEVEMENTS:
                GuiEventManager.trigger(GuiEventType.SHOW_ACHIEVEMENTS);
                GameMenu.menuOpen = false;
                break;
            case RETREAT:
                if (Flags.isMacro()) {
                    GuiEventManager.trigger(GuiEventType.SHOW_NAVIGATION_PANEL, null);
                    break;
                }
                GameMenu.menuOpen = false;
                Eidolons.onNonGdxThread(()->
                 Eidolons.getGame().getMetaMaster().getTownMaster().tryReenterTown());
                break;
                // GameMenu.menuOpen = false;
                // GuiEventManager.trigger(GuiEventType.BATTLE_FINISHED);
                // break;
            case RESUME:
            case LAUNCH_GAME:
            case ABOUT:
            case WEBSITE:
                break;
            case OPTIONS:
                //                OptionsMaster.openMenu();
                menu.openOptionsMenu();
                //                GuiEventManager.trigger(GuiEventType.OPEN_OPTIONS, MainMenuStage.class);
                return null;
        }
        return null;
    }
}
