package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 10/12/2018.
 * <p>
 * suppose each UnitAi had a number of behaviors at all times...
 * perhaps even competing!
 * <p>
 * each behavior has a waiting float and a way to convert it into actions
 * <p>
 * getNewOrders
 * checkOrdersValid
 * arrived
 * <p>
 * are they all about moving?
 * perhaps I could just rob GdxAi of some good concepts
 */
public class FollowAi extends AiBehavior {
    float lastDistanceFromLeader;

    public FollowAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

//    protected Action getFollowMove(Unit unit) {
//        Action move = master.getGame().getAiManager().getAtomicAi().getAtomicMove(lastPosition, unit);
//
//        if (move == null)
//            return null;
//        if (!checkMove(move, unit))
//            return null;
//        return move;
//    }

    private boolean checkMove(Action move, Unit sub) {
        if (!move.canBeActivated()) {
            return false;
        }
        return move.canBeTargeted();
    }



    @Override
    protected float getTimeBeforeFail() {
        return 20;
    }
}
