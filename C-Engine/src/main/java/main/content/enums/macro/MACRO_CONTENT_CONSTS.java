package main.content.enums.macro;

import main.system.auxiliary.StringMaster;

public class MACRO_CONTENT_CONSTS {

    public static final String PLACE_POSITION_VAR = "x;y";

    public enum DANGER_LEVEL {
        SAFE, UNCHECKED, UNSAFE, DANGEROUS, SUICIDAL,

    }

    public enum DAY_TIME { //different names in summer?..
        DAWN(4, true, true)  ,
        MORNING(8, false, true),
        NOON(12, false, true),
        DUSK(16, false, true),
        NIGHTFALL(20, false, false),
        MIDNIGHT(24, true, false),
        ;
        //8, 12, 16, 20, 24, 4
        int hour;
        private boolean undersunVisible;
        private boolean sunVisible;

        DAY_TIME(int hour, boolean undersunVisible, boolean sunVisible) {
            this.hour = hour;
            this.undersunVisible = undersunVisible;
            this.sunVisible = sunVisible;
        }

        public String getText() {
            return StringMaster.getWellFormattedString(toString());
        }

        public boolean isUndersunVisible() {
            return undersunVisible;
        }


        public boolean isSunVisible() {
            return sunVisible;
        }

    }

    public enum MISSION {
        TRANSCENDENCE, ASCENSION, DISSOLUTION, DOMINION, LIBERATION,
        WORLD_PEACE,
        // ARMAGEDDON,
        // SUBMISSION,
        // ENLIGHTENMENT,
        // BARBARISM,

    }

    public enum PLACE_TYPE {
        DUNGEON, LOCATION, BUILDING,

    }

    public enum ROUTE_TYPE {
        // movement %?
        ROAD, PATH, TUNNEL, SEA_ROUTE
    }

    public enum TERRAIN_TYPE {
        PLAINS, MOUNTAINS, UNDERGROUND, HILLS, FOREST, SWAMP, TUNDRA, DESERT,
        JUNGLE, SEA, CITY

    }
}
