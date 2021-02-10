package eidolons.game.netherflame.main;

import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioInitializer;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;

public class NF_MetaInitializer extends ScenarioInitializer {
    public NF_MetaInitializer(NF_MetaMaster master) {
        super(master);
    }

    @Override
    protected ScenarioMeta createMeta(Scenario scenario) {
        return new NF_Meta(scenario, getMaster());
    }
}
