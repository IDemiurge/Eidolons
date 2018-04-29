package tests.gdx;

import tests.DcTest;

/**
 * Created by JustMe on 2/12/2018.
 */
public class GdxJUnit extends DcTest {

    protected boolean isLoggingOff() {
        return false;
    }

    @Override
    protected boolean isGraphicsOff() {
        return false;
    }
}
