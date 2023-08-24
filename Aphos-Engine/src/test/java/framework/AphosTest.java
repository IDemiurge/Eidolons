package framework;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
public abstract class AphosTest {
    @Test
    public abstract void test();

    public void check(boolean bool){
        assertTrue(bool);
    }
}
