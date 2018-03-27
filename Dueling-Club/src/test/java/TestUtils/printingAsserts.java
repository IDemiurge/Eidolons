package TestUtils;

import static org.junit.Assert.assertTrue;

/**
 * Created by Nyx on 5/1/2017.
 */
public class printingAsserts {

    public static void assertEqualAndLog(int v1, int v2, String comment1, String comment2) {
        System.out.println(
         "Assert: " + comment1 + " = " + v1 + " equal to " + comment2 + " = " + v2);
        boolean result = v1 == v2;
        assertTrue(result);
    }

    public static void assertGreaterThanAndLog(
     int greater, int than, String comment) {
        System.out.println(comment +
         "; Assert: " + greater + " greater than " + than);
        boolean result = greater > than;
        assertTrue(result);
    }
}
