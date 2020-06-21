package main.ability.effects;

import main.ability.Ability;
import main.ability.ActiveAbilityObj;
import main.ability.effects.continuous.ContinuousEffect;
import main.data.ability.construct.Construct;
import main.data.ability.construct.ConstructionManager;
import main.elements.ReferredElement;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An effect is contained in an {@link Ability} and can either be passive (static) or OneShot (active). OneShot ones
 * take effect when an ability invoking them resolves. They perform some tasks and fire corresponding events, enabling
 * event handler to preCheck triggers and watchers, possibly leading to more abilities resolving and more effects coming
 * in play. Static effects often have watchers, conditions and triggers as well. Passive effects are often contained
 * within a buff, an object that represents one or two static effects bestowed on the target, provides API for
 * manipulating these effects from the outside
 *
 * @author JustMe
 */

// EFFECT ARE ABOUT WHAT
// ABILITIES ARE ABOUT HOW
public abstract class EffectImpl extends ReferredElement implements Effect {
    private static Map<UUID, Pair<Class, String>[]> constructorMap;
    private static boolean mappingOn;
    protected Formula formula;
    protected Integer target;
    protected Integer source;
    protected boolean altered = false;
    protected boolean interrupted = false;
    protected boolean altering = false;
    protected Construct construct;
    protected boolean quietMode;
    protected Integer forcedLayer;
    protected Trigger trigger;
    boolean mapped;
    private boolean ignoreGroupTargeting;
    private GroupImpl targetGroup;
    private boolean irresistible;
    private int layer = Effect.BASE_LAYER;
    private boolean reconstruct;
    private boolean copied;
    private boolean continuousWrapped;
    private Boolean forceStaticParse;
    private Formula originalFormula; //perhaps we could have more subclasses with fields
    private int amount;
    private boolean applied;
    private String xml;
    private UUID id; //To-Cleanup - if we don't do restore-xml..
    private String name;

    public EffectImpl() {
        super();
    }

    private static boolean isMappingOn() {
        if (CoreEngine.isLevelEditor())
            return false;
        if (CoreEngine.isArcaneVault())
            return false;

        return mappingOn;
    }

    public static Map<UUID, Pair<Class, String>[]> getConstructorMap() {
        if (constructorMap == null)
            constructorMap = new HashMap<>();
        return constructorMap;
    }

