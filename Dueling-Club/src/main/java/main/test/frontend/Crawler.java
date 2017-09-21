package main.test.frontend;

import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.GameLauncher;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.lists.ListChooser;

/**
 * Created by JustMe on 9/21/2017.
 */
public class Crawler {

    public static void main(String[] args) {
    /*
    choose dungeon from crawl folder?
    generate mission per dungeon?
         */
        DC_Game game;
        GameLauncher launcher = new GameLauncher(GAME_SUBCLASS.SCENARIO);
        launcher.setDungeon(
         new FileChooser(PathFinder.getDungeonLevelFolder()).launch("","") );

        Ref ref= new Ref();
        Condition conditions = new Conditions();
        launcher.PLAYER_PARTY = ListChooser.chooseType(DC_TYPE.CHARS, ref, conditions);
        game = launcher.initDC_Game();
        game.start(true);
//        launcher.la


    }


}
