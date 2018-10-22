package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;

import java.util.List;

/**
 * Created by JustMe on 9/23/2017.
 *
 * Nevers loses sight of the X that it's guarding! or maybe with y%
 *
 * idle => sleep ...
 *
 * create a limited pool of positions and go thru them...
 *
 *
 */
public class GuardAi extends AiBehavior {

    public GuardAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

    @Override
    public ActionSequence getOrders( ) {
        if (isEnabled(ai))
            return null;
        //check mode

        Coordinates target = CoordinatesMaster.getRandomAdjacentCoordinate(ai.getUnit().getCoordinates());
        List<Action> list = getMaster(ai).getTurnSequenceConstructor().
         getTurnSequence(FACING_SINGLE.IN_FRONT, ai.getUnit(), target);
        return new ActionSequence(GOAL_TYPE.PROTECT, list.toArray(new Action[list.size()]));
    }

    @Override
    protected float getTimeBeforeFail() {
        return 0;
    }

    protected boolean isEnabled(UnitAI ai) {
        return true;
    }

    @Override
    protected void initOrders() {

    }
}
