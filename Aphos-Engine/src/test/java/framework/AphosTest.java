package framework;

import framework.data.yaml.YamlBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
public class AphosTest {
    private static boolean testInitDone;

    @Test
    public void test() {
        if (!testInitDone) {
            init();
        }
    }

    protected void init() {
        Core.init();
        new YamlBuilder().buildYamlFiles();
        testInitDone = true;
    }

    ;

    public void check(boolean bool) {
        assertTrue(bool);
    }
}
