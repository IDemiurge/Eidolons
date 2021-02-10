package eidolons.game.netherflame.main;

import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * this is the thing to be constructed from save data?
 *
 */
public class NF_Meta extends ScenarioMeta {


    public NF_Meta(Scenario scenario, MetaGameMaster master) {
        super(scenario, master);
    }

    @Override
    public NF_MetaMaster getMaster() {
        return (NF_MetaMaster) super.getMaster();
    }


}
