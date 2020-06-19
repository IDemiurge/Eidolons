package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.List;

public class PriorityManagerSimplified extends PriorityManagerImpl {
    public PriorityManagerSimplified(AiMaster master) {
        super(master);
    }

    @Override
    public int getStealthPriority(Action action) {
        return super.getStealthPriority(action);
    }

    @Override
    public int getRetreatPriority(ActionSequence as) {
        return super.getRetreatPriority(as);
    }

    @Override
    public float getCowardiceFactor(Unit coward) {
        return super.getCowardiceFactor(coward);
    }


    @Override
    public int getSearchPriority(ActionSequence as) {
        return super.getSearchPriority(as);
    }

    @Override
    public int getCoatingPriority(DC_ActiveObj active, DC_Obj targetObj) {
        return super.getCoatingPriority(active, targetObj);
    }

    @Override
    public int getItemPriority(DC_Obj targetObj) {
        return super.getItemPriority(targetObj);
    }

    @Override
    public int getAttackOfOpportunityPenalty(DC_ActiveObj action, Unit targetObj) {
        return super.getAttackOfOpportunityPenalty(action, targetObj);
    }

    @Override
    public int getCounterPenalty(DC_ActiveObj active, Unit targetObj) {
        return super.getCounterPenalty(active, targetObj);
    }

    @Override
    public int getUnconsciousDamagePriority() {
        return super.getUnconsciousDamagePriority();
    }

    @Override
    public void applyConvergingPathsPriorities(List<ActionSequence> actions) {
        super.applyConvergingPathsPriorities(actions);
    }
}
