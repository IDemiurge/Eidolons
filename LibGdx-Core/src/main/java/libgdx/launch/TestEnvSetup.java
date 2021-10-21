package libgdx.launch;

import eidolons.game.core.launch.TestLaunch;
import main.system.util.DialogMaster;

public class TestEnvSetup {

    public static final String[] testModules = {
            "test/test.xml"
    };
    public static final String[] testHeroes = {
            "Anphis val Keserim"
    };

    private static TestLaunch.TestValue[] launchValues={
            TestLaunch.TestValue.module, TestLaunch.TestValue.hero,
    };
    public static String pick(TestLaunch.TestValue value) {
        switch (value) {
            case module -> {
                int i = DialogMaster.optionChoice(testModules, "");
                return testModules[i];
            }
            case hero -> {
                int i = DialogMaster.optionChoice(testHeroes, "");
                return testHeroes[i];
            }
            case chain -> {
            }
            case encounter -> {
            }
        }
        return null;
    }
    public static TestLaunch initTestEnvLaunch(String arg) {
        TestLaunch launch = new TestLaunch();
        for (TestLaunch.TestValue value : launchValues) {
            String val = TestEnvSetup.pick(value);
            launch.setValue(value, val);
        }
        return launch;
    }
}
