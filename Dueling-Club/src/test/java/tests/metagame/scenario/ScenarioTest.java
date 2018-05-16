package tests.metagame.scenario;

import tests.DcTest;

/**
 * Created by JustMe on 5/16/2017.
 */
public class ScenarioTest extends DcTest {

    @Override
    protected boolean isScenario() {
        return true;
    }

    @Override
    protected boolean isSelectiveXml() {
        return false;
    }

    @Override
    protected boolean isLoggingOff() {
        return false;
    }

    @Override
    protected boolean isGraphicsOff() {
        return false;
    }

    @Override
    protected Integer getScenarioIndex() {
        return 0;
    }

    @Override
    protected Integer getHeroIndex() {
        return 0;
    }
}
