package eidolons.entity.handlers.active;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.system.libgdx.GdxAdapter;
import main.content.enums.entity.UnitEnums;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.Flags;

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
        if (Flags.isActiveTestMode()) {
            return true;
        }
        if (getGame().getCombatMaster().isActionBlocked(getAction()))
            return false;
        if (getGame().getTestMaster().isActionFree(getEntity().getName())) {
            return true;
        }

        if (EidolonsGame.isActionBlocked(getEntity())) {
            getEntity().getCosts().setReason("Blocked");
            return false;
        }

        if (!getEntity().isMine()) //TODO EA check - no recheck for enemies?
            if (!first) {//|| broken) {
                if (canActivate != null) {

                    return canActivate;
                }
            }
        if (getChecker().checkStatus(UnitEnums.STATUS.BLOCKED)) {
            return false;
        }

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


    public boolean canBeManuallyActivated() {
        if (getChecker().isBlocked()) {
            return false;
        }
        if (game.isDebugMode())
            return true;
        if (getAction() instanceof DC_QuickItemAction) {
            return canBeActivated(getAction().getOwnerUnit().getRef(), true);
        }
        return canBeActivated(getRef(), true);
    }

    public void cannotActivate() {
        cannotActivate_(getEntity(), getEntity().getCosts().getReasonsString());
    }

    public static void cannotActivate_(DC_ActiveObj e, String reason) {
        GdxAdapter.getInstance().getEventsAdapter().cannotActivate(e, reason);

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
            if (pending) {
                return false;
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
                getHandler().activateOnGameLoopThread(triggeringAction.getOwnerUnit());
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
