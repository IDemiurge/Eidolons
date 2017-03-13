package main.entity.tools.active;

import main.content.PARAMS;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.game.core.Eidolons;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster;
import main.rules.RuleMaster;
import main.rules.RuleMaster.RULE_GROUP;
import main.rules.action.StackingRule;
import main.rules.attack.AttackOfOpportunityRule;
import main.rules.attack.ExtraAttacksRule;
import main.rules.combat.ChargeRule;
import main.rules.mechanics.ConcealmentRule;
import main.rules.perk.EvasionRule;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 2/21/2017.
 * <p>
 * :: Check can
 * :: Determine Target
 * :: Check interruptions
 * :: Resolve
 * :: Pay costs
 * Intersecting
 * :: Log
 * :: Communicate
 * events
 */
public class Executor extends ActiveHandler {
    boolean interrupted;
    boolean result;
    private Activator activator;
    private Targeter targeter;
    private List<DC_ActiveObj> pendingAttacksOpportunity;


    private boolean visualsRefreshed;
    private boolean failedLast;
    private boolean instantMode;
    private boolean counterMode;
    private boolean attackOfOpportunityMode;


    private boolean continuous;
    private Boolean cancelled;
    private boolean triggered;
    private boolean resistanceChecked;
    private int timeCost;


    public Executor(DC_ActiveObj active, ActiveMaster entityMaster) {
        super(active, entityMaster);
        targeter = createTargeter(active, entityMaster);
        activator = createActivator(active, entityMaster);
    }

    protected Targeter createTargeter(DC_ActiveObj active, ActiveMaster entityMaster) {
        return new Targeter(active, entityMaster);
    }

    protected Activator createActivator(DC_ActiveObj active, ActiveMaster entityMaster) {
        return new Activator(active, entityMaster);
    }

    public void activateOn(Ref ref) {
        targeter.setForcePresetTarget(true);
        getEntity().setRef(ref);
        activateOnActionThread();
    }

    public void activateOn(Obj t) {
        targeter.presetTarget = t;
        activateOnActionThread();

    }

    public void activateOnActionThread() {
        Eidolons.getActionThread().setExecutor(this);
        Eidolons.getExecutorService().execute(Eidolons.getActionThread());
    }


    public boolean activate() {

        reset();
        ActionTooltipMaster.test(getEntity());
        getTargeter().initTarget();
        if (interrupted)
            return result;
        beingActivated();
        if (interrupted)
            return result;
        activated();
        if (interrupted)
            return result;
        resolve();
        if (!BooleanMaster.isTrue(cancelled))
            payCosts();
        else {
            if (BooleanMaster.isFalse(cancelled))
                cancelled();
        }
        //TODO BEFORE RESOLVE???
        GuiEventManager.trigger(GuiEventType.ACTION_RESOLVES, new EventCallbackParam(getAction()));

        activationOver();

        return result;
    }


    private void cancelled() {
        timeCost = 0;
    }

    private void beingActivated() {
//        fireEvent(STANDARD_EVENT_TYPE.ACTION_BEING_ACTIVATED, true);
        timeCost = getCalculator().calculateTimeCost();
        if (getChecker().isCancellable()) {
//            activated(ref); TODO ???
//            if (!result) {
//                getGame().getManager().setActivatingAction(null);
//            }
        } else if (!checkExtraAttacksDoNotInterrupt(getLogger().getEntryType())) {
            // TODO NEW ENTRY AOO?
            payCosts();
            result = false;
        } else {
//            activated(ref); TODO
        }
        if (getChecker().isCancellable()) {
            result = checkExtraAttacksDoNotInterrupt(getLogger().getEntryType());
        }


        if (getChecker().isRangedTouch()) {
            boolean missed = ConcealmentRule.checkMissed(getAction());
            if (!missed) {
                missed = EvasionRule.checkMissed(getAction());
                if (missed) {
                    EvasionRule.logDodged(game.getLogManager(), getAction());
                }
            } else {
                ConcealmentRule.logMissed(game.getLogManager(), getAction());
            }
            if (missed) {
                result = false;
                interrupted = true;
                StackingRule.actionMissed(getAction());
            }
        }
        if (getGame().getRules().getEngagedRule().checkDisengagingActionCancelled(getAction())) {
            // return false; TODO
        }
    }


    private void activated() {
        fireEvent(STANDARD_EVENT_TYPE.ACTION_ACTIVATED, true);
        ownerObj.getRef().setID(KEYS.ACTIVE, getId());
        triggered = getRef().isTriggered();
        getCalculator().calculateTimeCost();
        getInitializer().construct();
        getTargeter().initAutoTargeting();
        getMaster().getAnimator().initAnimation();
        getMaster().getInitializer().initCosts(true);// for animation phase

    }


