package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/25/2018.
 */
public abstract class AiGroupBehavior extends AiBehavior {
    public AiGroupBehavior(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected DC_Obj updateTarget() {
        if (ai.getGroupAI() == null) {
            return super.updateTarget();
        }
        if (isLeader()) {
            return updateLeaderTarget();
        } else {
            return ai.getGroupAI().getLeader();
//            return getCell(ai.getGroupAI().getLeader().getCoordinates());
        }
    }

    public boolean isLeader() {
        return ai.isLeader();
    }

    protected abstract DC_Obj updateLeaderTarget();


    @Override
    protected boolean checkNextActionCanBeMade(Action action) {
        if (action == null) {
            return false;
        }
        if (isLeader())
            if (action.getActive().isMove()) {
                return checkGroupIsKeepingUp();
            }

        return true;
    }

    protected boolean checkGroupIsKeepingUp() {
        if (ai.getGroupAI() == null)
            return true;

        boolean keepsUp = false;
        List<UnitAI> forwards = new ArrayList<>();
        for (Unit unit : group.getMembers()) {
            if (unit == getUnit()) {
                continue;
            }
            UnitAI ai = unit.getUnitAI();
            boolean closeEnough = unit.getCoordinates().dst(getCoordinates()) <= getDistanceForNearby();
            //            if (!closeEnough) {
            //                closeEnough = isProgressObstructed(  ai );
            //            }
            if (closeEnough) {
                forwards.add(ai);
            }
        }
        if (group.getMembers().size() == 1 ||
         forwards.size() >= Math.round((group.getMembers().size() - 1)
          * getCatchUpFactor())) {
            keepsUp = true;
            status=BEHAVIOR_STATUS.RUNNING;
        } else {
           status=BEHAVIOR_STATUS.WAITING;
        }
        return keepsUp;
    }

    protected boolean isProgressObstructed(UnitAI ai) {
        return false;
    }

    protected float getCatchUpFactor() {
        return 0.5f;
    }

}
