package eidolons.game.battlecraft.logic.meta.adventure;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * Created by JustMe on 2/7/2018.
 *
 * Manage save/load?
 */
public class AdventurePartyManager extends ScenarioPartyManager {
    public AdventurePartyManager(MetaGameMaster master) {
        super(master);
    }

    protected boolean isWaitForGdx() {
        return false;
    }

    @Override
    protected Unit findMainHero() {
        return party.getLeader();
    }
}
