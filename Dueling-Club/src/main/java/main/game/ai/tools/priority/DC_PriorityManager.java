package main.game.ai.tools.priority;

import main.elements.costs.Costs;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.generic.AiHandler;

import java.util.List;

/**
 * 1) preCheck forced 2) create action sequences for default goal 3) choose the
 * topmost
 * <p>
 * to choose an action sequence, first create all possible tasks if there are X
 * enemies, create X Engage tasks, for instance Then for each of these tasks,
 * create all possible action sequences. >> Engage via Attack = turn > turn >
 * move > attack >> Engage via Stone Gaze = turn > turn > stone gaze If all
 * action sequences have the same first action, fast-skip to executing it. So
 * that in the end, it's move>attack VS stone gaze
 *
 * @author JustMe
 */

public class DC_PriorityManager {


    private static PriorityManager impl;

    public static PriorityManager
    init(AiHandler handler) {
        impl = new PriorityManagerImpl(handler);
        return impl;
    }


    public static float getCapacity(Unit unit) {
        return impl.calculateCapacity(unit);
    }

    public static int getAttackPriority(ActionSequence as) {
        return impl.getAttackPriority(as);
    }

    public static int getAttackPriority(DC_ActiveObj active, Unit targetObj) {
        return impl.getAttackPriority(active, targetObj);
    }

    public static int getUnitPriority(Obj targetObj) {
        return impl.getUnitPriority(targetObj);
    }

    public static int getUnitPriority(Obj targetObj, Boolean less_or_more_for_health) {
        return impl.getUnitPriority(targetObj, less_or_more_for_health);
    }

    public static int getUnitPriority(UnitAI unit_ai, Obj targetObj, Boolean less_or_more_for_health) {
        return impl.getUnitPriority(unit_ai, targetObj, less_or_more_for_health);
    }

    public static int getHealthFactor(Obj targetObj, Boolean less_or_more_for_health) {
        return impl.getHealthFactor(targetObj, less_or_more_for_health);
    }

    public static int getMeleeDangerFactor(Unit unit) {
        return impl.getMeleeDangerFactor(unit);
    }

    public static int getMeleeThreat(Unit enemy) {
        return impl.getMeleeThreat(enemy);
    }

    public static int getCostFactor(Costs cost, Unit unit) {
        return impl.getCostFactor(cost, unit);
    }


    public static ActionSequence chooseByPriority(List<ActionSequence> actions) {
        return impl.chooseByPriority(actions);
    }


    public static void setPriorities(List<ActionSequence> actions) {
        impl.setPriorities(actions);
    }

    public static int getPriority(ActionSequence sequence) {
        return impl.getPriority(sequence);
    }


    //    public int getActionNumberFactor(int n) {
//        return MathMaster.calculateFormula(ACTION_FORMULA, n);
//    }


}
