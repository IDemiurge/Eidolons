package tests.basic;

import elements.content.enums.FieldConsts;
import framework.AphosTest;
import framework.entity.field.FieldEntity;
import framework.field.FieldPos;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
@Deprecated
public class EntityCreateTest extends AphosTest {
    private String name = "dummling";

    @Override
    public void test() {
        Map<String, Object> map =
                framework.data.DataManager.getEntityData(name);
        FieldPos pos = new FieldPos(FieldConsts.Cell.Reserve_ally);
        FieldEntity entity = new FieldEntity(map, pos);

        assertTrue(entity.getName().equals(name));
    }
}
