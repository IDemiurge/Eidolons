package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.secondary.GeometryMaster;

import java.util.Arrays;
import java.util.Collections;
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

    public static Function<Coordinates, Integer> getSorterForShape(
     FILL_SHAPE shape, Coordinates seed, int arg) {
        //
//        shape.getMaxDistance(arg);
        //fullList.stream().
        switch (shape) {

            case TRIANGLE:
                return c -> {
                    if (seed.dst_(c) == new AbstractCoordinates(0, 0).dst_(new AbstractCoordinates(1, 2))) {
                        return 100 - c.x - c.y;
                    }
                    return -c.x - c.y;
                };

            case SQUARE:
                return c -> {
                    if (seed.dst_(c) <= GeometryMaster.hyp(arg+1, arg+1))
                        return 100;
                    return 0;
                };
            case TRIPLE:
                return c -> {

                    return 0;
                };
            case PAIR_VERTICAL:
                return c -> {

                    return 0;
                };
            case PAIR:
                return c -> {

                    return 0;
                };
            case DIAGONAL_PAIR:
                return c -> {

                    return 0;
                };
            case CROSS:
                return c -> {

                    return 0;
                };
            case X_CROSS:
                return c -> {

                    return 0;
                };
            case CROSS_HOLLOW:
                return c -> {

                    return 0;
                };
            case X_CROSS_HOLLOW:
                return c -> {

                    return 0;
                };
        }
        return null;
    }

    public static boolean checkShape(FILL_SHAPE shape, List<Coordinates> coords, int arg) {
        Coordinates topLeft = CoordinatesMaster.getFarmostCoordinateInDirection(DIRECTION.UP_LEFT, coords);
        Collections.sort(coords, new SortMaster<Coordinates>()
         .getSorterByExpression_(c ->
          -c.x * 100 - c.y * 101)); //x difference goes first
        coords = coords.stream().map(c -> c.getOffset(topLeft.negative())).collect(Collectors.toList());
//relative to 0-0 ALWAYS!


        //try all rotations and flips
//        Boolean[] rotations = {true};
//        coords = RotationMaster.rotateCoordinates(rotations, coords);

        switch (shape) {
            case TRIANGLE:
                if (coords.get(1).x != -1)
                    return false;
                if (coords.get(1).y != 2)
                    return false;
                if (coords.get(2).x != 1)
                    return false;
                if (coords.get(2).y != 2)
                    return false;

                return true;
            case PAIR:
                if (coords.get(1).x != 1 + arg)
                    return false;
                if (coords.get(1).y != 0)
                    return false;

                return true;
            case PAIR_VERTICAL:
                if (coords.get(1).x != 0)
                    return false;
                if (coords.get(1).y != 1 + arg)
                    return false;

                return true;
            case TRIPLE_VERTICA:
                if (coords.get(1).x != 1)
                    return false;
                if (coords.get(1).y != 0)
                    return false;
                if (coords.get(2).x != 2)
                    return false;
                if (coords.get(2).y != 0)
                    return false;

                return true;
            case TRIPLE:
                if (coords.get(1).x != 1)
                    return false;
                if (coords.get(1).y != 0)
                    return false;
                if (coords.get(2).x != 2)
                    return false;
                if (coords.get(2).y != 0)
                    return false;

                return true;
            case CORNER:
                //check for 'normal case', then apply rotations and see if any matches

                if (coords.get(1).x != 1)
                    return false;
                if (coords.get(1).y != 0)
                    return false;
                if (coords.get(2).x != 0)
                    return false;
                if (coords.get(2).y != 1)
                    return false;

                return true;
        }
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
        X_CROSS_HOLLOW(4), TRIPLE_VERTICA;
    /*
    XOX
    O0O
    XOX
     */

        private int minPoints;

        FILL_SHAPE() {
            this(2);
        }

        FILL_SHAPE(int minPoints) {
            this.minPoints = minPoints;
        }

        public int getMinPoints() {
            return minPoints;
        }

    }

}
