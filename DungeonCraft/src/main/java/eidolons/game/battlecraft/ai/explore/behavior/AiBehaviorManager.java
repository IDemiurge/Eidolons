package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.UnitExploreAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Core;
import main.content.enums.EncounterEnums;
import main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.game.logic.action.context.Context;
import main.system.SortMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.datatypes.DequeImpl;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 10/15/2018.
 */
public class AiBehaviorManager extends AiHandler {

    public static AI_BEHAVIOR_MODE TESTED = !Flags.isFullFastMode() ? null :
            AI_BEHAVIOR_MODE.GUARD;
    public static final boolean TEST_MODE = TESTED != null;
    private static final UNIT_GROUP_TYPE TESTED_GROUP = !Flags.isFullFastMode() ? null :
            EncounterEnums.UNIT_GROUP_TYPE.GUARDS;
    Set<UnitExploreAI> aiSet = new LinkedHashSet<>();
    private final DequeImpl<ActionInput> aiActionQueue = new DequeImpl<>();
    private final Integer maxActiveCount = null;
    private final List<GroupAI> activeGroups = new ArrayList<>();
    private final boolean testMode = false;
    private List<GroupAI> groups;

    public AiBehaviorManager(AiMaster master) {
        super(master);
    }

    public static boolean isNewAiOn() {
        return !EidolonsGame.FOOTAGE;
    }

    public void act(float delta) {

        for (Unit unit : master.getGame().getUnits()) {
            UnitExploreAI ai = unit.getAI().getExploreAI();
            ai.act(delta);
        }
    }