    private void resolve() {
        addStdPassives();
        getAction().activatePassives();
//        setResistanceChecked(false); ??

        GuiEventManager.trigger(GuiEventType.ACTION_BEING_RESOLVED,
                new EventCallbackParam(getAction()));
        getMaster().getAnimator().addResolvesPhase();

        if (getAction().getAbilities() != null) {
            try {
                result = getAction().getAbilities().activatedOn(getRef());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
        // for Multi-targeting when single-wrapped Abilities cannot be used
        {
            for (Active active : getAction().getActives()) {
                try {
                    result &= active.activatedOn(getRef());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!result) {
                    break;// TODO if cancelled!
                }
                if (!game.isStarted()) {
                    continue;
                }
                // cancelled = null; TODO ???
                if (getAction().getActives().size() > 1) { // between
                    game.getManager().reset();
                    refreshVisuals();
                }
            }
        }

//        SoundMaster.playEffectSound(SOUNDS.IMPACT, this); //TODO queue on anim!

    }

    private void refreshVisuals() {
        game.getManager().refresh(true);
        visualsRefreshed = true;
    }

    private void reset() {
        interrupted = false;
        result = false;
    }


    private void addStdPassives() {
        if (!StringMaster.isEmpty(getEntity().getProperty(G_PROPS.STANDARD_PASSIVES))) {
            ownerObj.addProperty(G_PROPS.STANDARD_PASSIVES, getAction().getProperty(G_PROPS.STANDARD_PASSIVES));
        }
        if (!StringMaster.isEmpty(getAction().getProperty(PROPS.STANDARD_ACTION_PASSIVES))) {
            ownerObj.addProperty(G_PROPS.STANDARD_PASSIVES,
                    getAction().getProperty(PROPS.STANDARD_ACTION_PASSIVES));
        }
    }

    public void payCosts() {
        if (getAction().isFree()) {
            return;
        }
        addCooldown();

        try {
            getAction().getCosts().pay(getRef());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addCooldown() {
        Integer cooldown = getAction().getIntParam(PARAMS.COOLDOWN);
        if (cooldown <= 0) {
            getAction().modifyParameter(PARAMS.C_COOLDOWN, 1);
        } else {
            getAction().setParam(PARAMS.C_COOLDOWN, cooldown);
        }
    }

    private void fireEvent(STANDARD_EVENT_TYPE type, boolean interrupting) {
        boolean result = getGame().fireEvent(new Event(type, getRef()));
        if (interrupting)
            interrupted = !result;
//        if (cancel)
//            this.result=result;
    }

    public void activationOver() {
        getGame().getManager().setActivatingAction(null);
//        if (result) TODO !!! always!
            actionComplete();
    }

    public void actionComplete() {
        fireEvent(STANDARD_EVENT_TYPE.ACTION_COMPLETE, false);
        getMaster().getLogger().logCompletion();
        getGame().getManager().applyActionRules(getAction());
        try {
            checkPendingAttacksOfOpportunity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Boolean endTurn = getGame().getRules().getTimeRule().actionComplete(getAction(), timeCost);
        if (!endTurn) {
            game.getManager().reset();
            if (ChargeRule.checkRetainUnitTurn(getAction())) {
                endTurn = null;
            }
        }
        if (endTurn != null) {
            endTurn = !endTurn;
        }
        getAnimator().waitForAnimation();
        getGame().getManager().unitActionCompleted(getAction(), endTurn);

    }

    public Activator getActivator() {
        return activator;
    }

    public Targeter getTargeter() {
        return targeter;
    }

//    public boolean activateChanneling() {
//        initCosts();
//        initChannelingCosts();
//        game.getLogManager().log(">> " + ownerObj.getName() + " has begun Channeling " + getName());
//        boolean result = (checkExtraAttacksDoNotInterrupt(ENTRY_TYPE.ACTION));
//        if (result) {
//            this.channeling = true;
//            ChannelingRule.playChannelingSound(this, HeroAnalyzer.isFemale(ownerObj));
//            result = ChannelingRule.activateChanneing(this);
//        }
//
//        channelingActivateCosts.pay(ref);
//        actionComplete();
//        return result;
//    }


    public void addPendingAttackOpportunity(DC_ActiveObj attack) {
        getPendingAttacksOpportunity().add(attack);
    }

    private List<DC_ActiveObj> getPendingAttacksOpportunity() {
        if (pendingAttacksOpportunity == null) {
            pendingAttacksOpportunity = new LinkedList<>();
        }
        return pendingAttacksOpportunity;
    }

    private void checkPendingAttacksOfOpportunity() {
        for (DC_ActiveObj attack : new LinkedList<>(getPendingAttacksOpportunity())) {
            if (!AttackOfOpportunityRule.checkPendingAttackProceeds(ownerObj, attack)) {
                continue;
            }
            getPendingAttacksOpportunity().remove(attack);
            Ref REF = Ref.getCopy(attack.getRef());
            REF.setTarget(ownerObj.getId());
            attack.activatedOn(REF);
        }

    }

    protected boolean checkExtraAttacksDoNotInterrupt(ENTRY_TYPE entryType) {
        if (RuleMaster.checkRuleGroupIsOn(RULE_GROUP.EXTRA_ATTACKS)) {
            return !ExtraAttacksRule.checkInterrupted(getAction(), entryType);
        }
        return true;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public boolean isFailedLast() {
        return failedLast;
    }

    public void setFailedLast(boolean failedLast) {
        this.failedLast = failedLast;
    }

    public boolean isInstantMode() {
        return instantMode;
    }

    public void setInstantMode(boolean instantMode) {
        this.instantMode = instantMode;
    }

    public boolean isCounterMode() {
        return counterMode;
    }

    public void setCounterMode(boolean counterMode) {
        this.counterMode = counterMode;
    }

    public boolean isAttackOfOpportunityMode() {
        return attackOfOpportunityMode;
    }

    public void setAttackOfOpportunityMode(boolean attackOfOpportunityMode) {
        this.attackOfOpportunityMode = attackOfOpportunityMode;
    }

    public int getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(int timeCost) {
        this.timeCost = timeCost;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public void setExtraAttackMode(Boolean instant_counter_opportunity, boolean b) {
        if (instant_counter_opportunity == null)
            setAttackOfOpportunityMode(b);
        else if (instant_counter_opportunity)
            setInstantMode(b);
        else
            setCounterMode(b);
    }

    public boolean isExtraAttackMode() {
        if (isAttackOfOpportunityMode()) return true;
        if (isCounterMode()) return true;
        if (isInstantMode()) return true;
        return false;
    }
}
