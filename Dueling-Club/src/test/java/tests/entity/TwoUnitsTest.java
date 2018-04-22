package tests.entity;

import eidolons.entity.obj.unit.Unit;
import org.junit.Test;
import res.JUnitResources;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/28/2017.
 */
public class TwoUnitsTest extends JUnitPartyCreated {
  protected   Unit unit2;

    @Override
    protected String getEnemyParty() {
        return JUnitResources.DEFAULT_UNIT;
    }
    @Test
    public void testUnitCreatedWithRightName() {
        assertTrue(!game.getUnits().isEmpty());
        assertTrue(unit != null);
        unit2 = (Unit) game.getPlayer(false).getControlledUnits().iterator().next();
        assertTrue(unit2 != null);

    }
}
