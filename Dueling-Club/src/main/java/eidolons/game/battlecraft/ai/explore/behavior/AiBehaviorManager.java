package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.UnitExploreAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.game.logic.action.context.Context;
import main.system.SortMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

/**
 * Created by JustMe on 10/15/2018.
 */
public class AiBehaviorManager extends AiHandler {

    public static final AI_BEHAVIOR_MODE TESTED = AI_BEHAVIOR_MODE.PATROL;
    private static final UNIT_GROUP_TYPE TESTED_GROUP = UNIT_GROUP_TYPE.PATROL;
    public static final boolean TEST_MODE = TESTED!=null ;
    Set<UnitExploreAI> aiSet = new LinkedHashSet<>();
    private DequeImpl<ActionInput> aiActionQueue = new DequeImpl<>();
    private Integer maxActiveCount = null;
    private List<GroupAI> activeGroups = new ArrayList<>();
    private boolean testMode = false;
    private List<GroupAI> groups;

    public AiBehaviorManager(AiMaster master) {
        super(master);
    }

    public static boolean isNewAiOn() {
        return true;
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
        //              dst(Eidolons.getMainHero().getCoordinates()))).collect(Collectors.toSet()) ;
        //            for (Unit unit : new HashSet<>(units)) {
        //                units.addAll(unit.getAI().getGroup().getMembers());
        //            }
        //        }
        if (groups == null)
        {
            groups = new ArrayList<>(getGame().getAiManager().getGroups());
        }
        if (testMode) {
            Collections.sort(groups,
             new SortMaster<GroupAI>().getSorterByExpression_(groupAI -> -groupAI.getLeader().getCoordinates().
              dst(Eidolons.getMainHero().getCoordinates())));
        }
        Integer n = 0;
        for (GroupAI group :     new ArrayList<>(groups)) {
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
                    aiActionQueue.add(new ActionInput(action.getActive(),
                     new Context(action.getRef())));
                    return true;
                } else {
                    behavior.queueNextAction();
                    continue;
                }
            }
        }

        return false;
    }

    private boolean isUpdated(UnitExploreAI ai, AiBehavior behavior) {
if (ai.isBehaviorOff())
    return false;
        return true;
    }

    public DequeImpl<ActionInput> getAiActionQueue() {
        return aiActionQueue;
    }

    public void initialize() {
        super.initialize();
        master.getManager().getGroups(); //TODO proper init groups!
        for (Unit unit : master.getGame().getUnits()) {
            initBehaviors(unit.getAI());
        }
    }

    private void initBehaviors(UnitAI ai) {
        ai.getExploreAI().setBehaviors(createBehaviors(ai));
    }

    private List<AiBehavior> createBehaviors(UnitAI ai) {
        List<AiBehavior> behaviors = new ArrayList<>();

        if (TEST_MODE) {
            ai.getGroupAI().setType(TESTED_GROUP);
            behaviors.add(new PatrolAi(master, ai));
            return behaviors;
        }
        if (ai.getGroupAI() == null) {
            behaviors.add(new WanderAi(master, ai));
            return behaviors;
        }
        switch (ai.getGroupAI().getType()) {
            case PATROL:
                    behaviors.add(new PatrolAi(master, ai));
                    break;
            case GUARDS:
            case BOSS:
                behaviors.add(new GuardAi(master, ai, (DC_Obj) ai.getGroupAI().getArg()));
                break;
            default:
                behaviors.add(new WanderAi(master, ai));
        }


        return behaviors;
    }

    public List<AI_BEHAVIOR_MODE> getBehaviors() {
        List<AI_BEHAVIOR_MODE> list = new ArrayList<>();
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

        return list;
    }
}
