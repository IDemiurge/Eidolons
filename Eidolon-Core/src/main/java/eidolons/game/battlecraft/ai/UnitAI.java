package eidolons.game.battlecraft.ai;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.tools.AiExecutor;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.content.enums.rules.VisionEnums.ENGAGEMENT_LEVEL;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UnitAI {

    public static final Integer DEFAULT_VERBATIM_MOD = null;
    Unit unit;
    UnitCombatAI combatAI;
    UnitExploreAI exploreAI;
    private GroupAI groupAI;
    private boolean outsideCombat;
    private ActionSequence standingOrders;
    private ORDER_TYPE orderType;
    private Order currentOrder;

    private List<ActiveObj> usedActions;
    private boolean engagedOverride;
    ENGAGEMENT_LEVEL engagementLevel=ENGAGEMENT_LEVEL.UNSUSPECTING;

    //    private CHARACTER_TYPE characterType;
//    private INCLINATION_TYPE characterType;
//    private IMPULSE_TYPE impulseType;
//    Map<IMPULSE_TYPE, Integer> impulseMap;

    public UnitAI(Unit unit) {
        this.unit = unit;
        //can we avoid this dependency?
        exploreAI = new UnitExploreAI(unit);
        combatAI = new UnitCombatAI(unit);
//        initType();

    }

    public UnitCombatAI getCombatAI() {
        return combatAI;
    }

    public UnitExploreAI getExploreAI() {
        return exploreAI;
    }

    public AI_TYPE getType() {
        return combatAI.getType();
    }

    public void setType(AI_TYPE type) {
        combatAI.setType(type);
    }

    public int getLogLevel() {
        // TODO group leader? by level? By selection?
        return AiLogger.LOG_LEVEL_FULL;

    }

    @Override
    public String toString() {
        return "AI: " + getUnit();
    }


    public Unit getUnit() {
        return unit;
    }


    public BEHAVIOR_MODE getBehaviorMode() {
        return unit.getBehaviorMode();

    }

    public boolean checkMod(AI_MODIFIERS trueBrute) {
        return unit.checkAiMod(trueBrute);
    }


    private boolean checkStalk() {
        // if (getGroup().getEngagementLevel()==ENGAGEMENT_LEVEL.UNSUSPECTING)
        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
            return false;
        }
        return unit.getAiType() == AI_TYPE.SNEAK;
    }

    private boolean checkAmbush() {
        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
            return false;
        }
        // intelligence preCheck? group preCheck?
        if (unit.getAiType() == AI_TYPE.ARCHER) {
            return true;
        }
        return unit.getAiType() == AI_TYPE.CASTER;
        // if (group.isAmbushing()) return true;
        // leader?
    }

    public ENGAGEMENT_LEVEL getEngagementLevel() {
        // if (engagementLevel == null) {
        //    return getGroup().getEngagementLevel();
        // }
        return engagementLevel;
    }

    public boolean isLeader() {
        if (getGroup() == null) return true;
        return getGroup().getLeader() == getUnit();
    }

    public boolean checkStandingOrders() {
        return checkStandingOrders(false);
    }

    public boolean checkStandingOrders(boolean force) {
        // change orders
        if (getStandingOrders() != null) {
            if (ListMaster.isNotEmpty(getStandingOrders().getActions())) {
                if (!getStandingOrders().get(0).canBeActivated()) {
                    if (!force)
                        return false;
                    getStandingOrders().get(0).getActive().setFree(true);
                }
                if (!force)
                    return getStandingOrders().get(0).canBeTargeted();

            }
            return true;
        }
        return false;
    }

    public ActionSequence getStandingOrders() {
        return standingOrders;
    }

    public void setStandingOrders(ActionSequence standingOrders) {
        if (standingOrders != null) {
            if (standingOrders.getType() == GOAL_TYPE.MOVE) {
                orderType = ORDER_TYPE.MOVE;
            } else if (standingOrders.getType() == GOAL_TYPE.WANDER) {
                orderType = ORDER_TYPE.WANDER;
            } else if (standingOrders.getType() == GOAL_TYPE.WANDER) {
                orderType = ORDER_TYPE.PATROL;
            } else if (standingOrders.getType() == GOAL_TYPE.STALK
                    || standingOrders.getType() == GOAL_TYPE.AGGRO) {
                orderType = ORDER_TYPE.PURSUIT;
            }
        }
        main.system.auxiliary.log.LogMaster.devLog(unit + " received orders: " + standingOrders);
        this.standingOrders = standingOrders;
    }

    public void checkSetOrders(ActionSequence sequence) {
        orderType = null;
        if ((sequence.getType() == GOAL_TYPE.WANDER)) {
            orderType = ORDER_TYPE.WANDER;
        }
        if ((sequence.getType() == GOAL_TYPE.PATROL)) {
            orderType = ORDER_TYPE.PATROL;
        }

        if (orderType != null) {
            standingOrders = sequence;
        }

    }

    public GroupAI getGroup() {
        return getGroupAI();
    }

    public GroupAI getGroupAI() {
        if (!getUnit().getGame().getAiManager().isDefaultAiGroupForUnitOn())
            if (groupAI == null) {
                groupAI = (unit.getGame().getAiManager().getCustomUnitGroup(getUnit()));
                main.system.auxiliary.log.LogMaster.log(1,"Custom AI UnitGroup created for "+unit.getNameAndCoordinate() );
                if (groupAI != null) {
                    groupAI.add(getUnit());
                }
            }
        return groupAI;
    }

    public void setGroupAI(GroupAI groupAI) {
        this.groupAI = groupAI;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Integer getGoalPriorityMod(GOAL_TYPE goalType) {
        if (currentOrder == null) return null;

        if (currentOrder.getStrictPriority() != null) {
            return Arrays.stream(currentOrder.getStrictPriority().getGoalTypes()).
                    collect(Collectors.toList()).contains(goalType)
                    ? 1000 : 0;
        }

        return currentOrder.getPriorityModsMap().get(goalType);
    }


    public boolean isFree() {
        return combatAI.isFree();
    }

    public void setFree(boolean free) {
        combatAI.setFree(free);
    }

    public boolean isOutsideCombat() {
        return outsideCombat;
    }

    public void setOutsideCombat(boolean outsideCombat) {
        this.outsideCombat = outsideCombat;
    }


    public List<ActiveObj> getUsedActions() {
        if (usedActions == null) {
            usedActions = new ArrayList<>();
        }
        return usedActions;
    }

    public AiExecutor getExecutor() {
        return combatAI.getExecutor();
    }

    public void setExecutor(AiExecutor executor) {
        combatAI.setExecutor(executor);
    }


    public int getEngagementDuration() {
        return combatAI.getEngagementDuration();
    }

    public void setEngagementDuration(int engagementDuration) {
        combatAI.setEngagementDuration(engagementDuration);
    }

    public boolean isEngaged() {
        return combatAI.isEngaged();
    }

    public void setEngaged(boolean engaged) {
        combatAI.setEngaged(engaged);
    }

    public List<AiAction> getForcedActions() {
        return combatAI.getForcedActions();
    }


    public boolean isPathBlocked() {
        return exploreAI.isPathBlocked();
    }

    public void setPathBlocked(boolean pathBlocked) {
        exploreAI.setPathBlocked(pathBlocked);
    }

    public float getExplorationTimePassed() {
        return exploreAI.getExplorationTimePassed();
    }

    public void setExplorationTimePassed(float explorationTimePassed) {
        exploreAI.setExplorationTimePassed(explorationTimePassed);
    }

    public float getExplorationTimeOfLastAction() {
        return exploreAI.getExplorationTimeOfLastAction();
    }

    public void setExplorationTimeOfLastAction(float explorationTimeOfLastAction) {
        exploreAI.setExplorationTimeOfLastAction(explorationTimeOfLastAction);
    }

    public float getExplorationTimeOfModeEffect() {
        return exploreAI.getExplorationTimeOfModeEffect();
    }

    public void setExplorationTimeOfModeEffect(float explorationTimeOfModeEffect) {
        exploreAI.setExplorationTimeOfModeEffect(explorationTimeOfModeEffect);
    }

    public float getExplorationTimeOfRegenEffects() {
        return exploreAI.getExplorationTimeOfRegenEffects();
    }

    public void setExplorationTimeOfRegenEffects(float explorationTimeOfRegenEffects) {
        exploreAI.setExplorationTimeOfRegenEffects(explorationTimeOfRegenEffects);
    }

    public boolean isAutoFollow() {
        return exploreAI.isAutoFollow();
    }

    public void setAutoFollow(boolean autoFollow) {
        exploreAI.setAutoFollow(autoFollow);
    }

    public float getExplorationMoveSpeedMod() {
        return exploreAI.getExplorationMoveSpeedMod();
    }

    public void setExplorationMoveSpeedMod(float explorationMoveSpeedMod) {
        exploreAI.setExplorationMoveSpeedMod(explorationMoveSpeedMod);
    }

    public void combatEnded() {
        setEngagementDuration(0);
        setEngaged(false);
    }

    public void setEngagedOverride(boolean engagedOverride) {
        this.engagedOverride = engagedOverride;
    }

    public boolean isEngagedOverride() {
        return engagedOverride;
    }

    public void setEngagementLevel(ENGAGEMENT_LEVEL engagementLevel) {
        this.engagementLevel = engagementLevel;
    }

    public enum AI_BEHAVIOR_MODE {
        WANDER, AMBUSH, AGGRO, STALK, PATROL, GUARD,
    }

}
