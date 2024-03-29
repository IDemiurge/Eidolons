package eidolons.game.battlecraft.ai.tools.target;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster.FILTER_REASON;
import eidolons.game.core.Core;
import eidolons.game.core.master.EffectMaster;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.ClassMaster;

import java.util.List;
import java.util.Set;

public class TargetingMaster extends AiHandler {

    public TargetingMaster(AiMaster master) {
        super(master);
    }

    public static Targeting findTargeting(IActiveObj active) {
        return findTargeting(active, null);
    }

    public static Targeting getZoneEffect(ActiveObj active) {
        List<Effect> zoneEffects = EffectMaster.getEffectsOfClass(active,
                SpecialTargetingEffect.class);
        if (!zoneEffects.isEmpty()) {
            SpecialTargetingEffect zoneEffect = (SpecialTargetingEffect) zoneEffects
                    .get(0);
            zoneEffect.setRef(active.getRef());
            zoneEffect.initTargeting();
            return zoneEffect.getTargeting();
        }
        return active.getTargeting();
    }

    public static Targeting findTargeting(IActiveObj active,
                                          Class<SelectiveTargeting> CLASS) {
        Targeting t = active.getTargeting();
        if (checkTargeting(CLASS, t)) {
            return t;
        }

        t = findTargetingInAbils(active, CLASS);
        if (t != null) {
            return t;
        }

        for (IActiveObj a : active.getActives()) {
            if (active instanceof ActiveObj)// 2 layers maximum, i hope
            {
                t = findTargeting(a, CLASS);
            }
            if (t != null) {
                return t;
            } else {
                for (IActiveObj a2 : a.getActives()) {
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

    public static Targeting findTargetingInAbils(IActiveObj active,
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

    public static boolean isValidTargetingCell(AiAction targetAiAction, Coordinates c, Unit unit) {
        // TODO this could be better done
        //AI FIX!
        return unit.getGame().getBattleFieldManager()
                .canMoveOnto(targetAiAction.getSource(), c);
    }


    public static boolean canBeTargeted(AiAction aiAction, boolean ignoreFacing) {
        return canBeTargeted(aiAction, false, ignoreFacing);
    }

    public static boolean canBeTargeted(AiAction aiAction, boolean ignoreVisibility, boolean ignoreFacing) {

        if (aiAction.canBeTargeted(aiAction.getTarget().getId())) {
            return true;
        }

        if (!ignoreFacing && !ignoreVisibility) {
            return false;
        }
        List<FILTER_REASON> reasons = ReasonMaster.getReasonsCannotTarget(aiAction);
        if (AI_Manager.MELEE_HACK)
        if (!reasons.contains(FILTER_REASON.DISTANCE))
            return true;
        // boolean visionRemoved = false;
        // if (reasons.contains(FILTER_REASON.FACING)
        // && !reasons.contains(FILTER_REASON.DISTANCE))
        // if (ReasonMaster.isAdjacentTargeting(action)) {
        // if (reasons.contains(FILTER_REASON.VISION)) {
        // reasons.remove(FILTER_REASON.VISION);
        // visionRemoved = true;
        // }
        // }
        //        if (action.getActive().isMelee()) {
        if (reasons.size() == 1) // what about DISTANCE?
        {
            // if (!visionRemoved)
            // main.system.auxiliary.LogMaster.log(1, "!!!");
            // else
            return reasons.get(0) == (FILTER_REASON.FACING);
        }
        //        }

        return false;
    }

    public static Integer selectTargetForAction(ActiveObj a) {
        /*
         * getOrCreate possible targets init goal type prioritize
         */
        GOAL_TYPE type = GoalManager.getGoalFromAction(a);

        Obj target = null;
        int max_priority = Integer.MIN_VALUE;
        Set<Obj> objects = null;
        a.getTargeting().getFilter().setRef(a.getRef());
        try {
            objects = a.getTargeting().getFilter().getObjects();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (a.getOwnerUnit().getAI().getStandingOrders() != null) {
            return Core.getMainHero().getId();
        } else
            for (Obj obj : objects) {
                ActionSequence sequence = new ActionSequence(type, new AiAction(a, obj));
                sequence.setAi(a.getOwnerUnit().getUnitAI());
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
