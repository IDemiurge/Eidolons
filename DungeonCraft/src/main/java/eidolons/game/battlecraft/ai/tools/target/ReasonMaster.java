package eidolons.game.battlecraft.ai.tools.target;

import eidolons.ability.conditions.VisibilityCondition;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class ReasonMaster {
    public static boolean checkReasonCannotActivate(DC_ActiveObj action, String reason) {
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(action);
        for (String r : reasons) {
            if (StringMaster.compareByChar(r, reason, false)) {
                return true;
            }
        }
        for (String r : reasons) {
            if (StringMaster.compare(r, reason, false)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkReasonCannotActivate(AiAction aiAction, String reason) {
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(aiAction);
        for (String r : reasons) {
            if (StringMaster.compareByChar(r, reason, false)) {
                return true;
            }
        }
        for (String r : reasons) {
            if (StringMaster.compare(r, reason, false)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getReasonsCannotActivate(DC_ActiveObj action) {

        Ref REF = action.getRef().getCopy();
        REF.setMatch(action.getRef().getTarget());
        REF.setID(KEYS.PAYEE, action.getOwnerUnit().getId());
        return getReasonsCannotActivate(action, REF);
    }

    public static List<String> getReasonsCannotActivate(AiAction aiAction) {
        // action.getActive().getCosts().getReasons()
        Ref REF = aiAction.getRef().getCopy();
        REF.setMatch(aiAction.getRef().getTarget());
        REF.setID(KEYS.PAYEE, aiAction.getSource().getId());
        return getReasonsCannotActivate(aiAction.getActive(), REF);
        // Map<String, Condition> reqs =
        // action.getActive().getCosts().getRequirements().getReqMap();
        // for (Condition c : reqs.values()) {
        // if (!c.preCheck(REF)) {
        // reasons.add
        // (new MapMaster<String, Condition>().getKeyForValue(reqs, c));
        // }
        // }
        //
        // return reasons;
    }

    public static List<String> getReasonsCannotActivate(DC_ActiveObj active, Ref REF) {
        List<String> reasons = new ArrayList<>();
        REF.setID(KEYS.PAYEE, REF.getSource());
        if (active.getCosts().canBePaid(REF)) {
            return reasons;
        }
        return active.getCosts().getReasonList();
    }

    public static boolean checkReasonCannotTarget(FILTER_REASON reason, AiAction aiAction) {
        return !getReasonsCannotTarget(aiAction, false, false, reason).isEmpty();
    }

    public static List<FILTER_REASON> getReasonsCannotTarget(AiAction aiAction
                                                             //     , boolean ignoreOthersIfNotFacing
    ) {

        List<FILTER_REASON> reasonsCannotTarget = getReasonsCannotTarget(aiAction, true, false, null);
        // if reasons are something other than single FACING, we don't care about OTHERS
        if (!reasonsCannotTarget.isEmpty()) {
            if (reasonsCannotTarget.size() != 1
                    && reasonsCannotTarget.get(0) != (FILTER_REASON.FACING)) {
                return reasonsCannotTarget;
            }
        }

        reasonsCannotTarget = getReasonsCannotTarget(aiAction, true, true, null);
        return reasonsCannotTarget;
    }

    public static List<FILTER_REASON> getReasonsCannotTarget(AiAction aiAction,
                                                             boolean useConditionResultCache, Boolean checkMiscOrOnly, FILTER_REASON searchedReason) {
        Ref REF = aiAction.getRef().getCopy();
        REF.setMatch(aiAction.getRef().getTarget());
        Targeting targeting = aiAction.getActive().getTargeting();

        if (targeting == null) {
            targeting = TargetingMaster.findTargeting(aiAction.getActive());
        }
        if (targeting == null) {
            return new ArrayList<>();
        }
        Conditions conditions = targeting.getFilter().getConditions();
        // conditions.preCheck(REF);
        List<FILTER_REASON> reasons = new ArrayList<>();
        for (Condition c : conditions) {
            FILTER_REASON reason = getReason(c);
            if (searchedReason != null) {
                if (reason != searchedReason) {
                    continue;
                }
            }
            if (checkMiscOrOnly == null) {
                if (reason != FILTER_REASON.OTHER) {
                    continue;
                }
            } else if (!checkMiscOrOnly) {
                if (reason == FILTER_REASON.OTHER) {
                    continue;
                }
            }
            //TODO ai Review - is this caching possible/needed?
            //= c.isTrue();
            // if (result==null || !useConditionResultCache) {
            boolean result = c.preCheck(REF);
            // }
            // else {
            //    LogMaster.log(1, c + " reason uses cached result: " + result+ " on " + REF);
            // }
            if (!result) {
                if (reason != null) {
                    reasons.add(reason);
                }
            }
        }

        return reasons;
    }

    private static FILTER_REASON getReason(Condition c) {
        if (c instanceof VisibilityCondition) {
            return FILTER_REASON.VISION;
        }

        if (c instanceof DistanceCondition) {
            return FILTER_REASON.DISTANCE;
        }

        return FILTER_REASON.OTHER;
    }

    public static boolean isAdjacentTargeting(AiAction targetAiAction) {
        Targeting targeting = targetAiAction.getActive().getTargeting();
        try {
            for (Condition c : targeting.getFilter().getConditions()) {
                if (c instanceof DistanceCondition) {
                    if (((DistanceCondition) c).getDistance().getInt(targetAiAction.getRef()) == 1) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    // kind of like analyzer here
    public enum FILTER_REASON {
        VISION, FACING, DISTANCE, IMMUNE, CONCEALMENT, SUICIDE, OTHER

    }

}
