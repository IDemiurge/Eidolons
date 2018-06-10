package tests.gdx;

import eidolons.libgdx.screens.ScreenWithVideoLoader;
import tests.EidolonsTest;

/**
 * Created by JustMe on 4/29/2018.
 */
public class LibgdxTest extends EidolonsTest {

    @Override
    protected String getPlayerParty() {
        return "Thief";
    }

    @Override
    protected boolean isGraphicsOff() {
        return false;
    }

    @Override
    protected boolean isOldLauncher() {
        return true;
    }

    @Override
    protected String getXmlTypesToRead() {
        return super.getXmlTypesToRead();
    }

    @Override
    protected boolean isSelectiveXml() {
        return false;
    }

    @Override
    public void init() {
        ScreenWithVideoLoader.setVideoEnabled(false);
        super.init();
    }
}
