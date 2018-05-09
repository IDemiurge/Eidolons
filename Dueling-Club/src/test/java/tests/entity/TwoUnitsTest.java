package tests.entity;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import res.JUnitResources;
import tests.DcTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/28/2017.
 */
public class TwoUnitsTest extends DcTest {
    protected   Unit unit;
    protected   Unit unit2;

    @Override
    protected String getEnemyParty() {
        return "";
    }

    @Override
    protected String getPlayerParty() {
        return "";
    }

    @Override
    public void init() {
        super.init();
        unit= helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, true);
        Eidolons.setMainHero(unit);
        unit2= helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, false);

        assertTrue(unit != null);
        assertTrue(unit2 != null);

    }
}
