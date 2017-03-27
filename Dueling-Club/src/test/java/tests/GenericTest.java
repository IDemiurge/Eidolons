package tests;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/27/2017.
 */
public class GenericTest {

    public void assertAndLog(int greater, int than){
        main.system.auxiliary.log.LogMaster.log(1,
         "Assert: "+greater+ " greater than " +than);
        assertTrue(greater > than);
    }
}
