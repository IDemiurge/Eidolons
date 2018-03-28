package main.game.module.dungeoncrawl.ai;

import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.bf.Coordinates;

import java.util.List;

/**
 * Created by JustMe on 9/23/2017.
 */
public class GuardAi extends AiBehavior {

    @Override
    public ActionSequence getOrders(UnitAI ai) {
        if (isEnabled(ai))
            return null;
        //check mode

        Coordinates target = CoordinatesMaster.getRandomAdjacentCoordinate(ai.getUnit().getCoordinates());
        List<Action> list = getMaster(ai).getTurnSequenceConstructor().
         getTurnSequence(FACING_SINGLE.IN_FRONT, ai.getUnit(), target);
        return new ActionSequence(GOAL_TYPE.PROTECT, list.toArray(new Action[list.size()]));
    }

    protected boolean isEnabled(UnitAI ai) {
        return true;
    }
}