    public void mapThisToConstrParams(Object... args) {
        //        if (ConstructionManager.isConstructing())
        //            return ;
        if (!isMappingOn())
            return;
        if (mapped)
            return;
        Pair<Class, String>[] paramType = Arrays.stream(args).map(obj ->
                new ImmutablePair(obj.getClass(), ConstructionManager.getXmlFromObject(obj))
        ).collect(Collectors.toList()).toArray(new Pair[args.length]);
        id = UUID.randomUUID();
        getConstructorMap().put(id, paramType);
        xml = ConstructionManager.getXmlFromConstructorData(getClass().getSimpleName(), getConstructorMap().get(id));

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toXml() {
        if (xml != null)
            return xml;
        if (construct != null)
            return construct.toXml();
        if (id != null) {
            xml = ConstructionManager.getXmlFromConstructorData(getClass().getSimpleName(), getConstructorMap().get(id));
        }
        //custom - just gonna have to serialize all fields using reflection?!
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public ActiveObj getActiveObj() {
        if (getRef().getActive() instanceof ActiveObj) {
            return getRef().getActive();
        }
        return null;
    }

    @Override
    public Trigger getTrigger() {
        return trigger;
    }

    @Override
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public void initLayer() {
        layer = Effect.BASE_LAYER;
    }

    @Override
    public int getLayer() {
        if (forcedLayer != null) {
            return forcedLayer;
        }
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public String toString() {
        if (name != null) return name;
        name = getClass().getSimpleName()
                + ("" + this.hashCode()).substring(("" + this.hashCode()).length() - 2);
        return name;
    }

    @Override
    public String getTooltip() {
        String arg = getArgString();
        return StringMaster.format(getClass().getSimpleName(), true) + arg;
    }

    private String getArgString() {
        String arg = "";
        if (getFormula() != null) {
            arg = " " + StringMaster.wrapInParenthesis("" + getFormula().getInt(ref));
        }
        return arg;
    }

    @Override
    public boolean apply(Ref ref) {
        setRef(ref);

        // for logging
        // boolean active = getLayer() != BUFF_RULE && ref.getObj(KEYS.ACTIVE) != null
        //  && (!(ref.getObj(KEYS.ABILITY) instanceof PassiveAbilityObj));

        if ((ref.getGroup() == null && targetGroup == null) || isIgnoreGroupTargeting()) {
            // single-target effect
            return apply();
        } else {
            //multi-target effect, applies to each target
            GroupImpl group = ref.getGroup();
            if (group == null) {
                group = targetGroup;
            } else if (targetGroup == null) {
                targetGroup = group;
            }
            if (group.isIgnoreGroupTargeting()) {
                return apply();
            }
            List<Integer> groupIds = group.getObjectIds();
            boolean result = true;
            for (Integer id : groupIds) {
                if (isInterrupted()) {
                    break;
                }

                Ref REF = this.ref.getCopy();
                REF.getGroup().setIgnoreGroupTargeting(true);

                REF.setTarget(id);

                if (construct != null) {
                    result &= getCopy().apply(REF);
                } else {
                    setIgnoreGroupTargeting(true);
                    result &= apply(REF);
                    setIgnoreGroupTargeting(false);
                }

            }
            return result;
        }

    }

    @Override
    public void remove() {

    }

    @Override
    public boolean apply() {
        if (!quietMode) {
            if (checkEventsFired())
                if (!game.getManager().effectApplies(this)) {
                    LogMaster.log(LOG_CHANNEL.EFFECT_DEBUG, "effect resisted! - " + toString());
                    return false;
                }
        }
        if (isInterrupted()) {
            setInterrupted(false);
            LogMaster.log(LOG_CHANNEL.EFFECT_DEBUG, "effect interrupted! - " + toString());
            return false;
        }
        if (ref.getObj(KEYS.ABILITY) instanceof ActiveAbilityObj) {
            if (ref.getActive() != null) {
                if (!ref.getActive().isEffectSoundPlayed()) {
                    SoundMaster.playEffectSound(SOUNDS.EFFECT, (Obj) ref.getActive());
                    ref.getActive().setEffectSoundPlayed(true);
                }
            }
        }
        boolean             result = applyThis();
            applied = true;
            if (checkEventsFired())
                fireAppliedEvent();

        return result;

    }

    protected void fireAppliedEvent() {
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED, ref));
    }

    protected boolean checkEventsFired() {
        return false;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    @Override
    public void appendFormulaByMod(Object mod) {
        setFormula(new Formula(getFormula().getAppendedByModifier(mod) + ""));

    }

    @Override
    public void multiplyFormula(Object mod) {
        setFormula(new Formula(getFormula().getAppendedByMultiplier(mod) + ""));

    }

    @Override
    public void addToFormula(Object mod) {
        setFormula(

                new Formula(StringMaster.wrapInParenthesis(getFormulaString()) + "+" + mod));

    }

    private String getFormulaString() {
        if (getFormula().toString().isEmpty()) {
            return "0";
        }
        return getFormula().toString();
    }

    public void resetOriginalFormula() {
        if (originalFormula == null) {
            originalFormula = new Formula(formula == null ? "" : formula.toString());
        } else {
            formula = new Formula(originalFormula.toString());
        }
    }


    @Override
    public Obj getSpell() {
        if (ref == null) {
            return null;
        }
        return ref.getObj(KEYS.ACTIVE);
    }

    @Override
    public abstract boolean applyThis();

    @Override
    public Formula getFormula() {
        if (formula == null) {
            formula = new Formula("");
        }
        return formula;
    }

    @Override
    public void setFormula(Formula newFormula) {
        this.formula = newFormula;
    }

    @Override
    public void setRef(Ref REF) {
        REF.setEffect(this);
        super.setRef(REF);
        this.source = REF.getSource();
        this.target = REF.getTarget();

    }

    public boolean isAltering() {
        return altering;
    }

    public void setAltering(boolean altering) {
        this.altering = altering;
    }

    public boolean isAltered() {
        return altered;
    }

    public void setAltered(boolean altered) {
        this.altered = altered;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    @Override
    public Construct getConstruct() {
        return construct;
    }

    @Override
    public void setConstruct(Construct construct) {
        this.construct = construct;
    }

    @Override
    public Effect getCopy() {
        if (construct == null || !isCopied()) {
            setCopied(true);
            return this;
        }
        EffectImpl effect = (EffectImpl) ConstructionManager.construct(construct);
        effect.setQuietMode(quietMode);
        effect.setAltered(altered);
        effect.setTargetGroup(targetGroup);
        effect.setIgnoreGroupTargeting(ignoreGroupTargeting);
        effect.setConstruct(construct);

        return effect;
    }

    @Override
    public boolean isCopied() {
        return copied;
    }

    @Override
    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    @Override
    public boolean isQuietMode() {
        return quietMode;
    }

    public void setQuietMode(boolean b) {
        this.quietMode = b;
    }

    public boolean isIgnoreGroupTargeting() {
        return ignoreGroupTargeting;
    }

    public void setIgnoreGroupTargeting(boolean ignoreGroupTargeting) {
        this.ignoreGroupTargeting = ignoreGroupTargeting;
    }

    public GroupImpl getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(GroupImpl targetGroup) {
        this.targetGroup = targetGroup;
    }


    public boolean isContinuousWrapped() {
        if (this instanceof ContinuousEffect) {
            return true;
        }
        return continuousWrapped;
    }

    public void setContinuousWrapped(boolean continuousWrapped) {
        this.continuousWrapped = continuousWrapped;
    }

    public boolean isIrresistible() {
        return irresistible;
    }

    public void setIrresistible(boolean b) {
        this.irresistible = b;

    }

    public boolean isReconstruct() {
        return reconstruct;
    }

    public void setReconstruct(boolean reconstruct) {
        this.reconstruct = reconstruct;
    }

    public void setForcedLayer(Integer forcedLayer) {
        this.forcedLayer = forcedLayer;
    }

    @Override
    public void setForceStaticParse(Boolean forceStaticParse) {
        this.forceStaticParse = forceStaticParse;

    }

    public STANDARD_EVENT_TYPE getEventTypeDone() {
        return null;
    }

    @Override
    public Boolean isForceStaticParse() {
        return forceStaticParse;
    }

    public void setOriginalFormula(Formula originalFormula) {
        this.originalFormula = originalFormula;
    }
}
