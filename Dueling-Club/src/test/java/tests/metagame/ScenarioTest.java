package tests.metagame;

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
    protected Integer getScenarioIndex() {
        return 0;
    }

    @Override
    protected Integer getHeroIndex() {
        return 0;
    }
}
