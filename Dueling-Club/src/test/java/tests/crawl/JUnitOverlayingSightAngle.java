package tests.crawl;

import eidolons.ability.conditions.special.ClearShotCondition;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static main.game.bf.directions.DIRECTION.*;

/**
 * Created by JustMe on 9/3/2018.
 */
public class JUnitOverlayingSightAngle {

    /*
     The best Unit Test ever written. Checks if a crack on a column is visible from your angle of view

     */
    @Test
    public void testOverlayingVisibility() {
        boolean result = false;
        DIRECTION d = RIGHT;
        Coordinates c = Coordinates.get(0, 0);
        Coordinates c1 = Coordinates.get(1, 0);
        result = ClearShotCondition.isOverlayingWithinSightAngle(c, d, c1);
        //    ###
        //    ##D
        //    ###U
        //== 45 == OK
        assertTrue(result);

        d = UP_LEFT;
        result = ClearShotCondition.isOverlayingWithinSightAngle(c, d, c1);
        assertTrue(!result);
        //    D##
        //    ###
        //    ###U
        //== 45 == NO

        //ok im too lazy...already took me a while to do this..

        DIRECTION[] wrong = new DIRECTION[]{
         DOWN_LEFT, LEFT, UP_LEFT
        };
        DIRECTION[] right = new DIRECTION[]{
         UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN,
        };
        for (DIRECTION direction : wrong) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must NOT...", !result);
        }
        for (DIRECTION direction : right) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must...", result);
        }

        c = Coordinates.get(0, 0);
        c1 = Coordinates.get(true, 0, -1); //above
        wrong = new DIRECTION[]{
         DOWN_LEFT, DOWN_RIGHT, DOWN,
        };
        right = new DIRECTION[]{
         UP, UP_RIGHT, RIGHT, LEFT, UP_LEFT
        };
        for (DIRECTION direction : wrong) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must NOT...", !result);
        }
        for (DIRECTION direction : right) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must...", result);
        }

        c = Coordinates.get(0, 0);
        c1 = Coordinates.get(true, 0, 1); //below
        wrong = new DIRECTION[]{
         UP, UP_RIGHT, UP_LEFT
        };
        right = new DIRECTION[]{
         RIGHT, LEFT, DOWN_LEFT, DOWN_RIGHT, DOWN,
        };
        for (DIRECTION direction : wrong) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must NOT...", !result);
        }
        for (DIRECTION direction : right) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must...", result);
        }

        c1 = Coordinates.get(true, -1, -1); //up-left
        wrong = new DIRECTION[]{
         RIGHT, DOWN_RIGHT, DOWN,
        };
        right = new DIRECTION[]{
         LEFT, DOWN_LEFT, UP, UP_RIGHT, UP_LEFT
        };
        for (DIRECTION direction : wrong) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must NOT...", !result);
        }
        for (DIRECTION direction : right) {
            result = ClearShotCondition.isOverlayingWithinSightAngle(c, direction, c1);
            assertTrue(direction + " must...", result);
        }


    }

}
