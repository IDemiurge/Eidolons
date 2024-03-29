package eidolons.entity.feat.active;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.feat.Feat;
import eidolons.entity.handlers.active.*;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.core.master.EffectMaster;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.math.ModMaster;
import main.ability.Abilities;
import main.ability.Interruptable;
import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.entity.ActionEnums.ACTION_TAGS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.SpellEnums.RESISTANCE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.elements.costs.Costs;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.handlers.EntityMaster;
import main.entity.obj.Active;
import main.entity.obj.IActiveObj;
import main.entity.obj.AttachedObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.action.context.Context;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.Player;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.List;

public abstract class ActiveObj extends DC_Obj implements Feat, IActiveObj, Interruptable,
        AttachedObj {

    protected BattleFieldObject ownerObj;
    protected Targeting targeting;
    private Obj targetObj;
    private GroupImpl targetGroup;

    protected Abilities abilities;

    protected Costs costs = new Costs(new ArrayList<>());
    protected boolean interrupted;
    protected boolean free = false;
    protected boolean quietMode = false;

    private RESISTANCE_TYPE resistType;
    private DAMAGE_TYPE energyType;
    private ACTION_TYPE_GROUPS actionTypeGroup;
    private ACTION_TYPE actionType;
    private AI_LOGIC aiLogic;

    private String customTooltip;
    private ActiveObj parentAction;
    private boolean autoSelectionOn = false;
    private boolean continuous;
    private boolean resistanceChecked;
    private boolean targetingCachingOff;
    private boolean disabled;

    private Damage damageDealt;
    private Runnable onComplete;


    public ActiveObj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
        setRef(ref);
        this.ownerObj = (BattleFieldObject) ref.getSourceObj();
    }

    @Override
    public EntityMaster initMaster() {
        return new ActiveMaster(this);

    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    @Override
    public String getToolTip() {
        return getActivator().getStatusString() + getName();
    }


    @Override
    public void init() {
        super.init();
    }


    /**
     * default self-activation with manual target choosing
     */
    public boolean activate() {
        getMaster().getHandler().activateOnGameLoopThread();
        return true;
    }


    // for ai and inside-engine usage NOT FOR GUI CONTROLS - RUNS IN SAME THREAD!
    @Override
    public boolean activatedOn(Ref ref) {
        return getHandler().activateOn(new Context(ref));
    }

    // for radial and easy custom activation
    public void activateOn(Obj t) {
        getMaster().getHandler().activateOnGameLoopThread((DC_Obj) t);
    }

    public DAMAGE_TYPE getDamageType() {
        if (super.getDamageType() == null) {
            if (getActiveWeapon() == null) {
                if (isAttackAny())
                    return DAMAGE_TYPE.PHYSICAL;
                if (isSpell())
                    return DAMAGE_TYPE.MAGICAL;
                return null;
            } else
                return getActiveWeapon().getDamageType();
        }
        return super.getDamageType();
    }

    public DAMAGE_TYPE getEnergyType() {
        if (energyType == null) {
            energyType = (new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
                    getProperty(PROPS.DAMAGE_TYPE)));
        }
        if (energyType == null) {
            energyType = DC_ContentValsManager.getDamageForAspect(getAspect());
            if (energyType == null) {
                return DAMAGE_TYPE.MAGICAL;
            }
        }
        return energyType;
    }

    public boolean isDamageSpell() {
        return EffectMaster.getFirstEffectOfClass(this,
                DealDamageEffect.class) != null;
    }

    public RESISTANCE_TYPE getResistanceType() {
        if (resistType == null) {
            resistType = (new EnumMaster<RESISTANCE_TYPE>().retrieveEnumConst(
                    RESISTANCE_TYPE.class, getProperty(PROPS.RESISTANCE_TYPE)));
        }

        if (resistType == null) {
            if (isDamageSpell()) {
                return RESISTANCE_TYPE.REDUCE_DAMAGE;
            }
            if (isDebuffSpell()) {
                return RESISTANCE_TYPE.REDUCE_DURATION;
            }

            return RESISTANCE_TYPE.CHANCE_TO_BLOCK;
        }
        return resistType;
    }

    public boolean isDebuffSpell() {
        return EffectMaster.getFirstEffectOfClass(this,
                AddBuffEffect.class) != null;
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

    protected boolean isConstructOnToBase() {
        return !ExplorationMaster.isExplorationOn();
    }

    @Override
    public boolean isConstructAlways() {
        return super.isConstructAlways();
    }

    public boolean canBeActivated() {
        return canBeActivated(ref);
    }

    @Override
    public Integer getCooldown() {
        return null;
    }

    //TODO feature Review - Cooldown
    @Override
    public void cooldownActivated() {
        // super.cooldownActivate();
    }

    @Override
    public void chargeUsed() {

    }

    @Override
    public void timePassed(int seconds) {

    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return canBeActivated(ref, false);
    }


    public String getSpecialRequirements() {
        return getProperty(G_PROPS.SPECIAL_REQUIREMENTS);
    }


    @Override
    public void setRef(Ref REF) {
        REF.setID(KEYS.ACTIVE, getId());
        super.setRef(REF);
        ref.setObj(KEYS.TARGET, targetObj);
        ref.setGroup(targetGroup);

        ref.setTriggered(false);
        setOwnerObj((BattleFieldObject) ref.getObj(KEYS.SOURCE));
        //        this.ref.setGroup(null); // TODO GROUP MUST NOT BE COPIED FROM OTHER SPELLS!
    }

    public void playCancelSound() {

    }

    public String getDescription(Ref ref) {
        return TextParser.parse(getProperty(G_PROPS.DESCRIPTION), ref,
                TextParser.ACTIVE_PARSING_CODE);
    }

    @Override
    public void invokeRightClicked() {
        super.invokeRightClicked();

    }


    @Override
    public boolean isActive() {
        return false;
    }

    public void invokeClicked() {
        if (!getActivator().canBeManuallyActivated()) {
            getActivator().cannotActivate();
            return;
        }
        getHandler().activateOnGameLoopThread();
        //        activate();
        //     TODO is any of it useful?
        //   boolean dont = ownerObj.checkUncontrollable();
        //        if (!dont) {
        //            dont = !canBeManuallyActivated();
        //        }
        //        if (dont && CoreEngine.isSwingOn()) {
        //            getGame().getToolTipMaster().initActionToolTip(this, false);
        //
        //            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
        //            return; // "hollow sound"? TODO
        //        }
        //        if (getGame().getManager().isSelecting()) {
        //            getGame().getManager().objClicked(this);
        //            return;
        //        } else {
        //            if (getGame().getManager().isActivatingAction()
        //             || !getGame().getManager().getActiveObj().equals(getOwnerUnit())) {
        //                DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
        //                return;
        //            }
        //            getGame().getManager().setActivatingAction(this);
        //            playActivateSound();
        //           activate();
        //        }
    }

    @Override
    public VISIBILITY_LEVEL getVisibilityLevel() {
        return VISIBILITY_LEVEL.CLEAR_SIGHT;
    }

    @Override
    public String toString() {
        if (getOwnerUnit() == null) {
            return getName();
        }
        if (Flags.isIDE()) {
            return StringMaster.getPossessive(getOwnerUnit().getName()) + " " + getName();
        }
        return StringMaster.getPossessive(getOwnerUnit().getNameIfKnown()) + " " + getName();
    }

    public String getCustomTooltip() {
        return customTooltip;
    }

    public void setCustomTooltip(String string) {
        customTooltip = string;

    }

    @Override
    public void clicked() {
        try {
            Ref REF = ownerObj.getRef().getCopy();
            REF.setTriggered(false);
            setRef(REF);
            activate();
        } catch (Exception e) {
            getGame().getManager().setActivatingAction(null);
            ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "Action failed: " + toString());
            // actionComplete();
        }
    }

    public void playActivateSound() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK);
    }

    public Targeting getTargeting() {
        if (!constructed || targeting == null) {
            construct();
        }
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }

    public boolean isFree() {

        if (!free) {
            if (!getOwnerObj().isAiControlled()) {
                if (game.isDebugMode()) {
                    return getGame().getTestMaster().isActionFree(getName());
                }
            }
        }
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
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

    @Override
    public UNIT_VISION getUnitVisionStatus() {
        return ownerObj.getUnitVisionStatus();
    }

    @Override
    public PLAYER_VISION getActivePlayerVisionStatus() {
        return ownerObj.getActivePlayerVisionStatus();
    }

    public synchronized BattleFieldObject getOwnerObj() {
        return ownerObj;
    }

    public synchronized void setOwnerObj(BattleFieldObject ownerObj) {
        this.ownerObj = ownerObj;
    }

    public synchronized Unit getOwnerUnit() {
        if (ownerObj instanceof Unit) {
            return (Unit) ownerObj;
        }
        return null;
    }

    public synchronized Costs getCosts() {
        return costs;
    }

    public synchronized void setCosts(Costs costs) {
        this.costs = costs;
    }


    public ACTION_TYPE_GROUPS getActionGroup() {
        if (actionTypeGroup == null) {
            actionTypeGroup = getInitializer().initActionTypeGroup();
        }
        return actionTypeGroup;
    }


    public void setActionTypeGroup(ACTION_TYPE_GROUPS action_type) {
        this.actionTypeGroup = action_type;
    }


    public TARGETING_MODE getTargetingMode() {
        if (CoreEngine.isArcaneVault())
            return new EnumMaster<TARGETING_MODE>().retrieveEnumConst(TARGETING_MODE.class, getProperty(G_PROPS.TARGETING_MODE));
        return getTargeter().getTargetingMode();
    }


    public ActiveObj getParentAction() {
        return parentAction;
    }

    public void setParentAction(ActiveObj activeObj) {
        parentAction = activeObj;

    }


    public void setSpellLogic(AI_LOGIC spellLogic) {
        this.aiLogic = spellLogic;
    }


    public AI_LOGIC getAiLogic() {
        if (aiLogic == null) {
            aiLogic = new EnumMaster<AI_LOGIC>().retrieveEnumConst(AI_LOGIC.class,
                    getProperty(PROPS.AI_LOGIC));
        }
        return aiLogic;
    }

    public ACTION_TYPE getActionType() {
        if (actionType == null)
            actionType =
                    new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
                            getProperty(G_PROPS.ACTION_TYPE));
        return actionType;
    }

    public Integer getFinalModParam(PARAMETER mod) {
        return
                ModMaster.getFinalModForAction(this, mod);
    }

    public int getFinalBonusParam(PARAMS bonus) {
        return
                ModMaster.getFinalBonusForAction(this, bonus);
    }

    public boolean isAttackOrStandardAttack() {
        return isAttackGeneric() || isStandardAttack();
    }

    public boolean isAutoSelectionOn() {
        return autoSelectionOn;
    }

    public void setAutoSelectionOn(boolean switchOn) {
        this.autoSelectionOn = switchOn;
    }

    public int getRange() {
        return getIntParam(PARAMS.RANGE);
    }

    public WeaponItem getActiveWeapon() {
        if (getOwnerUnit() == null) {
            return null;
        }
        if (isThrow()) {
            if (this instanceof QuickItemAction) {
                return ((QuickItemAction) this).getItem().getWrappedWeapon();
            }
        } else if (isRanged()) {
            return (WeaponItem) getOwnerUnit().getLinkedObj(IdKey.RANGED);
        }
        IdKey key = (isOffhand() ? IdKey.OFFHAND : IdKey.WEAPON);
        return (WeaponItem) getOwnerUnit().getLinkedObj(key);
    }


    //DELEGATES
    //


    //RESET
    //exec

    public void addPendingAttackOpportunity(ActiveObj attack) {
        getHandler().addPendingAttackOpportunity(attack);

    }

    public boolean isFailedLast() {
        return getHandler().isFailedLast();
    }

    public void setFailedLast(boolean failedLast) {
        getHandler().setFailedLast(failedLast);
    }


    //INIT


    @Override
    protected void addDynamicValues() {
        getInitializer().addDynamicValues();

    }

    @Override
    protected void addDefaultValues() {
        super.addDefaultValues();
    }

    public void initCosts() {
        getInitializer().initCosts();
    }


    //CHECK


    public boolean isSubActionOnly() {
        return getChecker().isSubActionOnly();
    }

    public boolean isSpell() {
        return getChecker().isSpell();
    }

    public boolean isChanneling() {
        return getChecker().isChanneling();
    }


    public boolean isAttackAny() {
        return getChecker().isAttackAny();
    }


    @Override
    public boolean isBlocked() {
        return getChecker().isBlocked();
    }

    @Override
    public boolean isMove() {
        return getChecker().isMove();
    }

    @Override
    public boolean isTurn() {
        return getChecker().isTurn();
    }

    public boolean isMode() {
        return getChecker().isMode();
    }

    @Override
    public boolean isMelee() {
        return getChecker().isMelee();
    }

    public boolean isStandardAttack() {
        if (getName().equalsIgnoreCase("Shield Bash")) {
            return true;
        }
        return getChecker().isStandardAttack();
    }

    @Override
    public boolean isAttackGeneric() {
        return getChecker().isAttackGeneric();
    }

    @Override
    public boolean isOffhand() {
        return getChecker().isOffhand();
    }

    @Override
    public boolean isRanged() {
        return getChecker().isRanged();
    }

    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public boolean isMissile() {
        return getChecker().isMissile();
    }

    @Override
    public boolean isZone() {
        return getChecker().isZone();
    }

    public boolean isThrow() {
        return getChecker().isThrow();
    }
    //EXEC

    public void payCosts() {
        getHandler().payCosts();
    }

    public void actionComplete() {
        getHandler().actionComplete();
        if (onComplete != null) {
            onComplete.run();
        }
    }

    public boolean isInstantAction() {
        return getChecker().isInstantAction();
    }

    public boolean isInstantMode() {
        return getHandler().isInstantMode();
    }


    public boolean isCounterMode() {
        return getHandler().isCounterMode();
    }

    public void setCounterMode(boolean counterMode) {
        getHandler().setCounterMode(counterMode);
    }

    public boolean isAttackOfOpportunityMode() {
        return getHandler().isAttackOfOpportunityMode();
    }

    public boolean isExtraAttackMode() {
        return getHandler().isExtraAttackMode();
    }

    public boolean selectTarget(Ref ref) {
        return getTargeter().selectTarget(ref);
    }

    public boolean canTargetAny() {
        return getTargeter().canTargetAny();
    }

    public boolean canBeTargeted(Integer id) {
        return getTargeter().canBeTargeted(id);
    }

    protected boolean isResetViaHandler() {
        return true;
    }

    public boolean canBeTargeted(Integer id, boolean caching) {
        return getTargeter().canBeTargeted(id, caching);
    }

    //Activator

    public Boolean isBroken() {
        return getActivator().isBroken();
    }

    public void setBroken(Boolean broken) {
        getActivator().setBroken(broken);
    }

    public boolean canBeManuallyActivated() {
        return getActivator().canBeManuallyActivated();
    }

    public boolean canBeActivated(Ref ref, boolean first) {
        return getActivator().canBeActivated(ref, first);
    }

    public boolean canBeActivatedAsCounter() {
        return getActivator().canBeActivatedAsCounter();
    }

    public boolean canBeActivatedAsAttackOfOpportunity(boolean pending, Unit target) {
        return getActivator().canBeActivatedAsAttackOfOpportunity(pending, target);
    }

    public boolean tryOpportunityActivation(ActiveObj triggeringAction) {
        return getActivator().tryOpportunityActivation(triggeringAction);
    }

    public boolean tryInstantActivation(ActiveObj triggeringAction) {
        return getActivator().tryInstantActivation(triggeringAction);
    }

    public ActiveObj getLastSubaction() {
        return getActivator().getLastSubaction();
    }

    public void setLastSubaction(ActiveObj lastSubaction) {
        getActivator().setLastSubaction(lastSubaction);

    }

    //                                   <><><><><>

    public Targeter getTargeter() {
        return getMaster().getHandler().getTargeter();
    }

    public Activator getActivator() {
        return getMaster().getHandler().getActivator();
    }

    @Override
    public Executor getHandler() {
        return getMaster().getHandler();
    }


    @Override
    public ActiveLogger getLogger() {
        return getMaster().getLogger();
    }

    @Override
    public ActiveObjInitializer getInitializer() {
        return getMaster().getInitializer();
    }

    @Override
    public ActiveCalculator getCalculator() {
        return getMaster().getCalculator();
    }

    @Override
    public ActiveChecker getChecker() {
        return getMaster().getChecker();
    }

    @Override
    public ActiveResetter getResetter() {
        return getMaster().getResetter();
    }


    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public boolean isResistanceChecked() {
        return resistanceChecked;
    }

    public void setResistanceChecked(boolean resistanceChecked) {
        this.resistanceChecked = resistanceChecked;
    }

    @Override
    public boolean isForcePresetTarget() {
        return getTargeter().isForcePresetTarget();
    }

    @Override
    public void setForcePresetTarget(boolean b) {
        getTargeter().setForcePresetTarget(b);
    }

    @Override
    public void setCancelled(Boolean c) {
        getHandler().setCancelled(c);
    }

    @Override
    public Boolean isCancelled() {
        return getHandler().isCancelled();
    }


    public Damage getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Damage damageDealt) {
        this.damageDealt = damageDealt;
    }

    @Override
    public Obj getTargetObj() {
        return targetObj;
    }

    public void setTargetObj(Obj targetObj) {
        this.targetObj = targetObj;
    }

    @Override
    public GroupImpl getTargetGroup() {
        return targetGroup;

    }

    public void setTargetGroup(GroupImpl targetGroup) {
        this.targetGroup = targetGroup;
        getRef().setGroup(targetGroup);
    }

    @Override
    protected void putParameter(PARAMETER param, String value) {
        if (param == PARAMS.AP_COST) {
            int v = NumberUtils.getIntParse(value);
            if (v > type.getIntParam(param) * 2) {
                return;
            }
        }
        super.putParameter(param, value);
    }

    public void setTargetingCachingOff(boolean targetingCachingOff) {
        this.targetingCachingOff = targetingCachingOff;
    }

    public boolean isTargetingCached() {
        if (targetingCachingOff)
            return false;
        return Flags.isTargetingResultCachingOn();
    }

    public List<ActiveObj> getValidSubactions() {
        return getValidSubactions(getRef(), null);
    }

    //TODO NF Rules revamp
    public List<ActiveObj> getValidSubactions(Ref ref, Integer target) {
        List<ActiveObj> subActions = new ArrayList<>();
        return subActions;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled) {
            getCosts().setReason("Disabled");
        }
    }

    public void initAnimRefs(Ref ref) {
        if (abilities != null) {
            for (Effect effect : abilities.getEffects()) {
                effect.initAnimRef(ref);
            }
        }
    }

    public Runnable getOnComplete() {
        return onComplete;
    }

    @Override
    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public ActiveObj getActive() {
        return this;
    }

    //TODO NF Rules revamp
    public List<ActiveObj> getAttackTypes() {
        return new ArrayList<>();
    }
}

