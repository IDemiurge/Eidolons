package main.content.enums.macro;

import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class MACRO_CONTENT_CONSTS {

    public static final String PLACE_POSITION_VAR = "x;y";

    public enum DANGER_LEVEL {
        SAFE, UNCHECKED, UNSAFE, DANGEROUS, SUICIDAL,

    }

    public enum WEATHER {
        CLEAR, OVERCAST, STORM,
        MISTY,

    }
        public enum DAY_TIME { //different names in summer?..
        MIDNIGHT(true,24, true, false, "It is midnight."),
        DAWN(true, 4, true, true, "The sun rises...")  ,
        MORNING(false, 8, false, true, "It is morning"),
        NOON(false, 12, false, true, "It is noon."),
        DUSK(false, 16, false, true, "The sun sets..."),
        NIGHTFALL(true,20, false, false, "The night falls..."),
        ;
        public static DAY_TIME[] values=values();
        //8, 12, 16, 20, 24, 4
        int hour;
        private boolean undersunVisible;
        private boolean sunVisible;
        private String logEntry;
        private boolean night;

        DAY_TIME(boolean night,int hour, boolean undersunVisible, boolean sunVisible, String logEntry) {
            this.hour = hour;
            this.undersunVisible = undersunVisible;
            this.sunVisible = sunVisible;
            this.night = night;
            this.logEntry = logEntry;
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

        public String getLogEntry() {
            return logEntry;
        }

        public boolean isNight() {
            return night;
        }

        public void setNight(boolean night) {
            this.night = night;
        }

        public DAY_TIME getNext() {
            return  new EnumMaster<DAY_TIME>().getNextEnumConst(DAY_TIME.class, this);
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
