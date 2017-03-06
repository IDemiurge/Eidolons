package tests.entity;

import init.JUnitDcInitializer;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by JustMe on 3/6/2017.
 */
public class CreateUnitTest{

    private static final String DEFAULT_TEST_UNIT = "Pirate";
    private   String typeName = "Pirate";
    private JUnitDcInitializer judi;
    private Unit entity;



    @Before
    public void createEntity() {
        judi = new JUnitDcInitializer();
        ObjType type= getTestType();
        entity = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0, judi.game.getPlayer(true), new Ref(judi.game));

    }

    private ObjType getTestType() {
        return DataManager.getType(typeName, DC_TYPE.UNITS);
    }

    @Test
    public void testUnitTest() {

        assert (entity!=null );
        assert (entity.getName().equals(typeName));



    }
}
