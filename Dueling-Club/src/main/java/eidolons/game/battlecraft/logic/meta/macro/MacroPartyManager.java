package eidolons.game.battlecraft.logic.meta.macro;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MacroPartyManager extends ScenarioPartyManager {
    public MacroPartyManager(MetaGameMaster master) {
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
