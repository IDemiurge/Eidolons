package main.game.ai.tools.target;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.target.ReasonMaster.FILTER_REASON;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.ClassMaster;

import java.util.List;
import java.util.Set;

public class TargetingMaster extends AiHandler{
    public TargetingMaster(AiHandler master) {
        super(master);
    }

    public static int pruneLimit = 5;

    public static Targeting findTargeting(ActiveObj active) {
        return findTargeting(active, null);
    }

    public static Targeting getZoneEffect(DC_ActiveObj active) {
        List<Effect> zoneEffects = EffectFinder.getEffectsOfClass(active,
                SpecialTargetingEffect.class);
        if (!zoneEffects.isEmpty()) {
            SpecialTargetingEffect zoneEffect = (SpecialTargetingEffect) zoneEffects
                    .get(0);
            zoneEffect.initTargeting();
            return zoneEffect.getTargeting();
        }
        return active.getTargeting();
    }

    public static Targeting findTargeting(ActiveObj active,
                                          Class<SelectiveTargeting> CLASS) {
        Targeting t = active.getTargeting();
        if (checkTargeting(CLASS, t)) {
            return t;
        }

        t = findTargetingInAbils(active, CLASS);
        if (t != null) {
            return t;
        }

        for (ActiveObj a : active.getActives()) {
            if (active instanceof DC_ActiveObj)// 2 layers maximum, i hope
            {
                t = findTargeting(a, CLASS);
            }
            if (t != null) {
                return t;
            } else {
                for (ActiveObj a2 : a.getActives()) {
                    t = findTargetingInAbils(a2, CLASS);
                    if (t != null) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    private static boolean checkTargeting(Class<SelectiveTargeting> CLASS,
                                          Targeting t) {
        if (CLASS == null && t != null) {
            return true;
        }
        return ClassMaster.isInstanceOf(t, CLASS);
    }

    public static Targeting findTargetingInAbils(ActiveObj active,
                                                 Class<SelectiveTargeting> CLASS) {
        if (active.getAbilities() != null) {
            for (Ability abil : active.getAbilities()) {
                if (abil.getTargeting() != null)
                // if (abil.getTargeting().getClass().equals(CLASS))
                {
                    if (checkTargeting(CLASS, abil.getTargeting())) {
                        return abil.getTargeting();
                    }
                }
            }
        }
        return null;
    }

    public static boolean isValidTargetingCell(Action targetAction, Coordinates c, Unit unit) {

        return unit.getGame().getBattleFieldManager()
         .canMoveOnto(targetAction.getSource(), c);
    }

    public static boolean canBeTargeted(Action action) {
        return canBeTargeted(action, true);
    }

    public static boolean canBeTargeted(Action action, boolean ignoreFacing) {
        try {
            if (action.canBeTargeted(action.getTarget().getId())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        if (!ignoreFacing) {
            return false;
        }
        List<FILTER_REASON> reasons = ReasonMaster.getReasonsCannotTarget(action);
        // boolean visionRemoved = false;
        // if (reasons.contains(FILTER_REASON.FACING)
        // && !reasons.contains(FILTER_REASON.DISTANCE))
        // if (ReasonMaster.isAdjacentTargeting(action)) {
        // if (reasons.contains(FILTER_REASON.VISION)) {
        // reasons.remove(FILTER_REASON.VISION);
        // visionRemoved = true;
        // }
        // }
        if (action.getActive().isMelee()) {
            if (reasons.size() == 1) // what about DISTANCE?
            {
                if (reasons.get(0) == (FILTER_REASON.FACING)) {
                    // if (!visionRemoved)
                    // main.system.auxiliary.LogMaster.log(1, "!!!");
                    // else
                    return true;
                }
            }
        }

        return false;
    }

    public static Integer selectTargetForAction(DC_ActiveObj a) {
        /*
         * getOrCreate possible targets init goal type prioritize
		 */
        GOAL_TYPE type = GoalManager.getGoalFromAction(a);

        Obj target = null;
        int max_priority = Integer.MIN_VALUE;
        Set<Obj> objects = null;
        try {
            objects = a.getTargeting().getFilter().getObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Obj obj : objects) {
            ActionSequence sequence = new ActionSequence(type, new Action(a, obj));
            sequence.setAi(a.getOwnerObj().getUnitAI());
            sequence.setType(type);
            int priority = DC_PriorityManager.getPriority(sequence);
            if (priority > max_priority) {
                target = obj;
                max_priority = priority;
            }
        }
        if (target == null) {
            return null;
        }
        return target.getId();
    }
}