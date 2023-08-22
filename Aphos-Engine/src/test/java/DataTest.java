import framework.entity.Entity;
import framework.entity.EntityData;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */

public class DataTest {

    @Test
    public void blast(){
        EntityData entityData = new EntityData("ghast=true;dummy=false;hp=5");
        assertTrue((Boolean) entityData. get("ghast"));
        assertTrue(!(Boolean) entityData. get("dummy"));
        assertTrue((Integer) entityData. get("hp")==5);

    }
}
