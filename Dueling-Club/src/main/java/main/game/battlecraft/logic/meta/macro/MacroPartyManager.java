package main.game.battlecraft.logic.meta.macro;

import main.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;

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
