package eidolons.system.test;

import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;

/**
 * Created by JustMe on 5/16/2018.
 */
public class Debugger {

    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String NO_GAME = "No game!";
    private static final String NO_GAME_LOOP = "No game loop!";
    private static final String NO_GAME_LOOP_THREAD = "No game loop thread!";

    private static final String NO_HERO = "No main hero!";
    private static final String NOT_ACTIVE = "Main hero is not the active unit!";
    private static final String NOT_PLAYERS_HERO = "Main hero is not player's heroobj!";

    public static String getGameStateSnapshot() {
        String data = "Data: \n";
        data += "Explore mode \n" + ExplorationMaster.isExplorationOn();
        data += "Thread status \n" + getThreadStatus();
        data += "Main hero state\n" + getMainHeroStatus();
        data += "Game loop state \n" + getGameLoopStatus();
        return data;
    }

    private static String getGameLoopStatus() {
        if (DC_Game.game == null)
            return NO_GAME;
        if (DC_Game.game.getGameLoop() == null)
            return NO_GAME_LOOP;
        if (DC_Game.game.getGameLoop() == null)
            return NO_GAME_LOOP_THREAD;
        return null;
    }

    private static String getMainHeroStatus() {
        try {
            if (Eidolons.getMainHero() == null)
                return NO_HERO;
            if (Eidolons.getMainHero() != DC_Game.game.getGameLoop().getActiveUnit())
                return NOT_ACTIVE;
            if (Eidolons.getMainHero() != DC_Game.game.getPlayer(true).getHeroObj())
                return NOT_PLAYERS_HERO;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return ERROR + " " + e.getMessage() + e.getCause();
        }
        return OK;
    }

    private static String getThreadStatus() {

        Thread[] array = new Thread[100];
        int count = Thread.enumerate(array);
        String msg = "Current Thread: " + Thread.currentThread().getName();
        msg += "\n" + count +
                " threads: ";
        StringBuilder msgBuilder = new StringBuilder(msg);
        for (Thread sub : array) {
            msgBuilder.append(sub.getName()).append("\n");
        }
        msg = msgBuilder.toString();
        return msg;
    }

    public static void writeLog() {

    }

    public static boolean isImmortalityOn() {
        return OptionsMaster.getGameplayOptions().
                getBooleanValue(GAMEPLAY_OPTION.IMMORTALITY);
    }

    public static void validateInvisibleUnitView(BaseView baseView) {

    }

}
