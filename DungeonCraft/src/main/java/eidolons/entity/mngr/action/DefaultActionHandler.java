package eidolons.entity.mngr.action;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManagerImpl;
import eidolons.game.battlecraft.logic.battlefield.ClearshotMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExploreGameLoop;
import eidolons.game.exploration.dungeon.objects.DungeonObj;
import eidolons.game.exploration.dungeon.objects.InteractiveObj;
import eidolons.game.exploration.dungeon.objects.InteractiveObjMaster;
import main.content.enums.entity.ActionEnums;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.logic.action.context.Context;

import java.util.List;

/**
 * Created by JustMe on 8/30/2017.
 */
public class DefaultActionHandler {

    public static boolean leftClickInteractiveObj(DC_Obj userObject) {
        if (!Core.getGame().getManager().isSelecting())
            if (userObject instanceof InteractiveObj) {
                activate(new Context(Core.getMainHero(), userObject), ((InteractiveObj) userObject).getDM().getDefaultAction(
                        Core.getMainHero(), (DungeonObj) userObject));
                return true;
            }

        return false;
    }

    public static void standardAction(DC_Obj target) {
    }

    public static ActiveObj getStandardAction(Unit source) {
        return source.getAttackOfType(ActionEnums.ATTACK_TYPE.STANDARD_ATTACK);
    }

    public static boolean standardAttack( DC_Obj target,Unit src) {
        ActiveObj atk = getStandardAction(src);
        return activate(new Context(src, target), atk);
    }

    public static boolean confirmAction(DC_Obj target, ActiveObj action) {
        return EUtils.waitConfirm("Do " + action.getName() + " on " + target + " ?");
    }

    public static boolean leftClickUnit(boolean shift, boolean control, BattleFieldObject target) {
        if (shift) {
            return leftClickCell(true, false, target.getX(), target.getY());
            //turn to?
        }
        if (control) {
            return leftClickCell(false, true, target.getX(), target.getY());
            // move into it?
        }
        // if (!OptionsMaster.getGameplayOptions().getBooleanValue
        //         (GAMEPLAY_OPTION.DEFAULT_ACTIONS))
        //     return false;
        Unit source = Core.getGame().getManager().getActiveObj();

        if (source.getGame().isDebugMode()) {
            return doDebugStuff(source, target);
        }

        if (!target.getCoordinates().isAdjacent(source.getCoordinates())) {
            return false;
        }

        //        if (target.isMine())
        //            return false;
        ActiveObj action;
        String msg = null;
        if (target instanceof DungeonObj) {
            action = getDungeonObjAction(source, (DungeonObj) target);
            if (action == null)
                msg = "Cannot find default action";
        } else {
            if (standardAttack(target, source))
                return true;

            action = getPreferredAttackAction(source, target);
            if (action == null)
                msg = "Cannot find optimal attack";
        }
        if (action == null) {
            EUtils.showInfoText(msg);
            main.system.auxiliary.log.LogMaster.log(1, source + " " +
                    msg + " " + target);
            return false;
        }
        if (!action.canBeTargeted(target.getId(), false)) {
            return false;
        }
        if (!confirmAction(target, action)) {
            return false;
        }
        Context context = new Context(action.getRef());
        context.setTarget(target);
        return activate(context, action);

    }

    private static ActiveObj getDungeonObjAction(Unit source, DungeonObj target) {
        return target.getDM().getDefaultAction(source, target);
    }

    public static ActiveObj getPreferredAttackAction(Unit source, BattleFieldObject target) {
        ActiveObj action = getPreferredAttackAction(false, source, target);
        if (action == null) {
            action = getPreferredAttackAction(true, source, target);
        }
        if (action != null)
            if (action.isAttackGeneric()) {
                return null;
            }
        return action;
    }

    public static ActiveObj getPreferredAttackAction(boolean offhand, Unit source, BattleFieldObject target) {
        // if (offhand)
        return source.getAttackOfType(ActionEnums.ATTACK_TYPE.STANDARD_ATTACK);
        //TODO DC revamp

        // DC_WeaponObj w = source.getWeapon(offhand);
        // List<DC_UnitAction> atks = source.getGame().getActionManager().getOrCreateWeaponActions(w);
        // w = source.getNaturalWeapon(offhand);
        // atks.addAll(source.getGame().getActionManager().getOrCreateWeaponActions(w));
        //
        // return pickAutomatically(
        //         atks, target);
    }

