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
        if (ai.isLeader()) {
            return updateLeaderTarget();
        } else {
            return ai.getGroupAI().getLeader();
        }
    }

    protected abstract DC_Obj updateLeaderTarget();


    @Override
    protected boolean checkNextActionCanBeMade(Action action) {
        if (action == null) {
            return false;
        }
        if (action.getActive().isMove()) {
            return  checkGroupIsKeepingUp();
        }

        return true;
    }

    protected boolean checkGroupIsKeepingUp() {
        if (ai.getGroupAI() == null)
            return false;

        boolean keepsUp = false;
        List<UnitAI> forwards = new ArrayList<>();
        for (Unit unit : group.getMembers()) {
            UnitAI ai = unit.getUnitAI();
            boolean done = unit.getCoordinates().dst(preferredPosition) <=getDistanceForNearby();
            if (!done) {
                done = isProgressObstructed(  ai );
            }
            if (done)
            // if (PositionMaster.getDistance(c,
            // m.getUnit().getCoordinates()) > max)
            {
                forwards.add(ai);
                if (group.getMembers().size() == 1 ||
                  forwards.size() > Math.round(group.getMembers().size()
                   * getCatchUpFactor())) {
                    keepsUp = true;
                    break;
                }
            }
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
