package main.content.enums.macro;

import main.system.auxiliary.StringMaster;

public class MACRO_CONTENT_CONSTS {

    public static final String PLACE_POSITION_VAR = "x;y";

    public enum DAY_TIME { //different names in summer?..
        DAWN, NOON, DUSK, NIGHTFALL, MIDNIGHT, HOUR_OF_WOLF{
            @Override
            public String getText() {
                return "The Hour of Wolf";
            }
        },
        ;
        //8, 12, 16, 20, 24, 4

        public String getText() {
            return StringMaster.getWellFormattedString(toString());
        }
    }
        public enum TERRAIN_TYPE {
        PLAINS, MOUNTAINS, UNDERGROUND, HILLS, FOREST, SWAMP, TUNDRA, DESERT,
        JUNGLE, SEA, CITY

    }

    public enum PLACE_TYPE {
        DUNGEON, LOCATION, BUILDING,

    }

    public enum DANGER_LEVEL {
        SAFE, UNCHECKED, UNSAFE, DANGEROUS, SUICIDAL,

    }

    public enum ROUTE_TYPE {
        // movement %?
        ROAD, PATH, TUNNEL, SEA_ROUTE
    }

    public enum MISSION {
        TRANSCENDENCE, ASCENSION, DISSOLUTION, DOMINION, LIBERATION,
        WORLD_PEACE,
        // ARMAGEDDON,
        // SUBMISSION,
        // ENLIGHTENMENT,
        // BARBARISM,

    }
}