    //
    private static ActiveObj pickAutomatically(List<UnitAction> subActions,
                                               BattleFieldObject target) {
        ActiveObj pick = null;
        int max = 0;
        for (ActiveObj attack : subActions) {
            if (attack.isThrow()) {
                continue;
            }
            if (attack.isRanged()) {
                if (target.getCoordinates().dst_(attack.getOwnerUnit().getCoordinates()) < 1.4f) {
                    continue;
                }
            }
            if (attack.getActiveWeapon().isNatural())
                if (attack.getOwnerUnit().getWeapon(attack.isOffhand()) != null)
                    continue;
            if (!attack.canBeActivated(attack.getRef(), true))
                continue;
            if (!attack.canBeTargeted(target.getId()))
                continue;
            DC_PriorityManager.toggleImplementation(new PriorityManagerImpl(target.getGame().getAiManager()) {
                @Override
                public Unit getUnit() {
                    return attack.getOwnerUnit();
                }
            });
            int priority = Integer.MIN_VALUE;
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


    public static boolean leftClickCell(boolean turn, boolean moveTo, int gridX, int gridY) {

        Unit source = null;
        try {
            source = Core.getGame().getManager().getActiveObj();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (source == null)
            return false;
        if (source.isAiControlled())
            return false;
        Coordinates c = Coordinates.get(gridX, gridY);

        if (moveTo) {
            if (source.getGame().isDebugMode()) {
                return doDebugStuffCell(source, c);
            }
            return moveToMotion( c);
        }

        if (c.x - source.getX() > 1) {
            return false;
        }
        if (c.y - source.getY() > 1) {
            return false;
        }
        if (c.equals(source.getCoordinates())) {
            return false;
        }

        UnitAction action = getMoveToCellAction(source, c);
        if (action == null) {
            return false;
        }
        Obj target = action.getTargeting() instanceof SelectiveTargeting ?
                Core.getGame().getCell(c) : null;
        Context context = new Context(source, target);
        return activate(context, action);

    }

    private static boolean activate(Context context, ActiveObj action) {
        if (!action.getActivator().canBeActivated(context, true)) {
            action.getActivator().cannotActivate();
            return false;
        }
        if (!action.getTargeter().canBeTargeted(context.getTarget(), false)) {
            action.getActivator().cannotActivate();
            return false;
        }
        if (context.getTargetObj() instanceof InteractiveObj) {
            if (action.getName().equalsIgnoreCase("use")) {
                if (((InteractiveObj) context.getTargetObj()).getTYPE() == InteractiveObjMaster.INTERACTIVE_OBJ_TYPE.INSCRIPTION
                        || action.getOwnerUnit().getCoordinates().dst(context.getTargetObj().getCoordinates()) <= 1) {
                    Core.onNonGdxThread(() -> Core.getGame().getGameLoop().activateAction(
                            new ActionInput(action, context)));
                    return true;
                }
            }
        }
        if (context.getTargetObj() != null)
            if (!action.canBeTargeted(context.getTarget(), false))
                return false;
        Core.getGame().getGameLoop().actionInputManual(
                new ActionInput(action, context));
        return true;
    }

    public static UnitAction getMoveToCellAction(Unit source, Coordinates c) {
        DIRECTION d = DirectionMaster.getRelativeDirection(source.getCoordinates(),
                c);
        String name= DC_MovementManager.STEP;
        if (d.isDiagonal()) {
            name= DC_MovementManager.JUMP;
        }
        if (name == null) {
            //            SoundController.getCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
            return null;
        }
        return source.getAction(name);
    }


    private static boolean doDebugStuffCell(Unit source, Coordinates c) {
        Ref ref = new Ref(source);
        ref.setMatch(source.getGame().getCell(c).getId());
        ClearshotMaster.clearCache();
        source.getGame().getVisionMaster().getSightMaster().getClearShotCondition().preCheck(ref);
        DC_Obj target = source.getGame().getCell(c);
        int g = target.getGame().getVisionMaster().getGammaMaster().getGamma(source, target);
        return false;
    }

    private static boolean doDebugStuff(Unit source, BattleFieldObject target) {
        target.getGame().getVisionMaster().getVisionController().log(source, target);
        target.getGame().getVisionMaster().getVisionController().logFor(target);

        if (target instanceof Unit) {
            ((Unit) target).getAI().getExploreAI().toggleGroupBehaviorOff();
        }
        return false;
    }

    public static boolean moveToMotion(Coordinates coordinates) {
        Unit source= Core.getMainHero();
        if (!isMoveToOn())
            return false;
        source.getGame().getMovementManager().cancelAutomove(source);
        new Thread(() -> {
            source.getGame().getMovementManager().moveTo(
                    source.getGame().getCell(coordinates));
        }, "moveTo thread").start();
        return true;
    }

    private static boolean isMoveToOn() {
        return DC_Game.game.getGameLoop() instanceof ExploreGameLoop;
    }
}
