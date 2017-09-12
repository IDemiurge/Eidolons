package main.entity.active;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.elements.targeting.SelectiveTargeting;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.ActionInput;
import main.game.core.Eidolons;
import main.game.logic.action.context.Context;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

import java.util.List;

/**
 * Created by JustMe on 8/30/2017.
 */
public class DefaultActionHandler {
    public static boolean leftClickActor(InputEvent event, float x, float y) {

        return false;
    }

    public static boolean moveToMotion(Unit source, Coordinates coordinates) {
//        List<ActionPath> paths = source.getGame().getAiManager().getPathBuilder().getInstance(source)
//         .build(new ListMaster<Coordinates>().getList(coordinates));

        source.getGame().getMovementManager().cancelAutomove(source);
        new Thread(() -> {
            source.getGame().getMovementManager().moveTo(
             source.getGame().getCellByCoordinate(coordinates));
        }, "moveTo thread").start();
        return true;
//       return      source.getGame().getMovementManager().getAutoPath(source)!=null ;
    }

    public static boolean turnToMotion(Unit source, Coordinates coordinates) {
        List<Action> sequence =
         source.getGame().getAiManager().getTurnSequenceConstructor().
          getTurnSequence(FACING_SINGLE.IN_FRONT, source, coordinates);

        DC_ActiveObj action = sequence.get(0).getActive();
        Context context = new Context(source, null);
        return activate(context, action);
    }


    public static boolean leftClickCell(boolean turn, boolean moveTo, int gridX, int gridY) {

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

        if (turn) {
            return turnToMotion(source, c);
        }
        if (moveTo) {
            return moveToMotion(source, c);
        }
        if (c.x - source.getX() > 1) {
            return false;
        }
        if (c.y - source.getY() > 1) {
            return false;
        }

        DC_UnitAction action = getActionForCell(source, c);
        if (action == null) {
            return false;
        }
        Obj target = action.getTargeting() instanceof SelectiveTargeting ?
         Eidolons.getGame().getCellByCoordinate(c) : null;
        Context context = new Context(source, target);
        return activate(context, action);

    }

    private static boolean activate(Context context, DC_ActiveObj action) {
        if (!action.getActivator().canBeActivated(context, true)) {
            action.getActivator().cannotActivate();
            return false;
        }
        if (context.getTargetObj() != null)
            if (!action.canBeTargeted(context.getTarget()))
                return false;
        Eidolons.getGame().getGameLoop().actionInput(
         new ActionInput(action, context));
        return true;
    }

    public static DC_UnitAction getActionForCell(Unit source, Coordinates c) {
        DIRECTION d = DirectionMaster.getRelativeDirection(source.getCoordinates(),
         c);
        FACING_SINGLE f = FacingMaster.getSingleFacing(source.getFacing(), source.getCoordinates(),
         c);
        String name = null;

        switch (f) {
            case IN_FRONT:
                if (d.isDiagonal()) {
//                    target = Eidolons.getGame().getCellByCoordinate(c);
                    name =
                     "Clumsy Leap";
                    break;
                }
                name = "Move";
                break;
            case BEHIND:
                if (d.isDiagonal())
                    return null;
                name = "Move Back";
                break;
            case TO_THE_SIDE:

                boolean right =
                 !source.getFacing().isVertical() ?
                  d.isGrowY() : d.isGrowX();
                if (!source.getFacing().isVertical()) {
                    if (source.getFacing().isCloserToZero())
                        right = !right;
                } else if (!source.getFacing().isCloserToZero())
                    right = !right;
                name = right ? "Move Right" :
                 "Move Left";
                break;
            case NONE:
                break;
        }
        if (name == null) {
//            SoundController.getCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
            return null;
        }
        return source.getAction(name);
    }


    public static boolean leftClickUnit(BattleFieldObject target) {
        if (!OptionsMaster.getGameplayOptions().getBooleanValue
         (GAMEPLAY_OPTION.DEFAULT_ACTIONS))
            return false;
        Unit source = Eidolons.getGame().getManager().getActiveObj();
        DC_ActiveObj action = getPreferredAttackAction(
         source, target);
        if (action == null)
            return false;
        Context context = new Context(source, target);
        return activate(context, action);

    }

    private static DC_ActiveObj getPreferredAttackAction(Unit source, BattleFieldObject target) {
// if (offhand)
        return pickAutomatically(source.getAttack().getSubActions(), target);
    }

    //
    private static DC_ActiveObj pickAutomatically(List<DC_ActiveObj> subActions,
                                                  BattleFieldObject target) {
        DC_ActiveObj pick = null;
        int max = 0;
        for (DC_ActiveObj attack : subActions) {
            if (!attack.canBeActivated(attack.getRef(), true))
                continue;
            if (attack.canBeTargeted(target.getId()))
                continue;
            int priority =
             attack.getGame().getAiManager()
              .getPriorityManager().getAttackPriority(attack, target);
            if (priority > max) {
                pick = attack;
                max = priority;
            }
        }
        return pick;
    }
}
