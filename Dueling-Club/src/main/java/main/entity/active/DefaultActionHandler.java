package main.entity.active;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.ActionInput;
import main.game.core.Eidolons;
import main.game.logic.action.context.Context;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 8/30/2017.
 */
public class DefaultActionHandler {
    public static boolean leftClickActor(InputEvent event, float x, float y) {

        return false;
    }

    public static boolean leftClickCell(InputEvent event, int gridX, int gridY) {
        Unit source = null;
        try {
            source = Eidolons.getGame().getManager().getActiveObj();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (source == null)
            return false;
        if (source.isAiControlled())
            return false;
        Coordinates c = new Coordinates(gridX, gridY);
        if (c.x - source.getX()>1){
            return false;
        }
        if (c.y - source.getY()>1){
            return false;
        }
        DIRECTION d = DirectionMaster.getRelativeDirection(source.getCoordinates(),
         c);
        FACING_SINGLE f = FacingMaster.getSingleFacing(source.getFacing(), source.getCoordinates(),
         c);
        String name = null;
        Obj target = null;


        switch (f) {
            case IN_FRONT:
                if (d.isDiagonal()) {
                    target = Eidolons.getGame().getCellByCoordinate(c);
                    name =
                     "Clumsy Leap";
                    break;
                }
                name = "Move";
                break;
            case BEHIND:
                name = "Move Back";
                break;
            case TO_THE_SIDE:

                boolean right =
                 source.getFacing().isVertical() ?
                  d.isGrowX() : d.isGrowY();
                name =  right ? "Move Right" :
                 "Move Left";
                break;
            case NONE:
                break;
        }
        if (name == null) {
//            SoundController.getCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
            return false;
        }
        Context context = new Context(source, target);
        DC_UnitAction action = source.getAction(name);
        if (!action.canBeActivated(context, true))
            return false;
        if (target != null)
            if (!action.canBeTargeted(target.getId()))
                return false;

        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
         new ActionInput(action, context));
        return true;
    }

    public static void leftClickUnit(BattleFieldObject target) {
        if (OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.DEFAULT_ACTIONS)) {
            Unit source = Eidolons.getGame().getManager().getActiveObj();
            DC_ActiveObj action = getPreferredAttackAction(

             source, target);

        }
    }

    private static DC_ActiveObj getPreferredAttackAction(Unit source, BattleFieldObject target) {
//    source.getAttack()

        return null;
    }
//
//    private DC_ActiveObj pickAttack() {
//        List<DC_ActiveObj> subActions = new LinkedList<>();
//        for (DC_ActiveObj attack : getActiveObj().getSubActions()) {
//            if (attack.canBeActivated(ref, true)) {
//                if (attack.canBeTargeted(target)) {
//                    subActions.add(attack);
//                }
//            }
//        } if (subActions.size() == 1) {
//            return subActions.get(0);
//        }
//
//        private DC_ActiveObj pickAutomatically(List<DC_ActiveObj> subActions) {
//            DC_ActiveObj pick = null;
//            int max = 0;
//
//            for (DC_ActiveObj attack : subActions) {
//                int priority = calculatePriority(attack, getTarget());
//                if (priority > max) {
//                    pick = attack;
//                    max = priority;
//                }
//            }
//            return pick;
//        }
}
