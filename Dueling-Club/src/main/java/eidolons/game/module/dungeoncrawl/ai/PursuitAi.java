package eidolons.game.module.dungeoncrawl.ai;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 9/23/2017.
 */
public class PursuitAi extends AiBehavior {

    @Override
    public ActionSequence getOrders(UnitAI ai) {
        new ActionSequence(GOAL_TYPE.MOVE,
         getMaster(ai).getAtomicAi().getAtomicMove(getLastKnownEnemyLocation(ai), ai.getUnit()));

        return null;
    }

    private Coordinates getLastKnownEnemyLocation(UnitAI ai) {

        return null;
    }

    @Override
    protected boolean isEnabled(UnitAI ai) {
        if (getLastKnownEnemyLocation(ai) == null) return false;
        return super.isEnabled(ai);
    }
}
