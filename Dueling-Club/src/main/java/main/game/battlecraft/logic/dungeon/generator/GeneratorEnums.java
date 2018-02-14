package main.game.battlecraft.logic.dungeon.generator;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GeneratorEnums {
    public enum ROOM_CELL {
        WALL ( "#"),
        FLOOR ( "O"),
        CONTAINER ( "C"),
        DOOR ( "D"),
        ART_OBJ ( "A"),,
        DESTRUCTIBLE_WALL ( "@"),
        SECRET_DOOR ( "S"),
        TRAP ( "T"),
        GUARD ( "G"),
        
        LIGHT_EMITTER( "L"),
        WALL_WITH_LIGHT_OVERLAY( "@"),
        WALL_WITH_DECOR_OVERLAY( "$"),
        LOCAL_KEY( "k"),
        GLOBAL_KEY( "K"),
        DESTRUCTIBLE( "X"),

        SPECIAL_CONTAINER ( "c"),
        SPECIAL_DOOR ( "d"),
        SPECIAL_ART_OBJ ( "a"),
            /*
        false_wall,
        BUTTON,
        SHRINE,

         */
        ;
        public final String symbol;

        public String getSymbol() {
            return symbol;
        }

        ROOM_CELL(String symbol) {
            this.symbol=symbol;
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

    public static enum EXIT_TEMPLATE {
        THROUGH,
        ANGLE,
        CROSSROAD,
        FORK,
        CUL_DE_SAC,
    }
}
