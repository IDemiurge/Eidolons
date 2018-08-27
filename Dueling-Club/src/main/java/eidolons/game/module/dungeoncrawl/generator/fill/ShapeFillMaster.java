package eidolons.game.module.dungeoncrawl.generator.fill;

import main.game.bf.Coordinates;
import main.system.SortMaster;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/26/2018.
 */
public class ShapeFillMaster {


    public static FILL_SHAPE getRandomShape(int limit) {
        return Arrays.stream(FILL_SHAPE.values()).filter(s ->
         s.getMinPoints() <= limit).findAny().get();
    }

    public static List<Coordinates> getCoordinatesForShape(
     FILL_SHAPE shape, Coordinates seed, int arg, List<Coordinates> fullList, int limit) {
        return fullList.stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
         getSorterForShape(shape, seed, arg)))
         .collect(Collectors.toList()).subList(0, limit);
    }

    public static Function<Coordinates,Integer> getSorterForShape(
     FILL_SHAPE shape, Coordinates seed, int arg ) {

        //fullList.stream().
        switch (shape) {

            case TRIANGLE:
                return c-> {

                    return 0;
                } ;

            case SQUARE:
                return c-> {

                    return 0;
                } ;
            case TRIPLE:
                return c-> {

                    return 0;
                } ;
            case PAIR_VERTICAL:
                return c-> {

                    return 0;
                } ;
            case PAIR:
                return c-> {

                    return 0;
                } ;
            case DIAGONAL_PAIR:
                return c-> {

                    return 0;
                } ;
            case CROSS:
                return c-> {

                    return 0;
                } ;
            case X_CROSS:
                return c-> {

                    return 0;
                } ;
            case CROSS_HOLLOW:
                return c-> {

                    return 0;
                } ;
            case X_CROSS_HOLLOW:
                return c-> {

                    return 0;
                } ;
        }
        return null;
    }

    public static boolean checkShape(FILL_SHAPE shape, List<Coordinates> coords) {
        return false;
    }


    public enum FILL_SHAPE {
        TRIANGLE(3),
        /*
        OxO
        OOO
        xOx
         */
        SQUARE(4),
        /*
        OOO
        XXO
        XXO
         */
        CORNER(3),
        /*
        OOO
        OOX
        0XX
         */
        TRIPLE(3),
        /*
        OOO
        OOO
        XXX
         */
        PAIR_VERTICAL,
        /*
        OXO
        OOO
        OXO
         */
        PAIR,
        /*
        O0O
        XOX
        O0O
         */
        DIAGONAL_PAIR,
        /*
        O0X
        OOO
        X0O
         */
        CROSS(5),
        /*
        OXO
        XXX
        OXO
         */
        X_CROSS(5),
        /*
        XOX
        OXO
        XOX
         */
        CROSS_HOLLOW(4),
        /*
        OXO
        X0X
        OXO
         */
        X_CROSS_HOLLOW(4),;
    /*
    XOX
    O0O
    XOX
     */

        FILL_SHAPE() {
            this(2);
        }

        FILL_SHAPE(int minPoints) {
            this.minPoints = minPoints;
        }

        private int minPoints;

        public int getMinPoints() {
            return minPoints;
        }

    }

}
