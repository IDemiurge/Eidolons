package tests.entity;

import eidolons.entity.obj.unit.Unit;
import org.junit.Before;
import res.JUnitResources;
import tests.JUnitDcTest;

/**
 * Created by JustMe on 4/9/2018.
 */
public class JUnitSingleUnit extends JUnitDcTest {

    protected Unit unit;

    @Override
    protected String getPlayerParty() {
        return JUnitResources.DEFAULT_UNIT;
    }
    @Before
    @Override
    public void init(){
        super.init();
        unit = game.getManager().getMainHero();
    }
}
