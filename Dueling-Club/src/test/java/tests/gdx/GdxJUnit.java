package tests.gdx;

import tests.FastDcTest;

/**
 * Created by JustMe on 2/12/2018.
 */
public class GdxJUnit extends FastDcTest {

    protected boolean isLoggingOff() {
        return false;
    }

    @Override
    protected boolean isGraphicsOff() {
        return false;
    }
}