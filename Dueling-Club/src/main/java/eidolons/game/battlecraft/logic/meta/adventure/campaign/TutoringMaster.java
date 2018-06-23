package eidolons.game.battlecraft.logic.meta.adventure.campaign;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.PointMaster;
import main.content.values.parameters.PARAMETER;

import java.util.List;

/**
 * Created by JustMe on 7/23/2017.
 * <p>
 * at camping stage,
 */
public class TutoringMaster {


    public void checkTutorRequests(Unit hero) {
        List<PARAMETER> potential;

    }

    public void tutor(Unit tutor, Unit learner, PARAMETER mastery, int arg) {
        int amount = arg;
        learner.modifyParameter(mastery, amount);
        int cost = PointMaster.getCost(learner.getIntParam(mastery), amount, learner, mastery);
        learner.modifyParameter(PARAMS.MASTERY_POINTS,
         -cost);
//        relationshipImpact;
        //favor
//        impact(tutor, learner, r);
    }
}
