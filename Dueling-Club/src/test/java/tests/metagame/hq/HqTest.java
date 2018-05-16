package tests.metagame.hq;

import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import tests.DcTest;

/**
 * Created by JustMe on 5/16/2018.
 */
public class HqTest extends DcTest{

    @Override
    public void init() {
        super.init();
        //enter hq?

        HqMaster.openHqPanel();
    }
}
