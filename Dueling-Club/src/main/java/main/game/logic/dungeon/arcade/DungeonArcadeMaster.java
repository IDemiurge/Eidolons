package main.game.logic.dungeon.arcade;

import main.data.xml.XML_Converter;
import main.game.logic.dungeon.Location;

public class DungeonArcadeMaster {
    /*
	 * No Proto-Macro? 
	 * 
	 * Just sequential dungeons... with dialogues/choices/regrouping in between 
	 * 
	 * Target-Scenarios 
	 * 
	 */
    //

    /*
     * generate arcade.xml data file
     * THOUGH some things could as well be generated on the spot to keep the mystery
     *
     * use it to determine the next mission, (scenario!)
     * for global settings,
     * for objectives/quests,
     */
    public void initArcade() {
//        arcade.toString();
//        String xml = "";
//
//        // working methods - nextLevel() etc
//
//        XML_Converter.wrapLeaf(valName, value);
//
//        // generate arcade plan
//        // ++ non-linear? branching
//
//        // SCENARIO HAS PLACE TYPE FOR SHOP/T IN ARC/CAMP
//        new Location(scenario);
        // change settlement

        // party management

        // generate missions

    }

    public enum ARCADE_PRESET {
        // AV?
    }

    public enum ARCADE_THEME {
        ERSIDRIS_CLASSIC, ROGUE, UNDERGROUND, UNDEAD, ARCANE, MIST

    }

    public enum ARCADE_PARAMS {
        GRIT,
        // N OF SUBMISSIONS
        // OF SUBQUESTS

    }

    public enum ARCADE_OBJECTIVE_TYPE {
        SURVIVE, GO_THRU, ANY_BOSS, FINAL_BOSS,
    }

    public enum ARCADE_MODS {
        IRON, SOLO, FAST, STRATEGIC, LINEAR, COMPLEX, STATIC_PARTY, IMMORTAL,
    }

    public enum ARCADE_MODE {
        MULTI_BOSS,

    }

}
