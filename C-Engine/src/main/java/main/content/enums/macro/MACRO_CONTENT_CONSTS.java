package main.content.enums.macro;

public class MACRO_CONTENT_CONSTS {

    public static final String PLACE_POSITION_VAR = "x;y";

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
