package tests.field;

import elements.content.enums.FieldConsts;
import framework.AphosTest;
import framework.field.FieldGeometry;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10/21/2023
 */
public class CellGeomTest extends AphosTest {

    @Override
    public void test() {
        super.test();
        assertTrue(FieldGeometry.get(FieldConsts.CellType.Front, true, true) == FieldConsts.Cell.Front_Player_3);
        assertTrue(FieldGeometry.get(FieldConsts.CellType.Back, true, false) == FieldConsts.Cell.Back_Player_1);
        assertTrue(FieldGeometry.get(FieldConsts.CellType.Flank, false, true) == FieldConsts.Cell.Top_Flank_Enemy);
        assertTrue(FieldGeometry.get(FieldConsts.CellType.Rear, true, null) == FieldConsts.Cell.Rear_Player);
        assertTrue(FieldGeometry.get(FieldConsts.CellType.Van, true, true) == FieldConsts.Cell.Vanguard_Top);
    }
}
