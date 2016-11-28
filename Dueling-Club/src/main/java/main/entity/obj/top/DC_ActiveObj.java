package main.entity.obj.top;

import main.ability.Abilities;
import main.ability.ActivesConstructor;
import main.ability.DC_CostsFactory;
import main.ability.Interruptable;
import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect.SPELL_MANIPULATION;
import main.ability.effects.containers.customtarget.ShapeEffect;
import main.ability.effects.containers.customtarget.ZoneEffect;
import main.ability.effects.special.ManipulateSpellEffect;
import main.client.cc.logic.HeroAnalyzer;
import main.content.CONTENT_CONSTS.*;
import main.content.DC_ContentManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.STD_MODES;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.elements.conditions.Condition;
import main.elements.costs.Cost;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.MultiTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.*;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.VisionManager;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.player.Player;
import main.rules.DC_ActionManager;
import main.rules.mechanics.*;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.rules.mechanics.RuleMaster.RULE_GROUP;
import main.swing.generic.components.list.ListItem;
import main.system.CustomValueManager;
import main.system.ai.logic.actions.ActionManager;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.graphics.*;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;
import main.system.threading.WaitMaster;

import java.util.*;

public abstract class DC_ActiveObj extends DC_Obj implements ActiveObj, Interruptable,
        SpriteAnimated, AttachedObj {
    protected static final String BLOCKED = STATUS.BLOCKED.name();
    protected static final String CANNOT_ACTIVATE = "Cannot activate";
    private static final String[] ANIMATION_EXCEPTIONS = {"Turn Clockwise", "Move",
            "Turn Anticlockwise",};
    protected Targeting targeting;
    protected Costs costs = new Costs(new LinkedList<Cost>());
    protected DC_HeroObj ownerObj;
    protected DamageSprite damageSprite;
    protected boolean highlighted;
    protected boolean interrupted;
    protected boolean free = false;
    protected boolean quietMode = false;
    protected boolean concurrentMode = false;
    protected String focReqMod = "0";
    protected Abilities abilities;
    protected Map<PARAMETER, String> costMods = new HashMap<PARAMETER, String>();
    protected int timeCost = 0;
    protected boolean channeling;
    protected Costs channelingActivateCosts;
    protected Costs channelingResolveCosts;
    protected List<DC_ActiveObj> subActions;
    Map<Coordinates, Map<FACING_DIRECTION, Boolean>> targetingAnyCache;
    Map<Coordinates, Map<FACING_DIRECTION, Map<Integer, Boolean>>> targetingCache;
    private ListItem listItem;
    private Boolean canActivate = null;
    private RESISTANCE_TYPE resistType;
    private DAMAGE_TYPE energyType;
    private ACTION_TYPE_GROUPS actionTypeGroup;
    private TARGETING_MODE targetingMode;
    private boolean continuous;
    private Boolean cancelled;
    private boolean triggered;
    private boolean resistanceChecked;
    private boolean broken;
    private boolean effectSoundPlayed;
    private boolean forcePresetTarget;
    private AI_LOGIC aiLogic;
    private boolean refreshed;
    private Set<Integer> targetPool;
    private boolean subActionsDisplayed;
    private Boolean zone;
    private Boolean missile;
    private boolean instantMode;
    private boolean counterMode;
    private boolean attackOfOpportunityMode;
    private DC_ActiveObj lastSubaction;
    private List<DC_ActiveObj> pendingAttacksOpportunity;
    private DC_ActiveObj parentAction;
    private boolean costAnimAdded;
    private String animationKey;
    private LogEntryNode entry;
    private String customTooltip;
    private boolean switchOn = false;

    public DC_ActiveObj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
        setRef(ref);
        this.ownerObj = (DC_HeroObj) ref.getSourceObj();
    }

    public static void waitForAnimation(Animation anim) {
        if (anim != null)
            if (anim.isStarted())
                while (!anim.isFinished()) { // TODO limit?
                    WaitMaster.WAIT(80);
                }
    }

    @Override
    public String getToolTip() {
        return getStatusString() + getName();
    }

    private String getStatusString() {
        return (getCanActivate()) ? "Activate " : "" + costs.getReasonsString() + " to activate ";
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void construct() {
        if (!isConstructed()) {
            try {
                super.construct();
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }
            initTargetingMode();
        }
        // ActivesConstructor.setAnimForEffects(this); now in initAnimation()
    }

    protected void initTargetingMode() {
        if (targetingMode == null)
            targetingMode = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(
                    TARGETING_MODE.class, getType().getProperty(G_PROPS.TARGETING_MODE));

        if (targetingMode == null)
            targetingMode = TARGETING_MODE.MULTI;
        ActivesConstructor.constructActive(targetingMode, this);
        if (targeting == null) {
            main.system.auxiliary.LogMaster.log(LOG_CHANNELS.CONSTRUCTION_DEBUG,
                    "null targeting for " + getName() + targetingMode + abilities);
            // ActivesConstructor.constructActive(mode, this);
        }
    }

    public boolean isCounterMode() {
        return counterMode;
    }

    public void setCounterMode(boolean b) {
        this.counterMode = b;
    }

    public boolean canBeActivatedAsExtraAttack(Boolean instant_counter_opportunity) {
        setExtraAttackMode(instant_counter_opportunity, true);
        boolean res = false;
        try {
            res = canBeActivated(ref, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setExtraAttackMode(instant_counter_opportunity, false);
        }
        return res;
    }

    public boolean canBeActivatedAsCounter() {
        return canBeActivatedAsExtraAttack(false);
    }

    public boolean canBeActivatedAsInstant() {
        return canBeActivatedAsExtraAttack(true);
    }

    public boolean canBeActivatedAsAttackOfOpportunity(boolean pending, DC_HeroObj target) {
        boolean watch = getOwnerObj().getMode().equals(STD_MODES.ALERT)
                || WatchRule.checkWatched(getOwnerObj(), target);

        if (!watch) {
            if (pending)
                return false;
            return canBeActivatedAsInstant();
        }
        if (!pending)
            if (canBeActivatedAsInstant())
                return true;

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
        setExtraAttackMode(instant_counter_opportunity, true);
        try {
            if (canBeActivated(ref, true)) {
                ref.setTarget(triggeringAction.getOwnerObj().getId());
                activate(ref);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setExtraAttackMode(instant_counter_opportunity, false);
        }
        return false;
    }

    private void setExtraAttackMode(Boolean instant_counter_opportunity, boolean b) {
        if (instant_counter_opportunity == null)
            setAttackOfOpportunityMode(b);
        else if (instant_counter_opportunity)
            setInstantMode(b);
        else
            setCounterMode(b);

    }

    public boolean isInstantMode() {
        return instantMode;
    }

    public void setInstantMode(boolean b) {
        this.instantMode = b;

    }

    /**
     * default self-activation
     */
    public boolean activate() {
        return activate(true);
    }

    public boolean activate(boolean transmit) {
        // setRef(ownerObj.getRef()); breaks ammo for quick item active
        // ownerObj.getRef().setID(KEYS.ACTIVE, getId());

        if (transmit) {
            getGame().getBattleField().getBuilder().toggleDisplayActionModePanel(this, true);
            construct();
            highlight();
        }
        boolean result = true;
        if (!transmit)
            result = activate(ref);
        else if (targeting == null) {
            try {
                result = activate(ref);
                if (!isCancelled())
                    communicate(ref);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            synchronized (ref) { // ?
                if (selectTarget(ref)) {
                    try {
                        result = activate(ref);
                        if (!BooleanMaster.isTrue(isCancelled()))
                            communicate(ref);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getGame().getManager().setActivatingAction(null);
                    return false;
                }
            }
        }

        getGame().getManager().applyActionRules(this);
        if (!refreshed)
            refreshVisuals();
        if (!isTriggered())
            actionComplete();
        if (isAttackAny()) {
            Obj targetObj = ref.getTargetObj();
            if (targetObj == null)
                if (lastSubaction != null)
                    targetObj = lastSubaction.getRef().getTargetObj();
            if (targetObj == null)
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, ownerObj.getNameIfKnown());
            else
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, ownerObj.getNameIfKnown(),
                        // lastSubaction.getName()
                        targetObj.getNameIfKnown());
        } else {
            game.getLogManager().doneLogEntryNode();
            if (getEntry() != null)
                getEntry().setLinkedAnimation(getAnimation());
        }
        return result;
    }

    private void refreshVisuals() {
        refreshVisuals(true);
    }

    private void refreshVisuals(boolean allowCameraPanning) {
        if (getAnimation() != null)
            getGame().getAnimationManager().updatePoints();
        game.getManager().refresh(ownerObj.getOwner().isMe());
    }

    public boolean isTriggered() {
        return triggered;
    }

    protected void highlightOff() {
        setHighlighted(false);
    }

    protected void highlight() {
        setHighlighted(true);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    @Override
    public boolean activate(Ref ref) {

        ownerObj.getRef().setID(KEYS.ACTIVE, getId());

        triggered = ref.isTriggered();
        setEffectSoundPlayed(false);
        cancelled = false;
        refreshed = false;
        setCostAnimAdded(false);
        initTimeCost();

        if (!isConstructed())
            construct();

        if (targeting instanceof AutoTargeting) {
            selectTarget(ref);
        }

        setRef(ref);

        boolean result = false;

        if (getGame().getRules().getEngagedRule().checkDisengagingActionCancelled(this)) {
            // return false; TODO
        }

        // TODO *player's* detection, not AI's!
        String string = ownerObj.getNameIfKnown() + " is activating " + getDisplayedName();
        LogMaster.gameInfo(StringMaster.getStringXTimes(80 - string.length(), ">") + string);

        boolean logAction = ownerObj.getVisibilityLevel() == VISIBILITY_LEVEL.CLEAR_SIGHT
                && !isAttackAny();
        entry = null;
        ENTRY_TYPE entryType = ENTRY_TYPE.ACTION;
        if (getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
            entryType = ENTRY_TYPE.MOVE;
            logAction = true;
        }
        if (!isAttackAny()) {
            entry = game.getLogManager().newLogEntryNode(entryType, getOwnerObj(), this);
        }

        if (logAction)
            game.getLogManager().log(">> " + string);
        else if (VisionManager.checkVisible(ownerObj) && !isAttackAny()) {
            String text = " performs an action... ";
            game.getLogManager().log(">> " + ownerObj.getNameIfKnown() + text);
        }

        if (isCancellable()) {
            result = activated(ref, result, string, logAction);
            if (!result)
                getGame().getManager().setActivatingAction(null);
        } else if (!checkExtraAttacksDoNotInterrupt(entryType)) {
            // TODO NEW ENTRY AOO?
            payCosts();
            result = false;
        } else
            result = activated(ref, result, string, logAction);
        if (isCancellable()) {
            result = checkExtraAttacksDoNotInterrupt(entryType);
        }
        return result;
    }

    public boolean isAttackAny() {
        return getActionGroup() == ACTION_TYPE_GROUPS.ATTACK || isAttack() || isStandardAttack();
    }

    public boolean isCancellable() {
        return checkBool(STD_BOOLS.CANCELLABLE);
    }

    public boolean isCancelDefault() {
        if (isAttack())
            return true;
        return checkBool(STD_BOOLS.CANCEL_FOR_FALSE);
    }

    private boolean activated(Ref ref, boolean result, String string, boolean logAction) {
        // if (!isStandardAttack())
        // animation = (ActionAnimation)
        // getGame().getAnimationManager().getAnimation(
        // getAnimationKey());
        // TODO could be activated w/o generic Attack!!!
        // else

        initAnimation();
        initCosts(true);// for animation phase
        getGame().getAnimationManager().newAnimation(getAnimation());
        if (getParentAction() != null) // TODO ?
            getParentAction().setAnimation(animation);
        if (isRangedTouch()) {
            int missChance = ConcealmentRule.getMissChance(this);
            boolean missed = ConcealmentRule.checkMissed(this);
            boolean concealment = true;
            if (!missed) {
                concealment = false;
                missed = EvasionRule.checkMissed(this);
                if (missed) {
                    missChance = EvasionRule.getMissChance(this);
                    game.getLogManager().log(
                            StringMaster.getMessagePrefix(true, ownerObj.getOwner().isMe())
                                    + StringMaster.getPossessive(ownerObj.getName()) + " "
                                    + getDisplayedName() + " has been dodged "
                                    + StringMaster.wrapInParenthesis("" + missChance + "%"));
                }

            } else
                game.getLogManager().log(
                        StringMaster.getMessagePrefix(true, ownerObj.getOwner().isMe())
                                + StringMaster.getPossessive(ownerObj.getName())
                                + " "
                                + getDisplayedName()
                                + " has missed due to Concealment"
                                + StringMaster.wrapInParenthesis(""
                                + ConcealmentRule.getMissChance(this) + "%"));
            if (missed) {
                animation.addPhase(new AnimPhase(PHASE_TYPE.MISSED, missChance, concealment));
                result = false;
                StackingRule.actionMissed(this);
            } else {
                result = resolve();
            }
            payCosts();
        } else {
            result = resolve();
            // if (result || (!checkBool(STD_BOOLS.CANCELLABLE) && mode
            // !=TARGETING_MODE.MULTI))
            if (!result)
                if (isCancelDefault())
                    cancelled = true;
            if (cancelled != null) {
                if (cancelled)
                    timeCost = 0;
                else
                    payCosts();
            } else
                payCosts();
        }
        if (!triggered)
            if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, ref)))
                result = false;
        if (!BooleanMaster.isTrue(cancelled))
            refreshVisuals();
        refreshed = true;
        return result;
    }

    public void initAnimation() {
        animationKey = null;
        setAnimation(getGame().getAnimationManager().getActionAnimation(this));
        ActivesConstructor.setAnimForEffects(this);
    }

    private void initTimeCost() {
        if (costs.getCost(PARAMS.C_N_OF_ACTIONS) == null)
            return;
        timeCost = costs.getCost(PARAMS.C_N_OF_ACTIONS).getPayment().getAmountFormula().getInt(ref)

                * ownerObj.getIntParam(PARAMS.INITIATIVE_MODIFIER);

    }

    public void actionComplete() {

        getGame().getManager().setActivatingAction(null);
        highlightOff();
        try {
            checkPendingAttacksOfOpportunity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Boolean endTurn = getGame().getRules().getTimeRule().actionComplete(this, timeCost);
        if (!endTurn) {
            game.getManager().reset();
            if (ChargeRule.checkRetainUnitTurn(this))
                endTurn = null;
        }
        if (endTurn != null)
            endTurn = !endTurn;
        waitForAnimation(getAnimation());
        getGame().getManager().unitActionCompleted(this, endTurn);

    }

    private void checkPendingAttacksOfOpportunity() {
        for (DC_ActiveObj attack : new LinkedList<>(getPendingAttacksOpportunity())) {
            if (!AttackOfOpportunityRule.checkPendingAttackProceeds(getOwnerObj(), attack))
                continue;
            getPendingAttacksOpportunity().remove(attack);
            Ref REF = Ref.getCopy(attack.getRef());
            REF.setTarget(getOwnerObj().getId());
            attack.activate(REF);
        }

    }

    protected boolean checkExtraAttacksDoNotInterrupt(ENTRY_TYPE entryType) {
        if (RuleMaster.checkRuleGroupIsOn(RULE_GROUP.EXTRA_ATTACKS))
            return !ExtraAttacksRule.checkInterrupted(this, entryType);
        return true;
    }

    public boolean resolve() {
        addStdPassives();
        activatePassives();
        setResistanceChecked(false);

        boolean result = true;
        if (isContinuous()) // don't remove unless a shot in the leg is
            // scheduled ... performance is fine!
            setConstructed(false);
        if (!isConstructed())
            construct();

        if (animation != null)
            if (!(animation instanceof AttackAnimation))
                animation.addPhase(new AnimPhase(PHASE_TYPE.ACTION_RESOLVES, this));
        animate();
        if (abilities != null) {
            result = getAbilities().activate(ref);
        } else
            // for Multi-targeting when single-wrapped Abilities cannot be used
            for (Active active : actives) {
                try {
                    result &= active.activate(ref);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!result)
                    break;// TODO if cancelled!
                if (!game.isStarted())
                    continue;
                // cancelled = null; TODO ???
                if (actives.size() > 1) {
                    game.getManager().reset();
                    refreshVisuals(false);
                }
            }
        SoundMaster.playEffectSound(SOUNDS.IMPACT, this);
        return result;
    }

    private void addStdPassives() {
        if (!StringMaster.isEmpty(getProperty(G_PROPS.STANDARD_PASSIVES))) {
            ownerObj.addProperty(G_PROPS.STANDARD_PASSIVES, getProperty(G_PROPS.STANDARD_PASSIVES));
        }
        if (!StringMaster.isEmpty(getProperty(PROPS.STANDARD_ACTION_PASSIVES))) {
            ownerObj.addProperty(G_PROPS.STANDARD_PASSIVES,
                    getProperty(PROPS.STANDARD_ACTION_PASSIVES));
        }
    }

    public void animate(Obj target) {
        Ref REF = ref.getCopy();
        REF.setTarget(target.getId());
        animate(REF);
    }

    public void animate(Ref ref) {

        getGame().getAnimationManager().actionResolves(this, ref);

        // phases? for generic actions - turn, modes, inventory etc - ?

    }

    public void animate() {
        if (checkAnimationOmitted())
            return;

        animate(this.ref);
    }

    private boolean checkAnimationOmitted() {

        for (String exception : ANIMATION_EXCEPTIONS) {
            if (getName().equalsIgnoreCase(exception))
                return true;
        }
        return false;
    }

    public boolean isResistible() {

        return getResistanceType() == RESISTANCE_TYPE.CHANCE_TO_BLOCK;
    }

    public DAMAGE_TYPE getEnergyType() {
        if (energyType == null) {
            energyType = (new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
                    getProperty(PROPS.DAMAGE_TYPE)));
        }
        if (energyType == null) {
            energyType = DC_ContentManager.getDamageForAspect(getAspect());
            if (energyType == null)
                return DAMAGE_TYPE.MAGICAL;
        }
        return energyType;
    }

    public RESISTANCE_TYPE getResistanceType() {
        if (resistType == null) {
            resistType = (new EnumMaster<RESISTANCE_TYPE>().retrieveEnumConst(
                    RESISTANCE_TYPE.class, getProperty(PROPS.RESISTANCE_TYPE)));
        }
        return resistType;
    }

    public void payCosts() {
        if (!isCostAnimAdded())
            addCostAnim();

        if (isFree()) {

            return;
        }
        addCooldown();

        try {
            getCosts().pay(this.ref);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void communicate(Ref ref) {
        if (!getGame().isOffline())
            getGame().getCommunicator().transmitActivateCommand(this, ref);
    }

    @Override
    public boolean hasSprite() {
        return hasImpactSprite();
    }

    public boolean hasImpactSprite() {
        return !StringMaster.isEmpty(getProperty(G_PROPS.IMPACT_SPRITE));
    }

    @Override
    public boolean isInterrupted() {
        if (interrupted) {
            interrupted = false;
            return true;
        }
        boolean result = false;
        for (Active abil : actives) {
            result |= abil.isInterrupted();
        }

        return result;
    }

    @Override
    public void setInterrupted(boolean b) {
        this.interrupted = b;

    }

    @Override
    public boolean isConstructed() {
        return super.isConstructed();
    }

    protected boolean isConstructAlways() {
        return false;
        // return isFree(); could this be our quarry? TODO
    }

    @Override
    public void toBase() {
        for (DC_ActiveObj subaction : getSubActions())
            subaction.toBase();
        super.toBase();
        if (getOwnerObj() == null)
            return;
        if (getOwnerObj().isAiControlled())
            resetTargetingCache();
        addCostMods();
        this.canActivate = null;

        initRange();

        if (!StringMaster.isEmpty(getProperty(G_PROPS.PASSIVES)))
            try {
                AbilityConstructor.constructPassives(this);
            } catch (Exception e) {
            }
        // super.activatePassives(); now upon resolve()!!!
    }

    private void initRange() {
        if (!isStandardAttack() && getActionType() != ACTION_TYPE.SPECIAL_ATTACK)
            return;
        Obj weapon = ref.getObj(KEYS.RANGED);
        if (!isRanged()) {
            weapon = null;
            // if (isOffhand()) TODO
            // weapon = ref.getObj(KEYS.OFFHAND);
            // else
            // weapon = ref.getObj(KEYS.WEAPON);
            // or get from hero directly?
        } else {
            // Ref Not Empty(ranged,AMMO,);
        }
        if (weapon != null)
            modifyParameter(PARAMS.RANGE, weapon.getIntParam(PARAMS.RANGE));
    }

    protected void addCostMods() {
        if (getActionGroup() == ACTION_TYPE_GROUPS.MODE)
            return;

        // addCustomMods(); deprecated
        if (getActionGroup() == ACTION_TYPE_GROUPS.ITEM)
            return;
        applyPenalties();

    }

    protected void applyPenalties() {
        Integer sta = ownerObj.getIntParam(PARAMS.STAMINA_PENALTY);
        Integer ap = ownerObj.getIntParam(PARAMS.AP_PENALTY);
        Integer ess = ownerObj.getIntParam(PARAMS.ESSENCE_PENALTY);
        Integer foc = ownerObj.getIntParam(PARAMS.FOCUS_PENALTY);
        Integer cp = ownerObj.getIntParam(PARAMS.CP_PENALTY);
        if (isCounterMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.COUNTER_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
                    .getIntParam(PARAMS.COUNTER_STAMINA_PENALTY));
        }
        if (isInstantMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.INSTANT_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
                    .getIntParam(PARAMS.INSTANT_STAMINA_PENALTY));
        }
        if (isAttackOfOpportunityMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.AOO_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
                    .getIntParam(PARAMS.AOO_STAMINA_PENALTY));
        }
        switch (getActionGroup()) {
            case ATTACK:
                // boolean offhand = checkSingleProp(G_PROPS.ACTION_TAGS,
                // StringMaster
                // .getWellFormattedString(ACTION_TAGS.OFF_HAND + ""));
                if (isOffhand()) {
                    ap += ownerObj.getIntParam(PARAMS.OFFHAND_ATTACK_AP_PENALTY, false);
                    sta += ownerObj.getIntParam(PARAMS.OFFHAND_ATTACK_STA_PENALTY, false);
                } else {
                    ap += ownerObj.getIntParam(PARAMS.ATTACK_AP_PENALTY, false);
                    sta += ownerObj.getIntParam(PARAMS.ATTACK_STA_PENALTY, false);
                }
                if (isThrow()) {
                    sta += 25 * (EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, getOwnerObj()
                            .getWeapon(isOffhand()).getWeaponSize()) - 1);
                    // TODO
                }
                break;
            case MOVE:
                FlyingRule.checkAddMoveCostReductions(ownerObj);
                TerrainRule.addMoveCost(this);
                sta += ownerObj.getIntParam(PARAMS.MOVE_STA_PENALTY, false);
                ap += ownerObj.getIntParam(PARAMS.MOVE_AP_PENALTY, false);
                break;
            case SPELL:
                ap += ownerObj.getIntParam(PARAMS.SPELL_AP_PENALTY, false);
                sta += ownerObj.getIntParam(PARAMS.SPELL_STA_PENALTY, false);
                ess += ownerObj.getIntParam(PARAMS.SPELL_ESS_PENALTY, false);
                foc += ownerObj.getIntParam(PARAMS.SPELL_FOC_PENALTY, false);

                break;
            case TURN:
                ap += ownerObj.getIntParam(PARAMS.MOVE_AP_PENALTY, false);
                break;
            case HIDDEN:
                ap += ownerObj.getIntParam(PARAMS.ATTACK_AP_PENALTY, false);
                sta += ownerObj.getIntParam(PARAMS.ATTACK_STA_PENALTY, false);
                break;

        }
        modifyParamByPercent(PARAMS.CP_COST, cp, true);
        modifyParamByPercent(PARAMS.STA_COST, sta, true);
        modifyParamByPercent(PARAMS.AP_COST, ap, true);
        modifyParamByPercent(PARAMS.ESS_COST, ess, true);
        modifyParamByPercent(PARAMS.FOC_COST, foc, true);
        modifyParamByPercent(PARAMS.FOC_REQ, foc, false);
    }

    protected void addCustomMods() {
        if (ownerObj.getCustomParamMap() == null)
            return;
        for (PARAMETER param : DC_ContentManager.getCostParams()) {
            addCustomMod(
                    main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_ACTIVE_NAME,
                    getName(), param, false);
            addCustomMod(main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_MOD_ACTIVE_NAME,
                    getName(), param, true);
        }
    }

    protected void addCustomMod(CUSTOM_VALUE_TEMPLATE template, String string, PARAMETER param,
                                boolean percent) {

        String value_ref = CustomValueManager.getCustomValueName(template, param.getName(), string);
        int amount = ownerObj.getCounter(value_ref);
        if (amount == 0)
            return;
        if (percent)
            modifyParamByPercent(param, amount, true);
        else {
            if (amount > 0)
                modifyParameter(param, amount);
            else
                modifyParameter(param, amount, 1);
        }
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return canBeActivated(ref, false);
    }

    public boolean canBeActivated(Ref ref, boolean first) {
        if (!first || broken)
            if (canActivate != null) {

                return canActivate;
            }
        if (checkStatus(STATUS.BLOCKED)) {
            return false;
        }
        // toBase();
        boolean result = false;
        try {
            initCosts(); // TODO ++ check if there are any targets
            costs.setActiveId(id);
            result = costs.canBePaid(ref);
            broken = false;
        } catch (Exception e) {
            if (!broken)
                e.printStackTrace();
            broken = true;
        } finally {
            this.setCanActivate(result);
        }
        return result;
    }

    public void setFocusRequirementMod(String focReq) {
        this.focReqMod = focReq;
    }

    public void initCosts() {
        initCosts(false);
    }

    public void initCosts(boolean anim) {
        if (isFree())
            costs = new Costs(new LinkedList<Cost>());
        else {
            try {
                costs = DC_CostsFactory.getCostsForSpell(this, isSpell());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Cost cp_cost = costs.getCost(PARAMS.C_N_OF_COUNTERS);
            Cost ap_cost = costs.getCost(PARAMS.C_N_OF_ACTIONS);
            boolean noCounterCost = cp_cost == null;
            if (!noCounterCost)
                noCounterCost = cp_cost.getPayment().getAmountFormula().toString().isEmpty()
                        || cp_cost.getPayment().getAmountFormula().toString().equals("0");
            if (noCounterCost) { // if not specifically set...
                if (isExtraAttackMode()) {
                    cp_cost = new CostImpl(new Payment(PARAMS.C_N_OF_COUNTERS, ap_cost.getPayment()
                            .getAmountFormula()));
                    cp_cost.getPayment().getAmountFormula().applyModifier(
                            getOwnerObj().getIntParam(PARAMS.EXTRA_ATTACKS_POINT_COST_MOD));
                    cp_cost.setCostParam(PARAMS.CP_COST);

                }
            } else {
                if (!(isExtraAttackMode()))
                    costs.getCosts().remove(cp_cost);
            }
            if (isAttackOfOpportunityMode()) { // TODO only if watched? better
                // here perhaps!
                cp_cost.addAltCost(ap_cost);
            }
            costs.removeCost(isExtraAttackMode() ? PARAMS.C_N_OF_ACTIONS : PARAMS.C_N_OF_COUNTERS);
        }
        if (anim) {
            addCostAnim();

        }
        costs.setActive(this);
        if (!isInstantMode() || isCounterMode())
            setCanActivate(costs.canBePaid(ref));

    }

    public boolean isExtraAttackMode() {
        return isInstantMode() || isCounterMode() || isAttackOfOpportunityMode();
    }

    private void addCostAnim() {
        if (getAnimation() != null)
            if (!isSubActionOnly())
                getAnimation().addStaticPhase(new AnimPhase(PHASE_TYPE.COSTS_PAID, costs));
        setCostAnimAdded(true);
    }

    private boolean isCostAnimAdded() {
        return costAnimAdded;
    }

    public void setCostAnimAdded(boolean costAnimAdded) {
        this.costAnimAdded = costAnimAdded;
    }

    public LogEntryNode getEntry() {
        return entry;
    }

    @Override
    public Animation getAnimation() {
        if (getModeAction() != null) {
            return getModeAction().getAnimation();
        }
        return super.getAnimation();
    }

    public boolean isSubActionOnly() {
        return isAttack();
    }

    public boolean isSpell() {
        return TYPE_ENUM == OBJ_TYPES.SPELLS;
    }

    public String getSpecialRequirements() {
        return getProperty(G_PROPS.SPECIAL_REQUIREMENTS);
    }

    public boolean canBeActivated() {

        return canBeActivated(ref);
    }

    public void tick() {
        Integer cooldown = getIntParam(PARAMS.COOLDOWN);
        if (cooldown <= 0) {
            setParam(PARAMS.C_COOLDOWN, cooldown); // modify by [cooldown]?
        } else {
            if (getIntParam(PARAMS.C_COOLDOWN) > 0)
                modifyParameter(PARAMS.C_COOLDOWN, -1, 0);
        }
    }

    public boolean isChanneling() {
        return false;
    }

    public boolean activateChanneling() {
        animate(ownerObj);
        initCosts();
        initChannelingCosts();
        game.getLogManager().log(">> " + ownerObj.getName() + " has begun Channeling " + getName());
        boolean result = (checkExtraAttacksDoNotInterrupt(ENTRY_TYPE.ACTION));
        if (result) {
            this.channeling = true;
            ChannelingRule.playChannelingSound(this, HeroAnalyzer.isFemale(ownerObj));
            result = ChannelingRule.activateChanneing(this);
        }

        channelingActivateCosts.pay(ref);
        actionComplete();
        return result;
    }

    protected void initChannelingCosts() {
        channelingResolveCosts = new Costs(costs.getRequirements(), costs.getCosts());
        channelingActivateCosts = new Costs(costs.getRequirements(), costs
                .getCost(PARAMS.C_N_OF_ACTIONS));
        channelingResolveCosts.removeCost(PARAMS.C_N_OF_ACTIONS);
        channelingResolveCosts.removeRequirement(InfoMaster.COOLDOWN_REASON);
    }

    //
    // public Formula getCooldownFormula() {
    // return CooldownManager.getCooldownFormula(this);
    // }
    //
    // public void addCooldownBlock() {
    // Condition retainCondition = new NumericCondition("{TARGET_"
    // + CooldownManager.COOLDOWN_COUNTER + "}", "0", false);
    //
    // attachStatusBuff("On Cooldown", SPELL_MANIPULATION.BLOCK,
    // retainCondition);
    //
    // }

    protected void addCooldown() {
        // if (game.isDebugMode())
        // return;

        Integer cooldown = getIntParam(PARAMS.COOLDOWN);
        // if (cooldown == 0)
        // return;
        if (cooldown <= 0) {
            modifyParameter(PARAMS.C_COOLDOWN, 1);
        } else {
            setParam(PARAMS.C_COOLDOWN, cooldown);
        }
    }

    public void attachStatusBuff(String buffName, SPELL_MANIPULATION spell_manipulation,
                                 Condition retainCondition) {

        AddBuffEffect effect = new AddBuffEffect(retainCondition, buffName,
                new ManipulateSpellEffect(spell_manipulation));
        effect.apply(Ref.getSelfTargetingRefNew(this));
    }

    @Override
    protected void addDynamicValues() {
        Integer cooldown = getIntParam(PARAMS.COOLDOWN);
        if (cooldown < 0)
            setParam(PARAMS.C_COOLDOWN, cooldown);
        else
            setParam(PARAMS.C_COOLDOWN, 0);
        // TODO adjust costs based on hero's skills

    }

    public void modifyRequirement(PARAMETER value, String mod) {
        costs.modifyRequirement(value, mod);
    }

    public void setCost(PARAMETER value, String mod) {
        costs.setCost(value, mod);
    }

    public void modifyCost(PARAMETER value, String mod) {
        getCostMods().put(PARAMS.ESS_COST, mod);
        costs.modifyCost(value, mod);
    }

    @Override
    public void setRef(Ref REF) {
        REF.setID(Ref.KEYS.ACTIVE, getId());
        super.setRef(REF);
        ref.setTriggered(false);
        setOwnerObj((DC_HeroObj) ref.getObj(KEYS.SOURCE));
    }

    public void playCancelSound() {

    }

    @Override
    public void invokeRightClicked() {
        if (!getSubActions().isEmpty()) {
            getGame().getBattleField().getBuilder().toggleDisplayActionModePanel(this);
            SoundMaster.blockNextSound();
        }
        super.invokeRightClicked();

    }

    @Override
    public void initToolTip() {
        getGame().getToolTipMaster().initActionToolTip(this, true);
    }

    public void invokeClicked() {
        boolean dont = ownerObj.checkUncontrollable();
        if (!dont) {
            dont = !canBeManuallyActivated();
        }
        if (dont && !CoreEngine.isGraphicTestMode()) {
            getGame().getToolTipMaster().initActionToolTip(this, false);

            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return; // "hollow sound"? TODO
        }
        if (getGame().getManager().isSelecting()) {
            getGame().getManager().objClicked(this);
            return;
        } else {
            if (getGame().getManager().isActivatingAction()
                    || !getGame().getManager().getActiveObj().equals(getOwnerObj())) {
                SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                return;
            }
            getGame().getManager().setActivatingAction(this);
            playActivateSound();
            new Thread(this, "Action " + getName() + id).start();

        }
    }

    @Override
    public VISIBILITY_LEVEL getVisibilityLevel() {
        return VISIBILITY_LEVEL.CLEAR_SIGHT;
    }

    public boolean isBlocked() {
        setTooltip(null);
        if (getOwnerObj().isAiControlled()) {
            setTooltip("This unit is AI controlled!");
            return true;
        }
        if (!getOwnerObj().isMine()) {
            setTooltip("You do not control this unit!");
            return true;
        }
        if (getGame().getManager().getActiveObj() != null)
            if (!getGame().getManager().getActiveObj().isMine()) {
                setTooltip("Wait for the enemy's turn!");
                return true;
            }
        if (!getGame().isOffline())
            if (getGame().isHost()) {
                if (getGame().getConnector().isWaiting()) {
                    setTooltip("Wait for the other players to join!");
                    return true;
                }
            }
        return false;
    }

    private void setTooltip(String string) {
        customTooltip = string;

    }

    @Override
    public String toString() {
        return StringMaster.getPossessive(getOwnerObj().getNameIfKnown()) + " " + getName();
    }

    public String getCustomTooltip() {
        return customTooltip;
    }

    private boolean canBeManuallyActivated() {
        if (isBlocked())
            return false;
        Boolean checkSubActionMode = checkSubActionModeActivation();
        if (checkSubActionMode != null)
            return checkSubActionMode;

        return canBeActivated(ref, true);
    }

    private Boolean checkSubActionModeActivation() {
        // TODO triggered activation?
        DC_UnitAction action = getModeAction();
        if (action == null)
            return null;
        return action.canBeActivated(ref);

    }

    public DC_UnitAction getModeAction() {
        String mode = ownerObj.getActionMode(this);
        if (mode == null)
            return null;
        if (isAttack())
            return (DC_UnitAction) game.getActionManager().getAction(mode, ownerObj);
        return (DC_UnitAction) game.getActionManager().getAction(mode + " " + getName(), ownerObj);
    }

    @Override
    public void clicked() {
        try {
            DC_UnitAction modeAction = getModeAction();
            if (modeAction != null) {
                modeAction.activate();
                return;
            }
            Ref REF = ownerObj.getRef().getCopy();
            REF.setTriggered(false);
            setRef(REF);
            activate();
        } catch (Throwable e) {
            getGame().getManager().setActivatingAction(null);
            e.printStackTrace();
            main.system.auxiliary.LogMaster.log(1, "Action failed: " + toString());
            // actionComplete();
        }
    }

    public void playActivateSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
    }

    public Targeting getTargeting() {
        if (!constructed)
            construct();
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }

    public boolean isFree() {

        if (!free)
            if (!getOwnerObj().isAiControlled())
                if (game.isDebugMode())
                    return getGame().getTestMaster().isActionFree(getName());
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public DamageSprite getDamageSprite() {
        return damageSprite;
    }

    public void setDamageSprite(DamageSprite damageSprite) {
        this.damageSprite = damageSprite;
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

    public boolean isQuietMode() {
        return quietMode;
    }

    public void setQuietMode(boolean quietMode) {
        this.quietMode = quietMode;
    }

    public Map<PARAMETER, String> getCostMods() {
        return costMods;
    }

    public void setCostMods(Map<PARAMETER, String> costMods) {
        this.costMods = costMods;
    }

    public Boolean getCanActivate() {
        if (canActivate == null)
            canActivate = canBeManuallyActivated();

        return canActivate;
    }

    public void setCanActivate(Boolean canActivate) {
        this.canActivate = canActivate;
    }

    @Override
    public UNIT_TO_UNIT_VISION getUnitVisionStatus() {
        return ownerObj.getUnitVisionStatus();
    }

    @Override
    public UNIT_TO_PLAYER_VISION getActivePlayerVisionStatus() {
        return ownerObj.getActivePlayerVisionStatus();
    }

    public synchronized DC_HeroObj getOwnerObj() {
        return ownerObj;
    }

    public synchronized void setOwnerObj(DC_HeroObj ownerObj) {
        this.ownerObj = ownerObj;
    }

    public synchronized Costs getCosts() {
        return costs;
    }

    public synchronized void setCosts(Costs costs) {
        this.costs = costs;
    }

    public boolean isThrow() {
        if (getName().contains(ACTION_TAGS.THROW + ""))
            return true;
        if (this instanceof DC_ItemActiveObj) {
            DC_ItemActiveObj itemActiveObj = (DC_ItemActiveObj) this;
            if (!itemActiveObj.getItem().isAmmo())
                if (itemActiveObj.getItem().getWrappedWeapon() != null)
                    return true;
        }
        return checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.THROW + "")
                || checkProperty(G_PROPS.GROUP, ACTION_TAGS.THROW + "");
    }

    public boolean isRanged() {
        if (getActionGroup() != ACTION_TYPE_GROUPS.ATTACK)
            return false;
        return (checkProperty(G_PROPS.GROUP, ACTION_TAGS.RANGED + "") || checkProperty(
                G_PROPS.ACTION_TAGS, ACTION_TAGS.RANGED + ""));
        // return false;
        // return getIntParam(PARAMS.RANGE) > 1;
    }

    public ACTION_TYPE_GROUPS getActionGroup() {
        if (actionTypeGroup == null)
            actionTypeGroup = initActionTypeGroup();
        return actionTypeGroup;
    }

    private ACTION_TYPE_GROUPS initActionTypeGroup() {
        if (isStandardAttack())
            return ACTION_TYPE_GROUPS.ATTACK;
        if (StringMaster.isEmpty(getProperty(G_PROPS.ACTION_TYPE)))
            return ACTION_TYPE_GROUPS.SPELL;
        ACTION_TYPE type = new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
                getProperty(G_PROPS.ACTION_TYPE));
        if (type == null)
            return ACTION_TYPE_GROUPS.SPELL;
        switch (type) {
            case HIDDEN:
                return ACTION_TYPE_GROUPS.HIDDEN;
            case MODE:
                return ACTION_TYPE_GROUPS.MODE;
            case SPECIAL_ACTION:
                return ACTION_TYPE_GROUPS.SPECIAL;
            case SPECIAL_ATTACK:
                return ACTION_TYPE_GROUPS.ATTACK;
            case ADDITIONAL_MOVE:
                return ACTION_TYPE_GROUPS.MOVE;
            case SPECIAL_MOVE:
                return ACTION_TYPE_GROUPS.MOVE;
            case STANDARD:
                return DC_ActionManager.getStdActionType(this);

        }
        return ACTION_TYPE_GROUPS.SPELL;
    }

    public void setActionTypeGroup(ACTION_TYPE_GROUPS action_type) {
        this.actionTypeGroup = action_type;
    }

    public boolean selectTarget(Ref ref) {
        if (isForcePresetTarget())
            return true;
        if (getTargeting() == null) {
            construct();
            if (getTargeting() == null) {
                main.system.auxiliary.LogMaster.log(1, "null targeting invoked on " + getName());
                return false;
            }
        }
        boolean result = false;
        if (getTargeting() instanceof SelectiveTargeting && getOwnerObj().isAiControlled()) {
            Integer id = ActionManager.selectTargetForAction(this);
            if (id != null) {
                ref.setTarget(id);
                result = true;
            }
        } else
            result = getTargeting().select(ref);
        if (result)
            setCancelled(null);
        else
            setCancelled(true);

        return result;

    }

    public boolean isRangedTouch() {
        return false;

    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public RESISTANCE_TYPE getResistType() {
        return resistType;
    }

    public TARGETING_MODE getTargetingMode() {
        return targetingMode;
    }

    public boolean isResistanceChecked() {
        return resistanceChecked;
    }

    public void setResistanceChecked(boolean resistanceChecked) {
        this.resistanceChecked = resistanceChecked;
    }

    @Override
    public boolean isEffectSoundPlayed() {
        return effectSoundPlayed;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {
        this.effectSoundPlayed = effectSoundPlayed;
    }

    public void resetTargetingCache() {
        getTargetingAnyCache().clear();
        getTargetingCache().clear();
    }

    public boolean canTargetAny() {

        if (!(targeting instanceof SelectiveTargeting))
            return true;
        Map<FACING_DIRECTION, Boolean> map = getTargetingAnyCache().get(
                getOwnerObj().getCoordinates());
        if (map == null) {
            map = new HashMap<>();
            targetingAnyCache.put(getOwnerObj().getCoordinates(), map);
        }

        Boolean canTargetAny = map.get(getOwnerObj().getFacing());
        if (canTargetAny == null)
            canTargetAny = !targeting.getFilter().getObjects(ref).isEmpty();
        map.put(getOwnerObj().getFacing(), canTargetAny);
        return canTargetAny;
    }

    public boolean canBeTargeted(Integer id) {

        Map<FACING_DIRECTION, Map<Integer, Boolean>> map = getTargetingCache().get(
                getOwnerObj().getCoordinates());
        if (map == null) {
            map = new HashMap<>();
            getTargetingCache().put(getOwnerObj().getCoordinates(), map);
        }
        Map<Integer, Boolean> map2 = map.get(getOwnerObj().getFacing());
        if (map2 == null) {
            map2 = new HashMap<>();
            map.put(getOwnerObj().getFacing(), map2);
        }
        Boolean result = map2.get(id);
        // if (result != null)
        // return result;

        if (targeting == null) {
            // TODO ??
            if (getActives().size() > 1) {
                return true;
            }
            if (!getActives().isEmpty())
                if (getActives().get(0).getAbilities().getAbils().size() > 1) {
                    return true;
                }
            return false;
        }
        Ref REF = ref.getCopy();
        REF.setMatch(id);
        if (targeting instanceof MultiTargeting) {
            // TODO ??
        }
        if (result != null) {
            if (result)
                if (!targeting.getFilter().getConditions().check(REF))
                    return false;
            if (!result)
                if (targeting.getFilter().getConditions().check(REF))
                    return true;
        }
        result = targeting.getFilter().getConditions().check(REF);
        map2.put(id, result);
        return result;

    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public List<DC_ActiveObj> getSubActions() {
        if (subActions == null)
            subActions = new LinkedList<>();
        return subActions;
    }

    public void setSubActions(List<DC_ActiveObj> subActions) {
        this.subActions = subActions;
        if (subActions != null)
            for (DC_ActiveObj a : subActions) {
                a.setParentAction(this);
            }
    }

    public DC_ActiveObj getParentAction() {
        return parentAction;
    }

    public void setParentAction(DC_ActiveObj activeObj) {
        parentAction = activeObj;

    }

    @Override
    public boolean isForcePresetTarget() {
        return forcePresetTarget;
    }

    public void setForcePresetTarget(boolean b) {
        forcePresetTarget = b;
        if (actives != null)
            for (ActiveObj a : actives) {
                a.setForcePresetTarget(b);
            }
    }

    public void setSpellLogic(AI_LOGIC spellLogic) {
        this.aiLogic = spellLogic;
    }

    @Override
    public boolean isZone() {
        if (zone != null)
            return zone;
        if (EffectMaster.check(getAbilities(), ZoneEffect.class)) {
            zone = true;
            return true;
        }
        if (EffectMaster.check(getAbilities(), ShapeEffect.class)) {
            zone = true;
            return true;
        }
        zone = false;
        return false;
    }

    @Override
    public boolean isMissile() {
        if (missile != null)
            return missile;
        if (checkProperty(G_PROPS.SPELL_TAGS, SPELL_TAGS.MISSILE.toString())) {
            missile = true;
            return true;
        }
        if (checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.MISSILE.toString())) {
            missile = true;
            return true;
        }
        missile = false;
        return false;
    }

    public AI_LOGIC getAiLogic() {
        if (aiLogic == null)
            aiLogic = new EnumMaster<AI_LOGIC>().retrieveEnumConst(AI_LOGIC.class,
                    getProperty(PROPS.AI_LOGIC));
        return aiLogic;
    }

    public boolean isMove() {
        return getActionGroup() == ACTION_TYPE_GROUPS.MOVE;
    }

    public boolean isTurn() {
        return getActionGroup() == ACTION_TYPE_GROUPS.TURN;
    }

    public boolean isMelee() {
        if (getIntParam(PARAMS.RANGE) > 1)
            return false;
        return !isRanged();
    }

    public boolean isStandardAttack() {
        return getActionType() == ACTION_TYPE.STANDARD_ATTACK;
    }

    public ACTION_TYPE getActionType() {
        return new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
                getProperty(G_PROPS.ACTION_TYPE));
    }

    public boolean isAttack() {
        return getName().equals(DC_ActionManager.ATTACK)
                || getName().equals(DC_ActionManager.OFFHAND_ATTACK);
    }

    public boolean isOffhand() {
        return checkProperty(G_PROPS.GROUP, ACTION_TAGS.OFF_HAND + "");
    }

    public Map<Coordinates, Map<FACING_DIRECTION, Boolean>> getTargetingAnyCache() {
        if (targetingAnyCache == null)
            targetingAnyCache = new HashMap<>();
        return targetingAnyCache;
    }

    public Map<Coordinates, Map<FACING_DIRECTION, Map<Integer, Boolean>>> getTargetingCache() {
        if (targetingCache == null)
            targetingCache = new HashMap<>();
        return targetingCache;
    }

    public String getActionMode() {
        if (getOwnerObj() == null)
            return null;
        return getOwnerObj().getActionMode(this);
    }

    public boolean isAttackOfOpportunityMode() {
        return attackOfOpportunityMode;
    }

    public void setAttackOfOpportunityMode(boolean attackOfOpportunityMode) {
        this.attackOfOpportunityMode = attackOfOpportunityMode;
    }

    public void setLastSubaction(DC_ActiveObj lastSubaction) {
        this.lastSubaction = lastSubaction;

    }

    public void addPendingAttackOpportunity(DC_ActiveObj attack) {
        getPendingAttacksOpportunity().add(attack);
    }

    private List<DC_ActiveObj> getPendingAttacksOpportunity() {
        if (pendingAttacksOpportunity == null)
            pendingAttacksOpportunity = new LinkedList<>();
        return pendingAttacksOpportunity;
    }

    public Integer getFinalModParam(PARAMS mod) {
        return MathMaster.applyModIfNotZero(getIntParam(mod), getOwnerObj().getIntParam(mod));
    }

    public Object getAnimationKey() {
        if (animationKey != null)
            return animationKey;
        String id = getName();
        // if (isStandardAttack()) {
        // id = getParentAction().getName();
        // }
        if (getRef().getTargetObj() == null)
            animationKey = id + " by " + getRef().getSourceObj().getName();
        else
            animationKey = id + " on " + getRef().getTargetObj().getName();
        return animationKey;
    }

    public boolean isAttackOrStandardAttack() {
        return isAttack() || isStandardAttack();
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setAutoSelectionOn(boolean switchOn) {
        this.switchOn = switchOn;
    }

    public int getRange() {
        return getIntParam(PARAMS.RANGE);
    }

}
