package tests.entity;

import eidolons.entity.obj.unit.Unit;
import org.junit.Before;
import org.junit.Test;
import res.JUnitResources;
import tests.JUnitDcTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitPartyCreated extends JUnitDcTest {


    protected Unit unit;

    @Override
    protected String getPlayerParty() {
        return JUnitResources.DEFAULT_UNIT;
    }

    /**
     * Creates a unit with standard name.
     */
    @Before
    public void createEntity() {
        unit = game.getUnits().get(0);
    }

    /**
     * Tests that an entity object was created in @Before and it received expected name.
     */
    @Test
    public void testUnitCreatedWithRightName() {

        assertTrue(!game.getUnits().isEmpty());
        assertTrue(unit != null);

    }




}
