package tests.entity;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import org.junit.Before;
import org.junit.Test;
import tests.GenericTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class CreateUnitTest extends GenericTest {


    protected String typeName = "Pirate";
    protected Unit entity;


    /**
     * Creates a unit with standard name.
     */
    @Before
    public void createEntity() {
        ObjType type= DataManager.getType(typeName, DC_TYPE.UNITS);
        entity = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0,
                judi.game.getPlayer(true), new Ref(judi.game));

    }

    /**
     * Tests that an entity object was created in @Before and it received expected name.
     */
    @Test
    public void testUnitCreatedWithRightName() {

        assertTrue (entity!=null );
        assertTrue (entity.getName().equals(typeName));

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
