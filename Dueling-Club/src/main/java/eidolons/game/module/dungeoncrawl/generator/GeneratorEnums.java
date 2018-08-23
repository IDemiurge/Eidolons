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
        LOCK, KEY ,
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

        SIZE_MOD(100, 10, 300),
        ROOM_COUNT_MOD(100, 10, 300),
        MAIN_PATHS(1, 0, 5), BONUS_PATHS(2, 0, 10),
        MAIN_PATH_LENGTH(3, 2, 20),
        BONUS_PATH_LENGTH(2, 1, 15), RANDOM_ROTATION_CHANCE(65, 0, 100),
        FILL_LIGHT_COEF(100, 0, 500),

        //                                       <><><><><>

        SPAWN_GROUP_COEF_IDLERS(50, 0, 500),
        SPAWN_GROUP_COEF_CROWD(50, 0, 500),
        SPAWN_GROUP_COEF_GUARDS(100, 0, 500),
        SPAWN_GROUP_COEF_AMBUSH(40, 0, 500),
        SPAWN_GROUP_COEF_PATROL(30, 0, 500),
        SPAWN_GROUP_COEF_STALKER(20, 0, 500),
        SPAWN_GROUP_COEF_BOSS(100, 0, 500),


        RANDOMIZED_SIZE_SORT_CHANCE(35, 0, 100),
        CHANCE_LINKLESS(35, 0, 100),
        CHANCE_LINKLESS_MOD(100, 0, 500),
        SURFACE();
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
        PATROL("G"),
        AMBUSH("G"),
        CROWD("G"),
        IDLERS("G"),
        STALKER("G") ,
        MINI_BOSS("b"),
        BOSS("B"),

        LIGHT_EMITTER("L"),
        WALL_WITH_LIGHT_OVERLAY("@"),
        WALL_WITH_DECOR_OVERLAY("$"),
        LOCAL_KEY("k"),
        GLOBAL_KEY("K"),
        DESTRUCTIBLE("%"),


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
        ZONE_0("0"),
        ZONE_1("1"),
        ZONE_2("2"),
        ZONE_3("3"),
        /*
    false_wall,
    BUTTON,
    SHRINE,

     */
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
        CRYPT, //claustrophobic
        CAVE, //narrow and snaking
        CASTLE, //square square square
        TEMPLE, //diamond preference
        TOWER, //narrow and stylish
        DUNGEON, //classic and simple
        MAZE, //always spinning, fractal like
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
        CRYPT_TOWER,;

        public boolean isMultiGroup() {
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
