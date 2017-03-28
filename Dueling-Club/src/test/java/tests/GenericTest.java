package tests;

import init.JUnitDcInitializer;
import main.game.core.game.DC_Game;
import org.junit.Before;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/27/2017.
 */
public class GenericTest {

    protected JUnitDcInitializer judi;
    protected DC_Game game;


    @Before
    public void init() {
        judi = new JUnitDcInitializer();
        game = judi.game;
    }


    public void assertEqualAndLog(int v1, int v2, String comment1, String comment2) {
        System.out.println(
         "Assert: " + comment1 + " = " + v1 + " equal to " + comment2 + " = " + v2);
        boolean result = v1 == v2;
        assertTrue(result);
    }

    public void assertAndLog(
//         Boolean greater_less_equal,
     int greater, int than, String comment) {
        System.out.println(comment +
         "; Assert: " + greater + " greater than " + than);
        boolean result = greater > than;
        assertTrue(result);
    }
}
