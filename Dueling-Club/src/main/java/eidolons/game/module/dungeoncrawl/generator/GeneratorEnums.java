package eidolons.game.module.dungeoncrawl.generator;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GeneratorEnums {
    public  enum EXIT_TEMPLATE {
        THROUGH,
        ANGLE,
        CROSSROAD,
        FORK,
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

        SPECIAL_CONTAINER("c"),
        SPECIAL_DOOR("d"),
        SPECIAL_ART_OBJ("a"),

        TRAP("T"),
            /*
        false_wall,
        BUTTON,
        SHRINE,

         */;
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
            return getSymbol();
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum ROOM_TEMPLATE_GROUP {
        CRYPT,
        CAVERN,
        TUNNEL,
        CASTLE,
        TOWER,
        DUNGEON,
        RANDOM,
        RANDOM_INTERIOR,

    }
}
