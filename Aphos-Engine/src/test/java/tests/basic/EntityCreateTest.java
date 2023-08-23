package tests.basic;

import framework.AphosTest;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.field.FieldPos;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
public class EntityCreateTest extends AphosTest {
    private String name = "dummling";

    @Override
    public void test() {
        Map<String, Object> map =
                framework.data.DataManager.getEntityData(name);
        FieldPos pos = new FieldPos(FieldPos.CellType.Reserve_ally);
        FieldEntity entity = new FieldEntity(map, pos);

        assertTrue(entity.getName().equals(name));
    }
}
