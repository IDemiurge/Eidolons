package tests.gdx;

import tests.JUnitDcTest;

/**
 * Created by JustMe on 2/12/2018.
 */
public class GdxJUnit extends JUnitDcTest {

    protected boolean isLoggingOff() {
        return false;
    }

    @Override
    protected boolean isGraphicsOff() {
        return false;
    }
}
