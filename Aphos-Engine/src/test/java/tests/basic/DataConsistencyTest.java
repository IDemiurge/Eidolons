package tests.basic;

import elements.exec.ExecBuilder;
import elements.exec.Executable;
import framework.AphosTest;
import framework.data.DataManager;
import system.log.SysLog;

import java.util.Map;

/**
 * Created by Alexander on 8/27/2023
 */
public class DataConsistencyTest extends AphosTest {
    @Override
    public void test() {
        super.test();
        for (Map<String, Object> actionMap : DataManager.getActionMap().values()) {
            SysLog.printOut(actionMap.get("Name"));
            Object o = actionMap.get("exec data");
            // SysLog.printOut(actionMap.get(o));
            //test executable?
            Executable exec = ExecBuilder.getExecutable(o.toString());
            SysLog.printOut(exec.toString()+" --------  ok");
        }
    }
}
