package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.UnitExploreAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.core.ActionInput;
import main.game.logic.action.context.Context;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 10/15/2018.
 */
public class AiBehaviorManager extends AiHandler {

    public static final AI_BEHAVIOR_MODE TESTED = AI_BEHAVIOR_MODE.WANDER;
    Set<UnitExploreAI> aiSet = new LinkedHashSet<>();
    private DequeImpl<ActionInput> aiActionQueue= new DequeImpl<>();
    private Integer maxActiveCount=1;

    public AiBehaviorManager(AiMaster master) {
        super(master);
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
        Integer n=0;
        for (GroupAI group : getGame().getAiManager().getGroups()) {
            if (checkGroupActs(group))
                n++;
            if (maxActiveCount!=null )
                if (maxActiveCount==n)
                    break;
        }
        return n>0;

    }

    private boolean checkGroupActs(GroupAI group) {
        for (Unit unit : group.getMembers()) {
            master.setUnit(unit);
            UnitExploreAI ai = unit.getAI().getExploreAI();
            for (AiBehavior behavior : ai.getBehaviors()) {
                if (!isUpdated(behavior))
                    return false;
                if (!behavior.update())
                    return false; //unit is not ready to act
                if (behavior.canAct()){
                    Action action = behavior.nextAction();
                    // check action has been executed?
                    aiActionQueue.add(new ActionInput(action.getActive(),
                     new Context(action.getRef())));
                    return true;
                } else {
                    behavior.queueNextAction();
                    return false;
                }
            }
        }

        return false;
    }

    private boolean isUpdated(AiBehavior behavior) {

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
        if (ai.getGroupAI() == null) {
            behaviors.add(new WanderAi(master, ai));
            return behaviors;
        }
        switch (ai.getGroupAI().getType()) {
            case GUARDS:
                behaviors.add(new GuardAi(master, ai));
                break;
            case PATROL:
                behaviors.add(new PatrolAi(master, ai));
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

    public static boolean isNewAiOn() {
        return true;
    }
}
