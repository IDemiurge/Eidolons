package eidolons.game.battlecraft.logic.meta.macro;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;

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
}
