package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.entity.obj.Obj;

import java.util.List;

/**
 * 1) preCheck forced 2) create action sequences for default goal 3) choose the
 * topmost
 * <portrait>
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
    private static AiMaster aiMaster;
    private static PriorityManager mainImpl;

    public static PriorityManager alt(AiMaster master) {
        PriorityManagerImpl alt = new PriorityManagerImpl(master);
        return alt;
    }

    public static PriorityManager init(AiMaster master) {
        impl = new PriorityManagerImpl(master);
        mainImpl = impl;
        aiMaster = master;
        return impl;
    }


    public static float getCapacity(Unit unit) {
        return impl.calculateCapacity(unit);
    }

    public static int getAttackPriority(ActionSequence as) {
        return impl.getAttackPriority(as);
    }

    public static int getAttackPriority(DC_ActiveObj active, BattleFieldObject targetObj) {
        Unit unit = mainImpl.getMaster().getUnit();
        mainImpl.getMaster().setUnit(active.getOwnerUnit());
        //        toggleImplementation(new PriorityManagerImpl(mainImpl.getMaster()) {
        //        });
        int p = 0;
        try {
            p = impl.getAttackPriority(active, targetObj);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            mainImpl.getMaster().setUnit(unit);
        }
        return p;
    }

    public static void toggleImplementation(PriorityManager newImpl) {
        if (newImpl == null) {
            impl = mainImpl;
            return;
        }
        mainImpl = impl;
        impl = newImpl;
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

    public static ActionSequence chooseByPriority(List<ActionSequence> actions) {
        return impl.chooseByPriority(actions);
    }


    public static void setPriorities(List<ActionSequence> actions) {
        impl.setPriorities(actions);
    }

    public static int getPriority(ActionSequence sequence) {
        return impl.getPriority(sequence);
    }


}
