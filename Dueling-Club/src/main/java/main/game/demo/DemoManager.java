package main.game.demo;

import main.game.logic.arcade.Arcade;

/**
 * Created by JustMe on 5/3/2017.
 */
public class DemoManager {


    public static void loadLast() {

    }
    public static void showInfo() {

    }
    public static void save() {

    }
    public static void hqEntered() {
        initHero();
        initParty();
        initArcade();
    }

    private static void initParty() {
    }

    private static void initHero() {
    }

    public static void battleEntered() {
        
    }
    private static void initArcade() {
        Arcade arcade = new Arcade();
//        arcade.setLevel(level);
        
    }

    public static void init() {
            initDungeon();
    }

    private static void initDungeon() {
//        Eidolons.game.getDungeonMaster().
        
    }
}
