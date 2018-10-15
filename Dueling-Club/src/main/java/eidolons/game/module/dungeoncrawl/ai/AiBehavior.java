package eidolons.game.module.dungeoncrawl.ai;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

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

    public void act(float delta) {
    }
}
