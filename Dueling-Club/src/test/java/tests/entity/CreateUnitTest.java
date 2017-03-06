package java.tests.entity;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;

import java.init.JUnitDcInitializer;

/**
 * Created by JustMe on 3/6/2017.
 */
public class CreateUnitTest extends CreateEntityTest<Unit> {

    private static final String DEFAULT_TEST_UNIT = "Pirate";
    private   String typeName;

    public CreateUnitTest(JUnitDcInitializer initializer) {
        this(initializer, DEFAULT_TEST_UNIT);
    }
    public CreateUnitTest(JUnitDcInitializer initializer, String typeName) {
        super(initializer);
        this.typeName=typeName;
    }

    @Override
    protected Unit createEntity() {
        ObjType type= getTestType();
        game.getManager().getObjCreator().createUnit(type, 0, 0, game.getPlayer(true), new Ref(game));
        return null;
    }

    private ObjType getTestType() {
        return DataManager.getType(typeName, DC_TYPE.UNITS);
    }
}
