package tests.entity;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import org.junit.Before;
import org.junit.Test;
import tests.FastDcTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class CreateUnitTest extends FastDcTest {


    protected Unit entity;


    /**
     * Creates a unit with standard name.
     */
    @Before
    public void createEntity() {
        entity = game.getUnits().get(0);
    }

    /**
     * Tests that an entity object was created in @Before and it received expected name.
     */
    @Test
    public void testUnitCreatedWithRightName() {

        assertTrue(!game.getUnits().isEmpty());
        assertTrue (entity!=null );

    }


    /**
     * Tests that a modified parameter will return to its basic value upon calling entity.toBase()
     */
    @Test
    public void testToBase() {

        entity.setParam(PARAMS.ACID_ARMOR, entity.getType().getParam(PARAMS.ACID_ARMOR)+5);
        entity.toBase();
        assertTrue (entity.getParam(PARAMS.ACID_ARMOR) == entity.getType().getParam(PARAMS.ACID_ARMOR));

    }


}
