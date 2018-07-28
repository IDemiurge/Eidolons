package eidolons.game.module.dungeoncrawl.generator;

import eidolons.system.options.Options.OPTION;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GeneratorEnums {

    public enum EXIT_TEMPLATE {
        /*
#####
OOOOE
#####
         */
        THROUGH,
        /*
#####
OOO##
##E##
         */
        ANGLE,
        /*
##E##
OOOO#
##E##
         */
        FORK,
        /*
##E##
OOOOE
##E##
         */
        CROSSROAD,
        /*
#####
OOO##
#####
         */
        CUL_DE_SAC,
    }

    public enum GRAPH_NODE_ATTRIBUTE {
        LOCK,
    }

    public enum GRAPH_RULE {
        EXTEND,
        LOCK,
        SHORTCUT,
        DUPLICATE,
        TWIN_PATH,
        BLOCK,
        CIRCLE_BACK,
    }

    public enum LEVEL_GRAPH_LINK_TYPE {
        NORMAL, AMBUSH, LONG, LOCKED,
    }

    public enum LEVEL_VALUES
     //for manual skirmish generation; but we'll need more ad hoc values...
     implements OPTION {
        //GRAPH

        //MODEL
        ZONES,

        //RENDER
        PREFERRED_STYLE_1,

        //GENERAL
        DUNGEON_TYPE,

        WIDTH,
        HEIGHT,
        Z_LEVEL,

        DOOR_CHANCE_COMMON(75, 0, 100),
        WRAP_ROOMS(1, 0, 2),
        WRAP_CELL_TYPE(ROOM_CELL.VOID.getSymbol(), ROOM_CELL.DESTRUCTIBLE.getSymbol(), ROOM_CELL.WALL.getSymbol()),

        TREASURE_ROOM_COEF(3, 0, 10),
        THRONE_ROOM_COEF(1, 0, 3),
        DEATH_ROOM_COEF(2, 0, 10),
        GUARD_ROOM_COEF(4, 0, 7),
        COMMON_ROOM_COEF(6, 0, 12),
        SECRET_ROOM_COEF(1, 0, 10),

        SIZE_MODE(100, 50, 300);
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        LEVEL_VALUES(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        LEVEL_VALUES(Object... options) {
            this.options = options;
            if (options.length > 0)
                defaultValue = options[0];
        }

        LEVEL_VALUES(Integer defaultValue, Integer min, Integer max) {
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;

        }

        @Override
        public Integer getMin() {
            return min;
        }

        @Override
        public Integer getMax() {
            return max;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return exclusive;
        }

        @Override
        public Object[] getOptions() {
            return options;
        }
    }

    public enum PATH_TYPE {
        secret, hard, easy,
    }

    public enum ROOM_CELL { //water, void
        WALL("#"),
        FLOOR("O"),
        ENTRANCE("E"),
        EXIT("e"),
        CONTAINER("C"),
        DOOR("D"),
        ART_OBJ("A"),
        DESTRUCTIBLE_WALL("@"),
        SECRET_DOOR("S"),
        GUARD("G"),
        MINI_BOSS("b"),
        BOSS("B"),

        LIGHT_EMITTER("L"),
        WALL_WITH_LIGHT_OVERLAY("@"),
        WALL_WITH_DECOR_OVERLAY("$"),
        LOCAL_KEY("k"),
        GLOBAL_KEY("K"),
        DESTRUCTIBLE("X"),


        //ROOM_TYPES*
        TREASURE_ROOM("t"),
        THRONE_ROOM("h"),
        DEATH_ROOM("d"),
        GUARD_ROOM("g"),
        COMMON_ROOM("c"),
        EXIT_ROOM("x"),
        SECRET_ROOM("s"),
        ENTRANCE_ROOM("n"),
        CORRIDOR("l") {
            @Override
            public String toString() {
                return name();
            }
        },

        TRAP("T"),
        SPECIAL_CONTAINER("c"),
        SPECIAL_DOOR("d"),
        SPECIAL_ART_OBJ("a"),
        /*
    false_wall,
    BUTTON,
    SHRINE,

     */
        VOID(".")
        ;
        static ROOM_CELL[] vals = ROOM_CELL.values();
        public final String symbol;

        ROOM_CELL(String symbol) {
            this.symbol = symbol;
        }

        public static ROOM_CELL getBySymbol(String symbol) {
            for (ROOM_CELL sub : vals) {
                if (sub.getSymbol().equals(symbol)) {
                    return sub;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            if (name().endsWith("ROOM"))
                return super.toString();
            return getSymbol();
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum ROOM_TEMPLATE_GROUP {
        CRYPT,
        CAVE,
        MAZE,
        CASTLE,
        TEMPLE,
        TOWER,
        DUNGEON,
        RANDOM,
        RANDOM_INTERIOR,

    }

    public enum ZONE_TYPE {
        BOSS_AREA,
        OUTSKIRTS,
        MAIN_AREA,
        ENTRANCE,
    }
}
