package main.game.module.dungeoncrawl.ai;

import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 9/23/2017.
 */
public abstract class AiBehavior {

    protected boolean isEnabled(UnitAI ai) {
        return true;
    }
    protected AiMaster getMaster(UnitAI ai) {
        return ai.getUnit().getGame().getAiManager();
    }
     public abstract ActionSequence getOrders(UnitAI ai);
}
