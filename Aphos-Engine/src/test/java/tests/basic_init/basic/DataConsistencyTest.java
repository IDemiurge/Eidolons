package tests.basic_init.basic;

import elements.exec.ExecBuilder;
import elements.exec.Executable;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.data.DataManager;
import framework.entity.field.Unit;
import system.ListMaster;
import system.log.SysLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/27/2023
 */
public class DataConsistencyTest extends tests.basic_init.basic.BattleInitTest {
    @Override
    public void test() {
        super.test();

        for (Map typeData : DataManager.getUnitMap().values()) {
            // Map<String, Object> map = DataManager.getUnitMap().get(key);
            testType(typeData);
        }
        for (Map<String, Object> actionMap : DataManager.getActionMap().values()) {
            SysLog.printOut(actionMap.get("Name"));
            Object o = actionMap.get("exec data");
            // SysLog.printOut(actionMap.get(o));
            //test executable?
            Executable exec = ExecBuilder.getExecutable(o.toString());
            SysLog.printOut(exec.toString() + " --------  ok");
        }
    }

    private void testType(Map typeData) {
        Unit unit = new Unit(typeData, 1);
        unit.getActionSet();
        List<Object> invalid = new ArrayList<>();
        for (UnitParam value : UnitParam.values()) {
            if (!valueCanBeNull(unit, value)) {
                try {
                    unit.getInt(value);
                } catch (Exception e) {
                    // main.system.ExceptionMaster.printStackTrace(e);
                    invalid.add(value);
                }
            }
        }
        assertTrue(unit.getName() + " has missing or invalid values: \n" + ListMaster.represent(invalid),
                invalid.isEmpty());
        SysLog.printOut(unit.getData());
    }

    private boolean valueCanBeNull(Unit unit, UnitParam value) {
        if (value.isCur() || value.isBonus()) {
            return true;
        }
        if (value == UnitParam.Initiative)
            return true;
        if (value == UnitParam.Essence || value == UnitParam.Essence_Max || value == UnitParam.Power) {
            return !unit.isTrue(UnitProp.Daemon);
        }
        if (value == UnitParam.Sanity || value == UnitParam.Sanity_Max) {
            return unit.isTrue(UnitProp.Pure);
        }
        if (value == UnitParam.Faith || value == UnitParam.Faith_Max) {
            return !unit.isTrue(UnitProp.Pure);
        }
        // unit.get(UnitProp.Faction)
        return false;
    }

}
