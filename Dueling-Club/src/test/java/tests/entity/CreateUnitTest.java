package tests.entity;

import init.JUnitDcInitializer;
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


    protected JUnitDcInitializer judi;
    protected String typeName = "Pirate";
    protected Unit entity;



    @Before
    public void createEntity() {
        judi = new JUnitDcInitializer();
        ObjType type= DataManager.getType(typeName, DC_TYPE.UNITS);
        entity = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0, judi.game.getPlayer(true), new Ref(judi.game));

    }





    @Test
    public void testToBase() {

        assertTrue (entity!=null );
        assertTrue (entity.getName().equals(typeName));
        entity.setParam(PARAMS.ACID_ARMOR, entity.getType().getParam(PARAMS.ACID_ARMOR)+5);
        entity.toBase();
        assertTrue (entity.getParam(PARAMS.ACID_ARMOR) == entity.getType().getParam(PARAMS.ACID_ARMOR));

    }


}
