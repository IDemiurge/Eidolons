package main.entity.active;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.battlecraft.ai.tools.priority.PriorityManagerImpl;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.ActionInput;
import main.game.core.Eidolons;
import main.game.logic.action.context.Context;
import main.game.module.dungeoncrawl.objects.DungeonObj;
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

    private static boolean moveToMotion(Unit source, Coordinates coordinates) {
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

    private static boolean turnToMotion(Unit source, Coordinates coordinates) {
        DC_ActiveObj action = getTurnToAction(source, coordinates);
        Context context = new Context(source, null);
        return activate(context, action);
    }

    public static DC_ActiveObj getTurnToAction(Unit source, Coordinates coordinates) {

        List<Action> sequence =
         source.getGame().getAiManager().getTurnSequenceConstructor().
          getTurnSequence(FACING_SINGLE.IN_FRONT, source, coordinates);
        if (sequence.isEmpty())
            return null;
        return sequence.get(0).getActive();
    }


    public static boolean leftClickCell(boolean turn, boolean moveTo, int gridX, int gridY) {

        Unit source = null;
        try {
            source = Eidolons.getGame().getManager().getActiveObj();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
        if (source.getGame().isDebugMode()) {
            return doDebugStuffCell(source, c);
        }
        if (c.x - source.getX() > 1) {
            return false;
        }
        if (c.y - source.getY() > 1) {
            return false;
        }

        DC_UnitAction action = getMoveToCellAction(source, c);
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
            if (!action.canBeTargeted(context.getTarget(), false))
                return false;
        Eidolons.getGame().getGameLoop().actionInput(
         new ActionInput(action, context));
        return true;
    }

    public static DC_UnitAction getMoveToCellAction(Unit source, Coordinates c) {
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
                  d.growY : d.growX;
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


    public static boolean leftClickUnit(boolean shift, boolean control, BattleFieldObject target) {
        if (shift) {
            return leftClickCell(true, false, target.getX(), target.getY());
        }
        if (control) {
            return leftClickCell(false, true, target.getX(), target.getY());
        }
        if (!OptionsMaster.getGameplayOptions().getBooleanValue
         (GAMEPLAY_OPTION.DEFAULT_ACTIONS))
            return false;
        Unit source = Eidolons.getGame().getManager().getActiveObj();

        if (source.getGame().isDebugMode()) {
            return doDebugStuff(source, target);
        }

        if (target.isMine())
            return false;
        DC_ActiveObj action = null;
        if (target instanceof DungeonObj)
            action = getDungeonObjAction(source, (DungeonObj) target);
        else
            action = getPreferredAttackAction(source, target);
        if (action == null)
            return false;
        Context context = new Context(source, target);
        return activate(context, action);

    }

    private static DC_ActiveObj getDungeonObjAction(Unit source, DungeonObj target) {
        return target.getDM().getDefaultAction(source, target);
    }

    public static DC_ActiveObj getPreferredAttackAction(Unit source, BattleFieldObject target) {
// if (offhand)
        DC_ActiveObj action = pickAutomatically(
         source.getAttack().getSubActions(), target);
        if (action == null)
            if (source.getOffhandAttack() != null)
                action = pickAutomatically(source.getOffhandAttack().getSubActions(), target);
        return action;
    }

    //
    private static DC_ActiveObj pickAutomatically(List<DC_ActiveObj> subActions,
                                                  BattleFieldObject target) {
        DC_ActiveObj pick = null;
        int max = 0;
        for (DC_ActiveObj attack : subActions) {
            if (attack.getActiveWeapon().isNatural())
                if (attack.getOwnerObj().getWeapon(attack.isOffhand()) != null)
                    continue;
            if (!attack.canBeActivated(attack.getRef(), true))
                continue;
            if (!attack.canBeTargeted(target.getId()))
                continue;
            DC_PriorityManager.toggleImplementation(new PriorityManagerImpl(target.getGame().getAiManager()) {
                @Override
                public Unit getUnit() {
                    return attack.getOwnerObj();
                }
            });
            int priority = 0;
            try {
                priority = DC_PriorityManager.getAttackPriority(
                 attack, target);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                DC_PriorityManager.toggleImplementation(null);
            }
            if (priority >= max) {
                pick = attack;
                max = priority;
            }
        }
        return pick;
    }

    private static boolean doDebugStuffCell(Unit source, Coordinates c) {
        Ref ref = new Ref(source);
        ref.setMatch(source.getGame().getCellByCoordinate(c).getId());
        ClearShotCondition.clearCache();
        source.getGame().getVisionMaster().getSightMaster().getClearShotCondition().preCheck(ref);
        DC_Obj target = source.getGame().getCellByCoordinate(c);
        target.getGame().getVisionMaster().getGammaMaster().clearCache();
        target.getGame().getVisionMaster().getIlluminationMaster().clearCache();
        int g = target.getGame().getVisionMaster().getGammaMaster().getGamma(true, source, target);
        return false;
    }

    private static boolean doDebugStuff(Unit source, BattleFieldObject target) {
        OUTLINE_TYPE outlineType = source.getGame().getVisionMaster().getOutlineMaster().getOutlineType(target, source);
        VISIBILITY_LEVEL vl = source.getGame().getVisionMaster().getVisibilityLevel(source, target);
        target.getPlayerVisionStatus(true);
        target.getGamma();
        source.getGame().getVisionMaster().getIlluminationMaster().getIllumination(target);

        target.getGame().getVisionMaster().getGammaMaster().clearCache();
        target.getGame().getVisionMaster().getIlluminationMaster().clearCache();
        int g = target.getGame().getVisionMaster().getGammaMaster().getGamma(true, source, target);
        outlineType = source.getGame().getVisionMaster().getOutlineMaster().getOutlineType(target, source);
        vl = source.getGame().getVisionMaster().getVisibilityLevel(source, target);
        ClearShotCondition.clearCache();
        Ref ref = new Ref(source);
        ref.setMatch(target.getId());
        source.getGame().getVisionMaster().getSightMaster().getClearShotCondition().preCheck(ref);
        return false;
    }

}
