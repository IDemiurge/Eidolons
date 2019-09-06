package main.entity.obj;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.periodic.PeriodicEffect;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.BUFF_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.MessageManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.ArrayList;
import java.util.List;

public class BuffObj extends MicroObj implements Attachment, AttachedObj {

    public static final String DUMMY_BUFF_TYPE = "Dummy Buff";
    protected Double duration;
    protected Effect effect;
    protected boolean retainAfterDeath = false;
    protected Obj basis;
    protected Condition retainConditions;
    protected boolean permanent = false;
    private boolean effectApplied = false;
    private Obj active;
    private boolean appliedThrough;
    private boolean visible = true;
    private String counterName;
    private BUFF_TYPE buffType;
    private boolean isTransient;
    private Boolean negative;
    private Effect dispelEffects;
    private double timeInGame = 0;
    private double period;
    private List<PeriodicEffect> timeEffects;
    private boolean immobilizing;
    private boolean dynamic;

    public BuffObj(ObjType type, Player owner, GenericGame game, Ref ref, Effect effect,
                   double duration, Condition retainCondition) {
        super(
                type == null ? new ObjType(DataManager.getType(DUMMY_BUFF_TYPE, DC_TYPE.BUFFS))
                        : type
                , owner, game, ref);
        this.retainConditions = retainCondition;
        this.effect = effect;
        initTimeEffect();
        setDuration(duration);
        if (duration == 0 || duration == ContentValsManager.INFINITE_VALUE) {
            // this.duration = ContentManager.INFINITE_VALUE;
            this.permanent = true;
        }
        this.basis = game.getObjectById(ref.getBasis());
        addDynamicValues();
//     if   instance
        setTransient(((BuffType) type).isTransient());
        if (checkBool(GenericEnums.STD_BOOLS.INVISIBLE_BUFF)) {
            visible = false;
        }
        try {
            if (ref.getObj(KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.INVISIBLE_BUFF)) {
                visible = false;
            }
        } catch (Exception ignored) {
        }
        if (getName().contains(StringMaster.INVISIBLE_BUFF)) {
            visible = false;
        }
    }

    private void initTimeEffect() {
//        for (String sub : StringMaster.openContainer(getProperty(G_PROPS.PASSIVES))) {
//            timeAbility = AbilityConstructor.newAbility(sub, this, true);
//        }
        timeEffects = new ArrayList<>();
        if (effect instanceof Effects) {
            for (Effect sub : new ArrayList<>(((Effects) effect).getEffects())) {
                if (sub instanceof PeriodicEffect) {
                    timeEffects.add((PeriodicEffect) sub);
                    ((Effects) effect).remove(sub);
                }
            }

        }

    }

    @Override
    public BuffType getType() {
        return (BuffType) super.getType();
    }

    public Effects getEffects() {
        if (effect == null)
            effect = new Effects();
        if (!(effect instanceof Effects)) {
            effect = new Effects(effect);
        }
        return (Effects) effect;
    }

    public Boolean isNegative() {
        return negative;
    }

    public boolean applyEffect() {
        if (effect.getLayer() == Effect.BUFF_RULE) {
            effect.setRef(ref);
            return false;
        }
        setEffectApplied(true);
        setRef(ref);

        LogMaster.log(0, "BUFF EFFECT (" + toString() + ") applied to "
                + ref.getTargetObj());
        return effect.apply(ref);
    }

    @Override
    public void init() {
        super.init();
        toBase();
    }