    public boolean update() {
        //        List<Action> toExecute = new ArrayList<>();
        //        Set<Unit> units = master.getGame().getUnits();
        //        if (maxActiveCount!=null ){
        //            units = units.stream().filter(unit -> unit.isAiControlled()).
        //             sorted(
        //             SortMaster.getObjSorterByExpression(obj -> obj.getCoordinates().
        //              dst(Eidolons.getPlayerCoordinates()))).collect(Collectors.toCollection(LinkedHashSet::new)) ;
        //            for (Unit unit : new HashSet<>(units)) {
        //                units.addAll(unit.getAI().getGroup().getMembers());
        //            }
        //        }
        if (groups == null) {
            groups = new ArrayList<>(getGame().getAiManager().getGroups());
        }
        if (testMode) {
            groups.sort(new SortMaster<GroupAI>().getSorterByExpression_(groupAI -> -groupAI.getLeader().getCoordinates().
                    dst(Core.getPlayerCoordinates())));
        }
        Integer n = 0;
        for (GroupAI group : new ArrayList<>(groups)) {
            if (maxActiveCount != null)
                if (activeGroups.size() >= maxActiveCount) {
                    if (!activeGroups.contains(group)) {
                        continue;
                    }
                }
            try {
                if (checkGroupActs(group)) {
                    n++;
                    if (maxActiveCount != null) {
                        activeGroups.add(group);
                        if (maxActiveCount == n)
                            break;
                    }
                }
            } catch (Exception e) {
                if (!TEST_MODE)
                    groups.remove(group);
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return n > 0;

    }

    private boolean checkGroupActs(GroupAI group) {
        for (Unit unit : group.getMembers()) {
            master.setUnit(unit);
            UnitExploreAI ai = unit.getAI().getExploreAI();
            for (AiBehavior behavior : ai.getBehaviors()) {
                if (!isUpdated(ai, behavior))
                    continue;
                if (!behavior.update())
                    continue;//unit is not ready to act

                if (testMode) {
                    unit.setPlayerVisionStatus(PLAYER_VISION.DETECTED);
                    unit.setVisibilityLevelForPlayer(VISIBILITY_LEVEL.CLEAR_SIGHT);
                    unit.setDetectedByPlayer(true);
                    unit.setVisibilityOverride(false);
                }

                if (behavior.canAct()) {
                    Action action = behavior.nextAction();
                    // check action has been executed?
                    if (action == null) {
                        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.AI_DEBUG,
                                "No action for " + ai);
                        return false;
                    }

                    aiActionQueue.add(new ActionInput(action.getActive(),
                            new Context(action.getRef())));
                    return true;
                } else {
                    behavior.queueNextAction();
                }
            }
        }

        return false;
    }

    private boolean isUpdated(UnitExploreAI ai, AiBehavior behavior) {
        return !ai.isBehaviorOff();
    }

    public DequeImpl<ActionInput> getAiActionQueue() {
        return aiActionQueue;
    }

    public void initialize() {
        super.initialize();
        master.getManager().getGroups(); //TODO proper init groups!
        for (Unit unit : master.getGame().getUnits()) {
            if (unit.isAiControlled()) //TODO will need main hero ai anytime?
                initBehaviors(unit.getAI());
        }
    }

    private void initBehaviors(UnitAI ai) {
        ai.getExploreAI().setBehaviors(createBehaviors(ai));
    }

    private AiBehavior createAi(UnitAI ai, UNIT_GROUP_TYPE groupType) {
//        if (CoreEngine.isSafeMode()) {
//            return new WanderAi(master, ai);
//        }
        switch (groupType) {
            case GUARDS:
            case BOSS:
                if (ai.getGroupAI().getArg() == null) {
                    try {
                        ai.getGroupAI().setArg(game.getCell(
                                ai.getGroupAI().getLeader().getCoordinates().getAdjacentCoordinate(
                                        ai.getGroup().getLeader().getFacing().getDirection())));
                    } catch (Exception e) {
//                        CoordinatesMaster.getra
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
                return new GuardAi(master, ai, (DC_Obj) ai.getGroupAI().getArg());
            case PATROL:
                return new PatrolAi(master, ai);
        }
        return new WanderAi(master, ai);
    }

    private List<AiBehavior> createBehaviors(UnitAI ai) {
        if (ai.getUnit().isBoss()) {
            return new ArrayList<>();
        }
        List<AiBehavior> behaviors = new ArrayList<>();

        if (TEST_MODE) {
            ai.getGroupAI().setType(TESTED_GROUP);
            behaviors.add(createAi(ai, TESTED_GROUP));
            return behaviors;
        }
//        if (CoreEngine.isSafeMode() ||  ai.getGroupAI() == null) {
//            behaviors.add(new WanderAi(master, ai));
//            return behaviors;
//        }
        UNIT_GROUP_TYPE t;

        if (ai.getGroupAI() == null || ai.getGroupAI().getMembers().size() == 1) {
            t = EncounterEnums.UNIT_GROUP_TYPE.IDLERS;
        } else {
            t = ai.getGroupAI().getType();
        }
        //TODO use createAi () ?!
        switch (t) {
            case IDLERS:
                behaviors.add(new IdleAi(master, ai));
                break;

            case PATROL:
                behaviors.add(new PatrolAi(master, ai));
                break;
            case GUARDS:
            case BOSS:
                if (ai.getGroupAI().getArg() == null) {
//               TODO let them just STAND     behaviors.add(new IdleAi(master, ai));
                } else {
                    behaviors.add(new GuardAi(master, ai, (DC_Obj) ai.getGroupAI().getArg()));
                }
                break;
            default:
                behaviors.add(new WanderAi(master, ai));
        }


        return behaviors;
    }


    public List<AI_BEHAVIOR_MODE> getBehaviors() {
        //        if (checkAmbush()) {
        //            list.add(AI_BEHAVIOR_MODE.AMBUSH);
        //        }
        //        if (checkStalk()) {
        //            list.add(AI_BEHAVIOR_MODE.STALK);
        //        }
        //        if (checkAggro()) {
        //            list.add(AI_BEHAVIOR_MODE.AGGRO);
        //        }
        //
        //        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
        //            list.add(getPassiveBehavior());
        //        }

        return new ArrayList<>();
    }
}
