package tests.entity;

import eidolons.content.PARAMS;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by JustMe on 4/22/2018.
 */
public class JUnitToBase extends JUnitPartyCreated {

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
