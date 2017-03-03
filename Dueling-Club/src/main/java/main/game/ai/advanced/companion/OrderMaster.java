package main.game.ai.advanced.companion;

import main.content.CONTENT_CONSTS2.GLOBAL_ORDER_TYPE;
import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.elements.conditions.Condition;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.ai.GroupAI;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.elements.task.Task;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;

import java.util.List;

public class OrderMaster extends AiHandler{
    public OrderMaster(AiHandler master) {
        super(master);
    }
    // also used in scripting enemies?

    public   Action checkFollowOrder(UnitAI ai) {
        Order order = ai.getCurrentOrder();
        if (order == null) {
//            order = ai.getGroup().getOrder();
            if (order == null) {
                return null;
            }
        }
        if (!checkValid(order)) {
            ai.setCurrentOrder(null);
            return null;
        }
        return order.getSequence().getNextAction();
        // check re-build sequence
    }

    private   void initOrderSequence(Order order) {
        Action action = new Action(order.getAi().getUnit().getAction(order.getActionType()));
        Task task = new Task(order.getAi(), getGoalType(order.getType()), order.getArg());
        List<ActionSequence> sequences =  getActionSequenceConstructor().getSequences(action, order
                .getArg(), task);
        DC_PriorityManager.setPriorities(sequences);
        for (ActionSequence sequence : sequences) {
            SpecialPriorities.applySpecialPriorities(sequence);
        }

        ActionSequence sequence = DC_PriorityManager.chooseByPriority(sequences);
        order.setSequence(sequence);
        // special prioritizing? companion ai...
    }

    private   GOAL_TYPE getGoalType(ORDER_TYPE type) {
        switch (type) {
            case ATTACK:
                return GOAL_TYPE.ATTACK;
            case HEAL:
                return GOAL_TYPE.RESTORE;
            case HOLD:
                return GOAL_TYPE.DEFEND;
            case KILL:
                return GOAL_TYPE.ATTACK;
            case MOVE:
                return GOAL_TYPE.MOVE;
            case PATROL:
                return GOAL_TYPE.MOVE;
            case PROTECT:
                return GOAL_TYPE.DEFEND;
            case PURSUIT:
                return GOAL_TYPE.MOVE;
            case SPECIAL:
                return GOAL_TYPE.CUSTOM_HOSTILE;
            case SUPPORT:
                return GOAL_TYPE.BUFF;
            default:
                break;

        }
        return null;
    }

    private   boolean checkValid(Order order) {
        if (!order.getSequence().getNextAction().canBeActivated()) {
            // alt action
        }
        if (!order.getSequence().getNextAction().canBeTargeted()) {
            // re-path - compare again alt-sequences
        }
        return true;
    }

    public   void giveGlobalOrders(Unit leader) {
        String type = ListChooser.chooseEnum(GLOBAL_ORDER_TYPE.class);
        if (type == null) {
            type = ListChooser.chooseEnum(ORDER_TYPE.class);
        }
        if (type == null) {
            return;
        }
        GroupAI group = leader.getUnitAI().getGroup();

//        group.setOrder(new Order(group, type, arg));
        for (Unit member :
                group.getMembers()) {
//            member.getUnitAI().setCurrentBehavior(currentBehavior);
        }
    }

    // TODO paged panel with 3-4 order-items
    // step-sequence: choose order type, select arg!

    // as Action/Effect?
    public   void giveOrders(Unit unit, Unit leader) {
        String type = ListChooser.chooseEnum(ORDER_TYPE.class);
        if (type == null) {
            return;
        }
        ORDER_TYPE TYPE = new EnumMaster<ORDER_TYPE>().retrieveEnumConst(ORDER_TYPE.class, type);

        Condition conditions = null;
        switch (TYPE) {

        }
        Ref ref = null;
        new SelectiveTargeting(conditions).select(ref);
        Order order = new Order(unit.getUnitAI(), TYPE, ref.getTarget() + "");
        initOrderSequence(order);
        unit.getUnitAI().setPlayerOrdered(true);

    }

}
