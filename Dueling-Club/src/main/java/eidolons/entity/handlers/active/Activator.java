package eidolons.entity.handlers.active;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.game.core.ActionInput;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.GridMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/25/2017.
 */
public class Activator extends ActiveHandler {


    private Boolean canActivate;
    private boolean broken;
    private DC_ActiveObj lastSubaction;

    public Activator(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public String getStatusString() {
        return (Bools.isTrue(canActivate)) ? "Activate " : "" +
                getAction().getCosts().getReasonsString() + " to activate ";
    }

    public boolean canBeActivated(Ref ref, boolean first) {
        if (CoreEngine.isActiveTestMode()){
            return true;
        }
        if (getGame().getCombatMaster().isActionBlocked(getAction()))
            return false;
        if (getGame().getTestMaster().isActionFree(getEntity().getName())) {
            return true;
        }  if (!getEntity().isMine()) //TODO igg demo hack
        if (!first ) {//|| broken) {
            if (canActivate != null) {

                return canActivate;
            }
        }
//     TODO req string   if (ExplorationMaster.isExplorationOn()) {
//            if (getChecker().checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.COMBAT_ONLY.toString())) {
//                return false;
//            }
//            if (getChecker().checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.COMBAT_ONLY.toString())) {
//                return false;
//            }
//        }
        if (getChecker().checkStatus(UnitEnums.STATUS.BLOCKED)) {
            return false;
        }
        // toBase();
        boolean result = false;
        try {
            getInitializer().initCosts(); // TODO ++ preCheck if there are any targets

            result = getAction().getCosts().canBePaid(ref);
            broken = false;
        } catch (Exception e) {
            if (!broken) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            broken = true;
        } finally {
            canActivate = (result);
        }
        return result;
    }


    private Boolean checkSubActionModeActivation() {
        // TODO triggered activation?
        DC_UnitAction action = getModeAction();
        if (action == null) {
            return null;
        }
        return action.canBeActivated(getRef());

    }

    public boolean canBeManuallyActivated() {
        //TODO igg demo hack critical
        if (getEntity().getOwnerUnit().getName().contains("Gorr ")) {
            if (getEntity().getOwnerUnit().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
//                GuiEventManager.trigger(GuiEventType.)
                try {
                    getGame().getLoop().actionInput(null);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
//                new ActionInput());
                return false;
            }
        }
        if (getChecker().isBlocked()) {
            return false;
        }
        if (game.isDebugMode())
            return true;
        Boolean checkSubActionMode =
                checkSubActionModeActivation();
        if (checkSubActionMode != null) {
            return checkSubActionMode;
        }
        if (getAction() instanceof DC_QuickItemAction) {
            return canBeActivated(getAction().getOwnerUnit().getRef(), true);
        }
        return canBeActivated(getRef(), true);
    }

    public void cannotActivate() {
        LogMaster.log(1, "Cannot Activate " +
                getEntity().getName() +
                ": " + getEntity().getCosts().getReasonsString());
        if (!getEntity().getOwnerUnit().isMine())
            if (getEntity().getOwnerUnit().isAiControlled())
                return;
        FloatingText f = FloatingTextMaster.getInstance().getFloatingText(getEntity(),
                TEXT_CASES.REQUIREMENT,
                getEntity().getCosts().getReasonsString());
        f.setDisplacementY(100);
        f.setDuration(3);
        Vector2 c = GridMaster.getCenteredPos(getEntity()
                .getOwnerUnit().getCoordinates());
        f.setX(c.x);
        f.setY(c.y);
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, f);
    }

    public DC_UnitAction getModeAction() {
        String mode = getAction().getOwnerUnit().getActionMode(getEntity());
        if (mode == null) {
            return null;
        }
        if (getChecker().isAttackGeneric()) {
            return (DC_UnitAction) game.getActionManager().getAction(mode, getAction().getOwnerUnit());
        }
        return (DC_UnitAction) game.getActionManager().getAction(mode + " " + getName(), getAction().getOwnerUnit());
    }


    public boolean canBeActivatedAsExtraAttack(Boolean instant_counter_opportunity) {
        getHandler().setExtraAttackMode(instant_counter_opportunity, true);
        boolean res = false;
        try {
            res = canBeActivated(getRef(), true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getHandler().setExtraAttackMode(instant_counter_opportunity, false);
        }
        return res;
    }

    public boolean canBeActivatedAsCounter() {
        return canBeActivatedAsExtraAttack(false);
    }

    public boolean canBeActivatedAsInstant() {
        return canBeActivatedAsExtraAttack(true);
    }

    public boolean canBeActivatedAsAttackOfOpportunity(boolean pending, Unit target) {
        boolean watch = getOwnerObj().getMode().equals(STD_MODES.ALERT)
                || WatchRule.checkWatched(getOwnerObj(), target);

        if (!watch) {
            if (pending) {
                return false;
            }
            return canBeActivatedAsInstant();
        }
        if (!pending) {
            if (canBeActivatedAsInstant()) {
                return true;
            }
        }

        return canBeActivatedAsExtraAttack(null);

    }

    public boolean tryOpportunityActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, null);
    }

    public boolean tryInstantActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, true);
    }

    public boolean tryCounterActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, false);
    }

    public boolean tryExtraAttackActivation(DC_ActiveObj triggeringAction,
                                            Boolean instant_counter_opportunity) {
        getHandler().setExtraAttackMode(instant_counter_opportunity, true);
        try {
            if (canBeActivated(getRef(), true)) {
                getHandler().activateOn(triggeringAction.getOwnerUnit());
                return true;
            }

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getHandler().setExtraAttackMode(instant_counter_opportunity, false);
        }
        return false;
    }

    public Boolean getCanActivate() {
        return canActivate;
    }

    public void setCanActivate(Boolean canActivate) {
        this.canActivate = canActivate;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }

    public DC_ActiveObj getLastSubaction() {
        return lastSubaction;
    }

    public void setLastSubaction(DC_ActiveObj lastSubaction) {
        this.lastSubaction = lastSubaction;
    }
}
