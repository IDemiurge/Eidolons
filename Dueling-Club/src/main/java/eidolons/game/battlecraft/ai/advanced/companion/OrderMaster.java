package eidolons.game.battlecraft.ai.advanced.companion;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.task.Task;
import main.entity.obj.ActiveObj;
import main.system.math.MathMaster;

public class OrderMaster extends AiHandler {
    private static final int DEFAULT_CHANCE = 75;

    // used in scripting enemies?
    public OrderMaster(AiMaster master) {
        super(master);
    }

    public static boolean checkOrderCompleted(Action action) {
        UnitAI ai = action.getSource().getAI();
        Order order = ai.getCurrentOrder();
        Task task = action.getTask();
        return false;
    }

    public static int getSuccessChance(boolean partyTargeting, Order order,
                                       Unit target, Unit source, ActiveObj active) {
        int chance = DEFAULT_CHANCE;
        chance += source.getIntParam(PARAMS.LEADERSHIP_MASTERY);
        chance += target.getIntParam(PARAMS.ORGANIZATION) - 50;
//        order.geti
        chance = MathMaster.applyMod(chance, active.getIntParam(PARAMS.ORDER_CHANCE_MOD,
         false));
        if (partyTargeting) {
            chance = chance * 75 / 100;
        }

        //battle spirit?

        return chance;
    }

    //atomic logic effects?


//    public Action checkFollowOrder(UnitAI ai) {
//        Order order = ai.getCurrentOrder();
//        if (order == null) {
////            order = ai.getGroup().getOrder();
//            if (order == null) {
//                return null;
//            }
//        }
//        if (!checkValid(order)) {
//            ai.setCurrentOrder(null);
//            return null;
//        }
//        return order.getSequence().getNextAction();
//        // preCheck re-build sequence
//    }


//    private GOAL_TYPE getGoalType(ORDER_TYPE type) {
//        switch (type) {
//            case ATTACK:
//                return GOAL_TYPE.ATTACK;
//            case HEAL:
//                return GOAL_TYPE.RESTORE;
//            case HOLD:
//                return GOAL_TYPE.DEFEND;
//            case KILL:
//                return GOAL_TYPE.ATTACK;
//            case MOVE:
//                return GOAL_TYPE.MOVE;
//            case PATROL:
//                return GOAL_TYPE.MOVE;
//            case PROTECT:
//                return GOAL_TYPE.DEFEND;
//            case PURSUIT:
//                return GOAL_TYPE.MOVE;
//            case SPECIAL:
//                return GOAL_TYPE.CUSTOM_HOSTILE;
//            case SUPPORT:
//                return GOAL_TYPE.BUFF;
//            default:
//                break;
//
//        }
//        return null;
//    }

//    private boolean checkValid(Order order) {
//        if (!order.getSequence().getNextAction().canBeActivated()) {
//            // alt action
//        }
//        if (!order.getSequence().getNextAction().canBeTargeted()) {
//            // re-path - compare again alt-sequences
//        }
//        return true;
//    }
//
//    public void giveGlobalOrders(Unit leader) {
//        String type = ListChooser.chooseEnum(GLOBAL_ORDER_TYPE.class);
//        if (type == null) {
//            type = ListChooser.chooseEnum(ORDER_TYPE.class);
//        }
//        if (type == null) {
//            return;
//        }
//        GroupAI group = leader.getUnitAI().getGroup();
//
////        group.setOrder(new Order(group, type, arg));
//        for (Unit member :
//                group.getMembers()) {
////            member.getUnitAI().setCurrentBehavior(currentBehavior);
//        }
//    }
//
//    // TODO paged panel with 3-4 order-items
//    // step-sequence: choose order type, select arg!
//
//    // as Action/Effect?
//    public void giveOrders(Unit unit, Unit leader) {
//        String type = ListChooser.chooseEnum(ORDER_TYPE.class);
//        if (type == null) {
//            return;
//        }
//        ORDER_TYPE TYPE = new EnumMaster<ORDER_TYPE>().retrieveEnumConst(ORDER_TYPE.class, type);
//
//        Condition conditions = null;
//        switch (TYPE) {
//
//        }
//        Ref ref = null;
//        new SelectiveTargeting(conditions).select(ref);
//        Order order = new Order(  TYPE, ref.getTarget() + "");
//        initOrderSequence(order);
//        unit.getUnitAI().setPlayerOrdered(true);
//
//    }
//    private void initOrderSequence(Order order) {
//        Action action = new Action(order.getAi().getUnit().getAction(order.getActionType()));
//        Task task = new Task(order.getAi(), getGoalType(order.getType()), order.getArg());
//        List<ActionSequence> sequences = getActionSequenceConstructor().getSequences(action, order
//         .getArg(), task);
//        DC_PriorityManager.setPriorities(sequences);
//        for (ActionSequence sequence : sequences) {
//            SpecialPriorities.applySpecialPriorities(sequence);
//        }
//
//        ActionSequence sequence = DC_PriorityManager.chooseByPriority(sequences);
//        order.setSequence(sequence);
//        // special prioritizing? companion ai...
//    }
}
