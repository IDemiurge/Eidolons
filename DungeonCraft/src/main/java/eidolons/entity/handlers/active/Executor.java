package eidolons.entity.handlers.active;

import eidolons.content.PROPS;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.active.QuickItemAction;
import eidolons.entity.item.QuickItem;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.ExtraAttacksRule;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.atb.AtbMaster;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.datasource.AnimContext;
import main.ability.Ability;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.FileLogger;
import main.system.auxiliary.log.SpecialLogger;
import main.system.auxiliary.secondary.Bools;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;

import static main.system.text.LogManager.LOGGING_DETAIL_LEVEL.FULL;

//REVIEW 40 public methods and not a single one (or did I miss something?) has comments...
//First of all - what is the purpose of the whole class? in plain english please.

/**
 * Created by JustMe on 2/21/2017.
 * <portrait>
 * :: Check can :: Determine Target :: Check interruptions :: Resolve :: Pay costs Intersecting :: Log :: Communicate
 * events
 */
public class Executor extends ActiveHandler {
    protected boolean result;
    private boolean interrupted;
    private final Activator activator;
    private final Targeter targeter;
    private List<ActiveObj> pendingAttacksOpportunity;
    private boolean failedLast;
    private boolean instantMode;
    private boolean counterMode;
    private boolean attackOfOpportunityMode;
    private Boolean cancelled;
    private boolean triggered;

    public Executor(ActiveObj active, ActiveMaster entityMaster) {
        super(active, entityMaster);
        targeter = createTargeter(active, entityMaster);
        activator = createActivator(active, entityMaster);
    }

    protected Targeter createTargeter(ActiveObj active, ActiveMaster entityMaster) {
        return new Targeter(active, entityMaster);
    }

    protected Activator createActivator(ActiveObj active, ActiveMaster entityMaster) {
        return new Activator(active, entityMaster);
    }

    public Boolean activateOn(Context context) {
        if (context.getTargetObj() != null || context.getGroup() != null || context.isTriggered()) {
            //            Ref ref = getAction().getRef();
            //            ref.setTarget(context.getTarget());
            targeter.setForcePresetTarget(true);
            targeter.setPresetTarget(context.getTargetObj());
            getAction().setTargetObj(context.getTargetObj());
            getAction().setTargetGroup(context.getGroup());
        } else
            targeter.setForcePresetTarget(false);
        activate();
        return !Bools.isTrue(isCancelled());
    }

    public void activateOnGameLoopThread(Ref ref) {
        targeter.setForcePresetTarget(true);
        Core.getGame().getGameLoop().actionInputManual(
                new ActionInput(getAction(), new Context(ref)));
    }

    public void activateOnGameLoopThread(DC_Obj t) {
        if (Thread.currentThread() == getGame().getGameLoopThread()) {
            // for triggered activation, e.g. Extra Attacks
            targeter.setPresetTarget(t);
            activate();
            return;
        }
        Core.getGame().getGameLoop().actionInputManual(
                new ActionInput(getAction(), t));
    }

    public void activateOnGameLoopThread() {
        Core.getGame().getGameLoop().actionInputManual(
                new ActionInput(getAction(), new Context(getAction().getOwnerObj().getRef())));
    }

    public boolean activate() {
        reset();
        syncActionRefWithSource();

        GuiEventManager.trigger(GuiEventType.ACTION_BEING_ACTIVATED, getAction());

        getTargeter().initTarget();
        if ((isCancelled()) != null) {
            cancelled();
            return false;
        }
        if (isInterrupted()) {
            return interrupted();
        }
        Obj target = getAction().getTargetObj();

        boolean gameLog = getAction().getLogger().isActivationLogged();
        String targets = " ";
        if (getAction().getLogger().isTargetLogged())
            if (target != null) {
                target.getRef().setObj(KEYS.HOSTILITY, getEntity());
                if (game.isDebugMode())
                    targets = " on " + getAction().getTargetObj().getNameAndCoordinate();
                else
                    targets = " on " + getAction().getTargetObj().getNameIfKnown();
            } else if (getAction().getTargetGroup() != null) {
                targets = " on " + getAction().getTargetGroup().toString();
                getAction().getTargetGroup().getObjects().forEach(t -> t.getRef().setObj(KEYS.HOSTILITY, getEntity()));
            }
        log(getAction().getOwnerObj().getNameAndCoordinate() + " activates "
                + getAction().getName() + targets, false);
        if (gameLog)
            log(getAction().getOwnerObj().getNameIfKnown() + " activates "
                    + getAction().getNameIfKnown() + targets, true);

        beingActivated();
        if (isInterrupted()) {
            return interrupted();
        }
        initActivation();
        if (isInterrupted()) {
            return interrupted();
        }
        Context animContext = new AnimContext(getAction().getOwnerObj(), target);
        animContext.setAnimationActive(getAction());
        animContext.setObj(KEYS.ACTIVE, getAction());
        ActionInput input = new ActionInput(getAction(), animContext);
        resolve(input);
        if (!Bools.isTrue(cancelled)) {
            if (getChecker().isCancellable()) {
                setResult(checkExtraAttacksDoNotInterrupt(getLogger().getEntryType()));
            }
            payCosts();
        } else {
            cancelled();
            EUtils.showInfoText(getEntity().getName() + " cancelled");
            return false;
        }

        getEntity().getOwnerUnit().setLastAction(getEntity());
        actionComplete();
        return isResult();
    }

