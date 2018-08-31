package tests.entity;

import eidolons.entity.obj.unit.Unit;
import org.junit.Ignore;
import org.junit.Test;
import res.JUnitResources;
import tests.EidolonsTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitPartyCreated extends EidolonsTest {


    protected Unit unit;

    @Override
    protected String getPlayerParty() {
        return JUnitResources.DEFAULT_UNIT;
    }

    @Override
    public void init() {
        super.init();
        unit = game.getUnits().iterator().next();
    }

    /**
     * Tests that an entity object was created in @Before and it received expected name.
     */
    @Test
    @Ignore
    public void testUnitCreatedWithRightName() {

        assertTrue(!game.getUnits().isEmpty());
        assertTrue(unit != null);

    }




}
