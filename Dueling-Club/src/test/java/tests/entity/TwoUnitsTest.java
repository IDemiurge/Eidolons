package tests.entity;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import org.junit.Before;

/**
 * Created by JustMe on 3/28/2017.
 */
public class TwoUnitsTest extends CreateUnitTest {
    protected Unit entity2;
    protected String typeName2 = "Thief";

    @Before
    public void createSecondEntity() {
        ObjType type = DataManager.getType(typeName2, DC_TYPE.UNITS);
        entity2 = (Unit) game.getManager().getObjCreator().createUnit(type, 0, 0, game.getPlayer(true), new Ref(game));

    }
}