    @Override
    protected void log(String string, boolean gameLog) {
        FileLogManager.streamAction(string);
        if (!ExplorationMaster.isExplorationOn()) {
            super.log(string, gameLog);
            if (!gameLog) {
                SpecialLogger.getInstance().appendAnalyticsLog(FileLogger.SPECIAL_LOG.COMBAT, string);
            }
        }
    }

    protected void syncActionRefWithSource() {
        if (getAction() instanceof QuickItemAction) {
            QuickItem item = ((QuickItemAction) getAction()).getItem();
            if (item.isAmmo()) {
                getAction().getOwnerObj().getRef().setID(KEYS.AMMO, item.getId());
            }
        }
        //TODO  quickfix, replace with IdKey resolving
        if (getAction().getRef().getTargetObj() == null) {
            if (getAction().getOwnerObj().getRef().getTargetObj() != null) {
                getAction().getRef().setTarget(getAction().getOwnerObj().getRef().getTarget());
            }
        }


        getAction().setRef(getAction().getOwnerObj().getRef());

    }

    private boolean interrupted() {
        log(getAction().getNameAndCoordinate() + " is interrupted", false);
        setResult(false);
        actionComplete();
        return false;
    }

    private void cancelled() {
    }

    private void beingActivated() {
        fireEvent(STANDARD_EVENT_TYPE.ACTION_BEING_ACTIVATED, true);
        if (!getChecker().isCancellable())
            if (!checkExtraAttacksDoNotInterrupt(getLogger().getEntryType())) {
                payCosts();
                setResult(false);
                setInterrupted(true);
            }
    }

    private void initActivation() {
        fireEvent(STANDARD_EVENT_TYPE.ACTION_ACTIVATED, true);
        getAction().getOwnerObj().getRef().setID(KEYS.ACTIVE, getId());
        triggered = getRef().isTriggered();
        getInitializer().construct();
        getTargeter().initAutoTargeting();
        getMaster().getInitializer().initCosts(true);// for animation phase

    }

