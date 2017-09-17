package main.ability.effects;

import main.ability.Ability;
import main.ability.ActiveAbilityObj;
import main.ability.PassiveAbilityObj;
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
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.ANIM;
import main.system.graphics.SpriteAnimated;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An effect is contained in an {@link Ability} and can either be passive
 * (static) or OneShot (active). OneShot ones take effect when an ability
 * invoking them resolves. They perform some tasks and fire corresponding
 * events, enabling event handler to preCheck triggers and watchers, possibly
 * leading to more abilities resolving and more effects coming in play. Static
 * effects often have watchers, conditions and triggers as well. Passive effects
 * are often contained within a buff, an object that represents one or two
 * static effects bestowed on the target, prodives API for manipulating these
 * effects from the outside
 *
 * @author VonFinsterheim
 */

// EFFECT ARE ABOUT WHAT
// ABILITIE ARE ABOUT HOW
public abstract class EffectImpl extends ReferredElement implements Effect {
    private static Map<UUID, Pair<Class, String>[]> constructorMap;
    protected Formula formula;
    // protected Obj target_obj;
    // ?Ability ability;
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
    private Formula originalFormula;
    private int amount;
    private boolean applied;
    private ActiveObj animationActive;
    private ANIM animation;
    private String xml;
    private UUID id;
    private String name;

    public EffectImpl() {
        super();
    }

    private static boolean isMappingOn() {
        if (CoreEngine.isLevelEditor())
            return false;
        if (CoreEngine.isArcaneVault())
            return false;
        return true;
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
    public ActiveObj getAnimationActive() {
        return animationActive;
    }

    @Override
    public void setAnimationActive(ActiveObj animationActive) {
        this.animationActive = animationActive;
    }

    @Override
    public ANIM getAnimation() {
        if (animation == null)
            if (CoreEngine.isPhaseAnimsOn()) {
                if (getAnimationActive() instanceof ActiveObj) {
                    ActiveObj activeObj = getAnimationActive();
                    animation = activeObj.getAnimation();
                }
            }
        return animation;
    }

    @Override
    public void setAnimation(ANIM animation) {
        this.animation = animation;
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
        return StringMaster.getWellFormattedString(getClass().getSimpleName(), true) + arg;
    }

    private String getArgString() {
        String arg = "";
        if (getFormula() != null) {
            arg = " " + StringMaster.wrapInParenthesis("" + getFormula().getInt(ref));
        }
        // if (infoLevel==FULL) TODO
        // arg += getFormula().toString();
        return arg;
    }

    @Override
    public boolean apply(Ref ref) {
        setRef(ref);

        // for logging
        boolean active = getLayer() != BUFF_RULE && ref.getObj(KEYS.ACTIVE) != null
         && (!(ref.getObj(KEYS.ABILITY) instanceof PassiveAbilityObj));

        if ((ref.getGroup() == null && targetGroup == null) || isIgnoreGroupTargeting()) {
            // single-target effect
            return apply();
        } else {
//multi-target effect, applies to each target
            GroupImpl group = ref.getGroup();
            if (group == null) {
                group = targetGroup;
            } else if (targetGroup == null) {
                // group.setIgnoreGroupTargeting(true);// TODO later instead?
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
                // REF.setGroup(new GroupImpl(targetGroup));
                REF.getGroup().setIgnoreGroupTargeting(true);

                REF.setTarget(id);

                if (construct != null) {
                    result &= getCopy().apply(REF);
                } else {
                    // this.ref.setTarget(id);
                    setIgnoreGroupTargeting(true);
                    result &= apply(REF);
                    setIgnoreGroupTargeting(false);
                }

            }
            // game.getManager().refreshAll();
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
                    LogMaster.log(1, "effect resisted! - " + toString());
                    // TODO sound?
                    return false;
                }
        }

        if (isInterrupted()) {
            setInterrupted(false);
            LogMaster.log(1, "effect interrupted! - " + toString());
            return false;
        }

//        try {
//            animateSprite();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (ref.getObj(KEYS.ABILITY) instanceof ActiveAbilityObj) {
            if (ref.getActive() != null) {
                if (!ref.getActive().isEffectSoundPlayed()) {
                    SoundMaster.playEffectSound(SOUNDS.EFFECT, (Obj) ref.getActive());
                    ref.getActive().setEffectSoundPlayed(true);
                }
            }
        }
        boolean result = false;
        try {
            result = applyThis();
            applied = true;
            if (checkEventsFired())
                game.fireEvent(new Event(STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED, ref));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

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

    @Deprecated
    protected void animateSprite() {
        if (!hasSprite()) {
            return;
        }
        ((SpriteAnimated) getSpell()).getSprite().animate(ref);
    }

    protected boolean hasSprite() {
        try {
            return ((SpriteAnimated) getSpell()).hasSprite();
        } catch (Exception e) {
            return false;
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
        if (CoreEngine.isPhaseAnimsOn())
            if (getAnimationActive() == null) {
                setAnimationActive(ref.getAnimationActive()); // TODO ??
            } else {
                this.ref.setAnimationActive(getAnimationActive());
            }
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
}