    @Override
    public void setRef(Ref ref) {
        if (basis != null) {
            ref.setID(KEYS.BASIS, basis.getId());
        }
        ref.setID(KEYS.BUFF, id);
        super.setRef(ref);
        //

    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient(boolean b) {
        this.isTransient = b;
    }

    @Override
    public boolean kill() {
        if (isDead()) {
            return false;
        }

        // if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.BUFF_BEING_REMOVED,
        // REF)))
        // return false;

        setDead(true);
        game.getManager().buffRemoved(this);
        if (dispelEffects != null) {
            dispelEffects.apply(ref
                    // Ref.getSelfTargetingRefCopy(ref.getSourceObj())
            );
        }
        // game.fireEvent(new Event(STANDARD_EVENT_TYPE.BUFF_REMOVED, REF));
        return true;

    }

    @Override
    protected void addDynamicValues() {
        setParam(G_PARAMS.DURATION, duration.toString());
        setParam(G_PARAMS.C_DURATION, getIntParam(G_PARAMS.DURATION));
    }

    @Override
    public String getToolTip() {

        if (counterName == null) {
            if (!getProperty(G_PROPS.CUSTOM_PROPS).isEmpty()) {
                counterName = getProperty(G_PROPS.CUSTOM_PROPS);
            }
        }

        if (counterName != null) {
            return super.getToolTip() + " (" + basis.getCounter(counterName) + ")";
        }
        if (!permanent) {
            return super.getToolTip() + " duration: " + duration;
        }
        return super.getToolTip();
    }

    @Override
    public void clicked() {
        MessageManager.alert(getDescription() + "\n remaining duration: " + duration);
    }

    @Override
    public String getDescription() {
        if (StringMaster.isEmpty(super.getDescription())) {
            return getName() + " with " + getEffect();
        }
        return super.getDescription();
    }

    @Override
    public double getDuration() {
        return duration;
    }

    public void setDuration(Number duration) {
        this.duration = duration.doubleValue();
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    @Override
    public boolean isRetainAfterDeath() {
        return retainAfterDeath;
    }

    @Override
    public void setRetainAfterDeath(boolean retainAfterDeath) {
        this.retainAfterDeath = retainAfterDeath;
    }

    public Obj getBasis() {
        return basis;
    }

    public void timeElapsed(double time) {


        if (permanent) {
            applyTimeEffect(time);
            return;
        }
        LogMaster.log(1, this + " Buff duration reduced by " + time);
        duration -= time;
        LogMaster.log(1, this + " Buff duration = " + duration);
        if (duration < 0) {
            time += duration;
        }
        if (time > 0)
            applyTimeEffect(time);
        durationModified();
    }

    private void applyTimeEffect(double time) {
        for (PeriodicEffect sub : timeEffects) {
            sub.timeElapsed(time);
        }

    }

    private void durationModified() {
        setParam(G_PARAMS.C_DURATION, duration.toString());
        checkDuration();
    }

    @Override
    public Double tick() {
        if (permanent) {
            return duration;
        }
        duration--;
        durationModified();

        modifyParameter(G_PARAMS.TURNS_IN_GAME, 1);
        return duration;
    }

    public boolean checkDuration() {
//        if (permanent)
//            return true; //TODO sure?
        if (isTransient) {
            kill();
            return false;
        }
        if (duration <= 0) {
            if (!permanent) {
                LogMaster.log(1, this + " duration elapsed " + duration);
                kill();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkRetainCondition() {
        try {
            if (ref.getSourceObj().isDead()) {
                if (checkBool(GenericEnums.STD_BOOLS.SOURCE_DEPENDENT)) {
                    kill();
                }
                if (getActive().checkBool(GenericEnums.STD_BOOLS.SOURCE_DEPENDENT)) {
                    kill();
                }

            }
        } catch (Exception e) {
        }

        if (retainConditions != null) {
            if (!retainConditions.preCheck(ref)) {
                LogMaster.log(LOG_CHANNEL.BUFF_DEBUG,
                        "Retain conditions preCheck false for " + getName());
                kill();

                return false;
            }
        }
        return checkDuration();
    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    public void modifyDuration(Integer amount) {
        if (!permanent) {
            duration += amount;
        }
    }

    @Override
    public void remove() {
        kill();

    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean isDispelable() {
        return super.checkBool(GenericEnums.STD_BOOLS.DISPELABLE);
    }

    public boolean isStacking() {
        return super.checkBool(GenericEnums.STD_BOOLS.STACKING);
    }

    public boolean isEffectApplied() {
        return effectApplied;
    }

    public void setEffectApplied(boolean effectApplied) {
        this.effectApplied = effectApplied;
    }

    @Override
    public Obj getOwnerObj() {
        return basis;
    }

    public Obj getActive() {
        return active;
    }

    public void setActive(Obj active) {
        this.active = active;
    }

    public boolean isAppliedThrough() {
        return appliedThrough;
    }

    public void setAppliedThrough(boolean appliedThrough) {
        this.appliedThrough = appliedThrough;
    }

    public void setOnDispelEffects(Effect dispelEffects) {
        this.dispelEffects = dispelEffects;
    }

    public synchronized Condition getRetainConditions() {
        return retainConditions;
    }

    public synchronized void setRetainConditions(Condition retainConditions) {
        this.retainConditions = retainConditions;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setCounterRef(String counterName) {
        this.counterName = counterName;
    }

    public BUFF_TYPE getBuffType() {
        if (buffType == null) {
            buffType = new EnumMaster<BUFF_TYPE>().retrieveEnumConst(BUFF_TYPE.class,
                    getProperty(G_PROPS.BUFF_TYPE));
        }
        return buffType;
    }

    public boolean isDisplayed() {
        if (getBuffType() == BUFF_TYPE.SPELL) {
            return true;
        }
        if (getType().getType() != null)
            if (getType().getType().getName().equals(DUMMY_BUFF_TYPE))
                if (!isTransient())
                    return false;

        return true;
    }

    public void setImmobilizing(boolean immobilizing) {
        this.immobilizing = immobilizing;
    }

    public boolean isImmobilizing() {
        return immobilizing;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }
}