    protected void resolve(ActionInput input) {
        log(getAction() + " resolves", false);
        addStdPassives();
        getAction().activatePassives();

        GuiEventManager.trigger(GuiEventType.ACTION_BEING_RESOLVED, getAction());
        getAction().initAnimRefs(getRef());
        //TODO gdx sync - did it ever do anything?
        if (!input.getAction().isAttackAny())
            GdxAdapter.getInstance().getAnims().actionResolves(input);

        if (getAction().getAbilities() != null) {
            try {
                setResult(getAction().getAbilities().activatedOn(getRef()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        ///////////////////
        // for Multi-targeting when single-wrapped Abilities cannot be used
        if (getAction().isStandardAttack()
                //if (getAction().isRanged() //TODO broke on aimed shot eh?
                || getAction().getAbilities() == null)
            if (!isResult() && getAction().getActives() != null) {
                result = true;
                for (Active active : getAction().getActives()) {
                    ((Ability) active).setForcePresetTargeting(true);
                    //TODO DC Review
                    try {
                        setResult(isResult() & active.activatedOn(getRef()));
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    if (!isResult()) {
                        break; //if cancelled!
                    }
                    if (!game.isStarted()) {
                        continue;
                    }
                    if (getAction().getActives().size() > 1) { // between
                        game.getManager().reset();
                        refreshVisuals();
                    }
                }
            }

    }

    private void refreshVisuals() {
        game.getManager().refresh(true);
        boolean visualsRefreshed = true;
    }

    private void reset() {
        setInterrupted(false);
        setResult(false);
        setCancelled(null);
    }

    private void addStdPassives() {
        if (!StringMaster.isEmpty(getAction().getProperty(G_PROPS.STANDARD_PASSIVES))) {
            getAction().getOwnerObj().addProperty(G_PROPS.STANDARD_PASSIVES, getAction().getProperty(G_PROPS.STANDARD_PASSIVES));
        }
        if (!StringMaster.isEmpty(getAction().getProperty(PROPS.STANDARD_ACTION_PASSIVES))) {
            getAction().getOwnerObj().addProperty(G_PROPS.STANDARD_PASSIVES,
                    getAction().getProperty(PROPS.STANDARD_ACTION_PASSIVES));
        }
    }

    public void payCosts() {
        if (getAction().isFree()) {
            return;
        }
        if (ExplorationMaster.isExplorationOn()) {
            getGame().getDungeonMaster().getExplorationMaster().getActionHandler().payCosts(getEntity());
        } else {
            reduceAtbReadiness();
        }

        try {
            getAction().getCosts().pay(getRef());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private void reduceAtbReadiness() {
        long initiativeCost = Math.round(
                -AtbMaster.reduceReadiness(getAction()));

        getGame().getLogManager().log(FULL, StringMaster.getPossessive(getOwnerObj().getName()) + " readiness is reduced by " +
                -initiativeCost +
                "%, now at " + AtbMaster.getDisplayedAtb(getOwnerObj()) +
                "%");

    }

    private void fireEvent(STANDARD_EVENT_TYPE type, boolean interrupting) {
        boolean result = getGame().fireEvent(new Event(type, getRef()));
        if (interrupting) {
            setInterrupted(!result);
        }
        //        if (cancel)
        //            this.result=result;
    }


    public void actionComplete() {
        getGame().getManager().setActivatingAction(null);
        if (isResult()) {
            log(getAction() + " done", false);
        } else {
            log(getAction() + " failed", false);
        }

        fireEvent(STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, false);
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, result);
        try {
            getMaster().getLogger().logCompletion();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        getGame().getManager().applyActionRules(getAction());
        if (isResult())
            try {
                checkPendingAttacksOfOpportunity();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        getAction().setTargetGroup(null);
        getAction().setTargetObj(null);

    }

    public Activator getActivator() {
        return activator;
    }

    public Targeter getTargeter() {
        return targeter;
    }

    public void addPendingAttackOpportunity(ActiveObj attack) {
        getPendingAttacksOpportunity().add(attack);
    }

    private List<ActiveObj> getPendingAttacksOpportunity() {
        if (pendingAttacksOpportunity == null) {
            pendingAttacksOpportunity = new ArrayList<>();
        }
        return pendingAttacksOpportunity;
    }

    //TODO NF Rules revamp
    private void checkPendingAttacksOfOpportunity() {
        if (getAction().getOwnerUnit() == null) {
            return; //objects...
        }
        for (ActiveObj attack : new ArrayList<>(getPendingAttacksOpportunity())) {
            // if (!AttackOfOpportunityRule.checkPendingAttackProceeds(getAction().getOwnerUnit(), attack)) {
            //     continue;
            // }
            getPendingAttacksOpportunity().remove(attack);
            Ref REF = Ref.getCopy(attack.getRef());
            REF.setTarget(getAction().getOwnerObj().getId());
            attack.activatedOn(REF);
        }
    }

    protected boolean checkExtraAttacksDoNotInterrupt(ENTRY_TYPE entryType) {
        if (ExplorationMaster.isExplorationOn()) {
            return true;
        }
        if (RuleKeeper.checkRuleGroupIsOn(RuleEnums.RULE_GROUP.EXTRA_ATTACKS)) {
            try {
                return !ExtraAttacksRule.checkInterrupted(getAction(), entryType);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
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

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public void setExtraAttackMode(Boolean instant_counter_opportunity, boolean b) {
        if (instant_counter_opportunity == null) {
            setAttackOfOpportunityMode(b);
        } else if (instant_counter_opportunity) {
            setInstantMode(b);
        } else {
            setCounterMode(b);
        }
    }

    public boolean isExtraAttackMode() {
        if (isAttackOfOpportunityMode()) {
            return true;
        }
        if (isCounterMode()) {
            return true;
        }
        return isInstantMode();
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
