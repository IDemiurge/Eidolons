package tests.utils;

import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 4/11/2018.
 */
public class JUnitUtils {
    DC_Game game;

    public JUnitUtils(DC_Game game) {
        this.game = game;
    }

    public void log(String s) {
        System.out.println(s);
    }
    public static void log_(String s) {
        System.out.println(s);
    }
}
