package tests.entity;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import org.junit.Before;
import org.junit.Test;
import res.JUnitResources;
import tests.FastDcTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitPartyCreated extends FastDcTest {


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


    /**
     * Tests that a modified parameter will return to its basic value upon calling entity.toBase()
     */
    @Test
    public void testToBase() {

        unit.setParam(PARAMS.ACID_ARMOR, unit.getType().getParam(PARAMS.ACID_ARMOR) + 5);
        unit.toBase();
        assertTrue(unit.getParam(PARAMS.ACID_ARMOR) == unit.getType().getParam(PARAMS.ACID_ARMOR));

    }


}
