package tests.calc;

import elements.content.enums.types.CombatTypes;
import framework.AphosTest;
import logic.calculation.GradeCalc;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 */
@Deprecated
public class GradeCalcTest extends AphosTest {

    @Override
    public void test() {
        Map<String, Integer> gradesMap = new HashMap();
        for (int j = 0; j < 1000; j++) {
            CombatTypes.RollGrade grade = GradeCalc.calculateGrade(4, 6, 8, 0);
            MapMaster.addToIntegerMap(gradesMap, grade.getName(), 1);
        }
        System.out.println(MapMaster.getNetStringForMap(gradesMap).replace(";", "\n"));
    }
}
