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
        LOCK_MAIN, KEY_MAIN,
        LOCK, KEY,
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

    public enum LEVEL_DATA_MODIFICATION {
        DOUBLE_ROOM_CHANCE,
        HALF_ROOM_CHANCE,
        NO_FILL,
        DOUBLE_FILL,
        HALF_FILL,

        NO_RANDOM_EXITS,
        NO_LINKLESS,
        NO_LINKS,
        NO_DOORS,

        WRAP_ROOMS,
        WRAP_TYPE,


        LENGTHEN_MAIN_PATHS,
        LENGTHEN_BONUS_PATHS,
        SHORTEN_MAIN_PATHS,
        SHORTEN_BONUS_PATHS,

        DOUBLE_MAIN_PATHS,
        HALF_BONUS_PATHS,
        DOUBLE_BONUS_PATHS,

        HALF_ZONES,
        SINGLE_ZONE,


        INCREASE_SIZE,
        INCREASE_ROOM_COUNT,
        DECREASE_SIZE,
        DECREASE_ROOM_COUNT,
        NO_ROOM_CHANCE, NO_RANDOM_ROTATIONS;
    }

    public enum LEVEL_VALUES
            //for manual skirmish generation; but we'll need more ad hoc values...
            implements OPTION {

        //MODEL
        ZONES,

        RANDOM_EXIT_CHANCE(0, 0, 100),
        DOOR_CHANCE_MOD(100, 0, 100),
        DOOR_CHANCE_THRONE_ROOM(75, 0, 100),
        DOOR_CHANCE_COMMON_ROOM(75, 0, 100),
        DOOR_CHANCE_CORRIDOR(75, 0, 100),
        DOOR_CHANCE_TREASURE_ROOM(75, 0, 100),
        DOOR_CHANCE_DEATH_ROOM(75, 0, 100),
        DOOR_CHANCE_GUARD_ROOM(75, 0, 100),
        DOOR_CHANCE_ENTRANCE_ROOM(75, 0, 100),
        DOOR_CHANCE_EXIT_ROOM(75, 0, 100),
        DOOR_CHANCE_SECRET_ROOM(75, 0, 100),

        WRAP_ROOMS(1, 0, 2),
        WRAP_CELL_TYPE(ROOM_CELL.INDESTRUCTIBLE.getSymbol(), ROOM_CELL.DESTRUCTIBLE.getSymbol(), ROOM_CELL.WALL.getSymbol()),
        VOID_CELL_TYPE(ROOM_CELL.INDESTRUCTIBLE.getSymbol(), ROOM_CELL.DESTRUCTIBLE.getSymbol(), ROOM_CELL.WALL.getSymbol()),

        TREASURE_ROOM_COEF(2, 0, 10),
        THRONE_ROOM_COEF(1, 0, 3),
        DEATH_ROOM_COEF(2, 0, 10),
        GUARD_ROOM_COEF(3, 0, 7),
        COMMON_ROOM_COEF(5, 0, 12),
        SECRET_ROOM_COEF(1, 0, 10),

        SIZE_MOD(63, 10, 300),
        ROOM_COUNT_MOD(30, 10, 300),
        MAIN_PATHS(1, 0, 5),
        BONUS_PATHS(1, 0, 10),
        MAIN_PATH_LENGTH(2, 2, 20),
        BONUS_PATH_LENGTH(1, 1, 15),

        RANDOM_ROTATION_CHANCE(65, 0, 100),

        FILL_LIGHT_EMITTER_COEF(50, 0, 500),
        FILL_WALL_WITH_LIGHT_OVERLAY_COEF(100, 0, 500),
        FILL_WALL_WITH_DECOR_OVERLAY_COEF(100, 0, 500),
        FILL_DESTRUCTIBLE_COEF(100, 0, 500),
        FILL_CONTAINER_COEF(100, 0, 500),
        FILL_SPECIAL_CONTAINER_COEF(100, 0, 500),
        FILL_ART_OBJ_COEF(100, 0, 500),
        FILL_SPECIAL_ART_OBJ_COEF(100, 0, 500),
        FILL_OVERLAY_COEF(100, 0, 500),

        //                                       <><><><><>

        SPAWN_GROUP_COEF_IDLERS(50, 0, 500),
        SPAWN_GROUP_COEF_CROWD(50, 0, 500),
        SPAWN_GROUP_COEF_GUARDS(100, 0, 500),
        SPAWN_GROUP_COEF_AMBUSH(40, 0, 500),
        SPAWN_GROUP_COEF_PATROL(30, 0, 500),
        SPAWN_GROUP_COEF_STALKER(20, 0, 500),
        SPAWN_GROUP_COEF_BOSS(100, 0, 500),


        RANDOMIZED_SIZE_SORT_CHANCE(95, 0, 100),
        CHANCE_LINKLESS(55, 0, 100),
        CHANCE_LINKLESS_MOD(150, 0, 500),
        POWER_PER_SQUARE_MAX_MOD(100, 0, 500),
        SURFACE(),

        //RENDER
        PREFERRED_STYLE_1,

        //GENERAL
        DUNGEON_TYPE,

        //GRAPH1
        WIDTH,
        HEIGHT,
        Z_LEVEL, CLEAN_DISABLED(), FILL_GLOBAL_COEF(),
        ADDITIONAL_FILL_RUNS(0, 0, 10);
        public static final LEVEL_VALUES[] FILL_VALS = {
                FILL_LIGHT_EMITTER_COEF,
                FILL_WALL_WITH_LIGHT_OVERLAY_COEF,
                FILL_WALL_WITH_DECOR_OVERLAY_COEF,
                FILL_DESTRUCTIBLE_COEF,
                FILL_CONTAINER_COEF,
                FILL_SPECIAL_CONTAINER_COEF,
                FILL_ART_OBJ_COEF,
                FILL_SPECIAL_ART_OBJ_COEF,
                FILL_OVERLAY_COEF,
        };
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
        VOID("-"),
        INDESTRUCTIBLE("I"),
        WALL("#"),
        FLOOR("O"),
        ENTRANCE("E"),
        EXIT("X"),
        ROOM_EXIT("e"),
        CONTAINER("C"),
        DOOR("D"),
        ART_OBJ("A"),
        DESTRUCTIBLE_WALL("*"),
        SECRET_DOOR("S"),
        SECRET_DOOR_BUTTON("!"),

        GUARDS("G"),
        PATROL("P"),
        AMBUSH("M"),
        CROWD("W"),
        IDLERS("i"),
        STALKER("K"),
        MINI_BOSS("b"),
        BOSS("B"),

        LIGHT_EMITTER("L"),
        WALL_WITH_LIGHT_OVERLAY("@"),
        WALL_WITH_DECOR_OVERLAY("$"),
        LOCAL_KEY("k"),
        GLOBAL_KEY("K"),
        DESTRUCTIBLE("%"),

//RANDOM

        RANDOM_PASSAGE("p", "D(5);floor(5);"),
        RANDOM_SPAWN_GROUP("u"),
        RANDOM_OBJECT("r", ""),


        //ROOM_TYPES*
        TREASURE_ROOM("t"),
        THRONE_ROOM("h"),
        DEATH_ROOM("d"),
        GUARD_ROOM("g"),
        COMMON_ROOM("m"),
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
        ZONE_0("0"),
        ZONE_1("1"),
        ZONE_2("2"),
        ZONE_3("3"),
        /*
    false_wall,
    BUTTON,
    SHRINE,

     */;
        static ROOM_CELL[] vals = ROOM_CELL.values();
        public final String symbol;
        private String randomWeightMap;

        ROOM_CELL(String symbol, String randomWeightMap) {
            this.symbol = symbol;
            this.randomWeightMap = randomWeightMap;
        }

        ROOM_CELL(String symbol) {
            this.symbol = symbol;
        }

        public static ROOM_CELL getBySymbol(String symbol) {
            if (symbol.equals("."))
                return FLOOR; //TODO better replace O's en masse...
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

        public String getRandomWeightMap() {
            return randomWeightMap;
        }

    }

    public enum ROOM_TEMPLATE_GROUP {
        CRYPT, //claustrophobic
        CAVE, //narrow and snaking
        CASTLE, //square square square
        TEMPLE, //diamond preference
        TOWER, //narrow and stylish
        DUNGEON, //classic and simple
        MAZE, //always spinning, fractal like
        PUZZLE_MAZE, //always spinning, fractal like
        GROVE, //chaos...
        CEMETERY, //enclosed areas
        RANDOM, //will mix all
//        RANDOM_INTERIOR, //except cave/grove...

        CAVE_MAZE, //60/40 chance
        CASTLE_TEMPLE,
        CASTLE_TOWER,
        TEMPLE_DUNGEON,
        TEMPLE_CRYPT,
        TOWER_TEMPLE,
        DUNGEON_CASTLE,
        CRYPT_TOWER,
        ;

        public boolean isMultiGroup() {
            if (this == PUZZLE_MAZE) {
                return false;
            }
            return name().contains("_");

        }

        public ROOM_TEMPLATE_GROUP getMultiGroupOne() {

            return valueOf(name().split("_")[0]);
        }

        public ROOM_TEMPLATE_GROUP getMultiGroupTwo() {
            return valueOf(name().split("_")[1]);
        }
    }

    public enum ZONE_TYPE {
        BOSS_AREA,
        OUTSKIRTS,
        MAIN_AREA,
        ENTRANCE,
    }
}
