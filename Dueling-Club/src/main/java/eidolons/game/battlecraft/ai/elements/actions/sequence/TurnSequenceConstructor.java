package eidolons.game.battlecraft.ai.elements.actions.sequence;

import eidolons.ability.conditions.FacingCondition;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.ClassMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class TurnSequenceConstructor extends AiHandler {
    public TurnSequenceConstructor(AiMaster master) {
        super(master);
    }

    private Action getTurnAction(boolean clockwise, Unit source) {
        DC_UnitAction specAction = source.getAction("Quick Turn "
         + (clockwise ? "Clockwise" : "Anticlockwise"));
        if (specAction != null) {
            if (specAction.canBeActivated(source.getRef(), true)) {
                return new Action(specAction, Ref.getSelfTargetingRefCopy(source));
            }
        }

        return new Action(source.getAction(""
         + ((clockwise) ? DC_ActionManager.STD_ACTIONS.Turn_Clockwise
         : DC_ActionManager.STD_ACTIONS.Turn_Anticlockwise)), Ref
         .getSelfTargetingRefCopy(source));

    }

    public List<Action> getTurnSequence(Action action) {
        Conditions conditions = (action.getTargeting().getFilter().getConditions());
        FacingCondition condition = null;
        FACING_SINGLE template = null;
        DC_Obj target = action.getTarget();
        Unit source = (Unit) action.getRef().getSourceObj();
        for (Condition c : conditions) {
            if (c instanceof FacingCondition) {
                condition = (FacingCondition) c;
                break;
            }
            List<Object> list = ClassMaster.getInstances(c, FacingCondition.class);
            if (!list.isEmpty()) {
                List<Action> front_sequence = getTurnSequence(FACING_SINGLE.IN_FRONT, source,
                 target.getCoordinates());
                List<Action> side_sequence = null;
                if (action.getSource().hasBroadReach()
                 || action.getActive().checkPassive(UnitEnums.STANDARD_PASSIVES.BROAD_REACH))
                // front_sequence.remove(front_sequence.size() - 1);
                {
                    side_sequence = getTurnSequence(FACING_SINGLE.TO_THE_SIDE, source, target
                     .getCoordinates());
                }
                List<Action> hind_sequence = null;
                if (action.getSource().hasHindReach()
                 || action.getActive().checkPassive(UnitEnums.STANDARD_PASSIVES.HIND_REACH)) {
                    hind_sequence = getTurnSequence(FACING_SINGLE.BEHIND, source, target
                     .getCoordinates());
                }

                return new ListMaster<Action>().getSmallest(front_sequence, hind_sequence,
                 side_sequence);
            }

        }
        // if (c instanceof OrConditions) {
        // if
        // (action.getActive().checkPassive(STANDARD_PASSIVES.BROAD_REACH))
        // // template = TODO
        // break;
        // }
        // }
        if (condition == null) {
            return new ArrayList<>();
        }
        if (ArrayMaster.isNotEmpty(condition.getTemplate())) {
            template = condition.getTemplate()[0];
        }
        return getTurnSequence(template, source, target.getCoordinates());

    }

    public List<Action> getTurnSequence(Unit source,
                                        Coordinates target) {
        return getTurnSequence(FACING_SINGLE.IN_FRONT, source, target);
    }

    public List<Action> getTurnSequence(FACING_SINGLE template, Unit source,
                                        Coordinates target) {

        FACING_DIRECTION original_facing = source.getFacing();
        FACING_DIRECTION facing = original_facing;

        boolean clockwise = true;
        int i = 0;
        List<Action> clockwise_list = new ArrayList<>();

        if (template == FacingMaster.getSingleFacing(FacingMaster.rotate180(facing), source
         .getCoordinates(), target)) {
            DC_UnitAction specAction = source.getAction("Turn About "
             + (RandomWizard.random() ? "anti" : "") + "clockwise");
            if (specAction != null) {
                clockwise_list.add(new Action(specAction));
                return clockwise_list;
            }
        }

        while (true) {
            if (template == FacingMaster.getSingleFacing(facing, source.getCoordinates(), target)) {
                break;
            }
            facing = FacingMaster.rotate(facing, clockwise);
            clockwise_list.add(getTurnAction(clockwise, source));
            i++;
            if (i > 2) {
                break;
            }
        }
        clockwise = false;
        i = 0;
        List<Action> anticlockwise_list = new ArrayList<>();
        facing = original_facing;
        while (true) {
            if (template == FacingMaster.getSingleFacing(facing, source.getCoordinates(), target)) {
                break;
            }
            facing = FacingMaster.rotate(facing, clockwise);
            anticlockwise_list.add(getTurnAction(clockwise, source));
            i++;
            if (i > 2) {
                break;
            }
        }
        return (anticlockwise_list.size() > clockwise_list.size()) ? clockwise_list
         : anticlockwise_list;
    }


//    @Deprecated
//    public List<Action> getTurnSequence(Unit unit, Coordinates targetCoordinates) {
//        List<Action> list = new ArrayList<>();
//        // this will only work if there are no obstacles to the sides
//        // in reality, we need to preCheck from which *empty* *adjacent* cell the
//        // enemy is closer
//        FACING_DIRECTION facing = unit.getFacing();
//        boolean clockwise = RandomWizard.random();
//        list.add(getTurnAction(clockwise, unit));
//        facing = FacingMaster.rotate(facing, clockwise);
//        // action.getActive().getTargeting().getFilter()
//        if (FacingMaster.getSingleFacing(FacingMaster.rotate(facing, clockwise), unit
//                .getCoordinates(), targetCoordinates) == UnitEnums.FACING_SINGLE.IN_FRONT) {
//            return list;
//        }
//
//        clockwise = !clockwise;
//        facing = unit.getFacing();
//        list.clear();
//
//        list.add(getTurnAction(clockwise, unit));
//        facing = FacingMaster.rotate(facing, clockwise);
//        if (FacingMaster.getSingleFacing(FacingMaster.rotate(facing, clockwise), unit
//                .getCoordinates(), targetCoordinates) == UnitEnums.FACING_SINGLE.IN_FRONT) {
//            return list;
//        }
//        list.add(getTurnAction(clockwise, unit));
//        facing = FacingMaster.rotate(facing, clockwise);
//
//        return list;
//
//    }
}
