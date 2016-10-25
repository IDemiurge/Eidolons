package main.entity.obj.specific;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS.BUFF_TYPE;
import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.content.ContentManager;
import main.content.parameters.G_PARAMS;
import main.content.properties.G_PROPS;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.AttachedObj;
import main.entity.obj.Attachment;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.game.MicroGame;
import main.game.event.MessageManager;
import main.game.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;

public class BuffObj extends MicroObj implements Attachment, AttachedObj {

    public static final String DUMMY_BUFF_TYPE = "Dummy Buff";
    protected int duration;
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

    public BuffObj(BuffType type, Player owner, MicroGame game, Ref ref, Effect effect,
                   int duration, Condition retainCondition) {
        super(type, owner, game, ref);
        this.retainConditions = retainCondition;
        this.effect = effect;
        this.duration = duration;
        if (duration == 0 || duration == ContentManager.INFINITE_VALUE) {
            // this.duration = ContentManager.INFINITE_VALUE;
            this.permanent = true;
        }
        this.basis = game.getObjectById(ref.getBasis());
        addDynamicValues();
        setTransient(type.isTransient());
        if (checkBool(STD_BOOLS.INVISIBLE_BUFF))
            visible = false;
        try {
            if (ref.getObj(KEYS.ACTIVE).checkBool(STD_BOOLS.INVISIBLE_BUFF))
                visible = false;
        } catch (Exception e) {
        }
        if (getName().contains(StringMaster.INVISIBLE_BUFF))
            visible = false;
    }

    @Override
    public BuffType getType() {
        return (BuffType) super.getType();
    }

    public Effects getEffects() {
        if (!(effect instanceof Effects))
            effect = new Effects(effect);
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

        main.system.auxiliary.LogMaster.log(0, "BUFF EFFECT (" + toString() + ") applied to "
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
        if (basis != null)
            ref.setID(KEYS.BASIS, basis.getId());
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
        if (isDead())
            return false;

        // if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.BUFF_BEING_REMOVED,
        // REF)))
        // return false;

        setDead(true);
        game.getManager().buffRemoved(this);
        if (dispelEffects != null)
            dispelEffects.apply(ref
                    // Ref.getSelfTargetingRefCopy(ref.getSourceObj())
            );
        // game.fireEvent(new Event(STANDARD_EVENT_TYPE.BUFF_REMOVED, REF));
        return true;

    }

    @Override
    protected void addDynamicValues() {
        setParam(G_PARAMS.DURATION, duration);
        setParam(G_PARAMS.C_DURATION, getIntParam(G_PARAMS.DURATION));
    }

    @Override
    public String getToolTip() {

        if (counterName == null)
            if (!getProperty(G_PROPS.CUSTOM_PROPS).isEmpty())
                counterName = getProperty(G_PROPS.CUSTOM_PROPS);

        if (counterName != null) {
            return super.getToolTip() + " (" + basis.getCounter(counterName) + ")";
        }
        if (!permanent)
            return super.getToolTip() + " duration: " + duration;
        return super.getToolTip();
    }

    @Override
    public void clicked() {
        MessageManager.alert(getDescription() + "\n remaining duration: " + duration);
    }

    @Override
    public String getDescription() {
        if (StringMaster.isEmpty(super.getDescription()))
            return getName() + " with " + getEffect();
        return super.getDescription();
    }

    @Override
    public int getDuration() {
        return getIntParam(G_PARAMS.C_DURATION);
    }

    public void setDuration(int duration) {

        this.duration = duration;
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

    @Override
    public int tick() {

        if (permanent)
            return duration;
        duration--;
        setParam(G_PARAMS.C_DURATION, duration);
        modifyParameter(G_PARAMS.TURNS_IN_GAME, 1);

        main.system.auxiliary.LogMaster.log(1, getName() + " ticked! duration: " + duration);
        checkDuration();

        return duration;
    }

    public boolean checkDuration() {
        if (duration <= 0) {
            kill();
            return false;
        }
        return true;
    }

    @Override
    public boolean checkRetainCondition() {
        try {
            if (ref.getSourceObj().isDead()) {
                if (checkBool(STD_BOOLS.SOURCE_DEPENDENT))
                    kill();
                if (getActive().checkBool(STD_BOOLS.SOURCE_DEPENDENT))
                    kill();

            }
        } catch (Exception e) {
        }

        if (retainConditions != null) {
            if (!retainConditions.check(ref)) {
                main.system.auxiliary.LogMaster.log(LOG_CHANNELS.BUFF_DEBUG,
                        "Retain conditions check false for " + getName());
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
        if (!permanent)
            duration += amount;
    }

    @Override
    public void remove() {
        kill();

    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean isDispelable() {
        return super.checkBool(STD_BOOLS.DISPELABLE);
    }

    public boolean isStacking() {
        return super.checkBool(STD_BOOLS.STACKING);
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
        if (buffType == null)
            buffType = new EnumMaster<BUFF_TYPE>().retrieveEnumConst(BUFF_TYPE.class,
                    getProperty(G_PROPS.BUFF_TYPE));
        return buffType;
    }

}
