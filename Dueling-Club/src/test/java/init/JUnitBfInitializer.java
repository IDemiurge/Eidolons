package java.init;

import main.client.DC_Engine;
import main.game.core.game.DC_Game;

import java.res.JUnitResources;
import java.tests.JUnitTests;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitBfInitializer {

    private static DC_Game game;
    private static JUnitTests tests;

    public static void main(String[] strings) {
//        PathFinder.setJUnitMode(true);
        DC_Engine.systemInit();
        JUnitResources.init();

//        GameLauncher
//        PresetLauncher.initLaunch(LAUNCH.JUNIT);
        game = new DC_Game();
        game.init();
        game.start(true);
        tests = new JUnitTests();
        tests.run();

    }
}
