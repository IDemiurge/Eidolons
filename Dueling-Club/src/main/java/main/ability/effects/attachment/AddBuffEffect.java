package main.ability.effects.attachment;

import main.ability.effects.*;
import main.ability.effects.continuous.ContinuousEffect;
import main.content.CONTENT_CONSTS.RETAIN_CONDITIONS;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.text.TextParser;

import java.util.HashMap;
import java.util.Map;

public class AddBuffEffect extends MultiEffect implements OneshotEffect, ResistibleEffect,
 ReducedEffect,
 ContainerEffect {

    public static final String dummyBuffType = BuffObj.DUMMY_BUFF_TYPE;
    public static final String EMPTY_BUFF_NAME = "@";
    protected BuffType buffType;
    protected Integer duration;
    protected Formula durationFormula;
    // protected Effect effect;
    private Conditions retainConditions;
    private String buffTypeName;
    private Obj active;
    private BuffObj buff;
    private Map<Integer, BuffObj> buffCache;
    private Integer resistanceMod;
    private boolean isTransient;
    private Integer baseDuration;

    public AddBuffEffect(Effect effect) {
        this("", effect);
    }

    public AddBuffEffect(String buffTypeName, Effect effect) {
        this.effect = effect;

        this.setBuffTypeName(buffTypeName);
    }

    public AddBuffEffect(String buffName, Effect effect, Integer duration) {
        this(buffName, effect, duration == 0 ? null : new Formula("" + duration));
    }

    public AddBuffEffect(String buffTypeName, Effect effect, Formula durationFormula) {
        this(buffTypeName, effect);
        this.durationFormula = durationFormula;
    }

    public AddBuffEffect(Condition retainCondition, String buffTypeName, Effect effect) {
        this(buffTypeName, effect);
        this.setRetainConditions(new Conditions(retainCondition));
    }

    @OmittedConstructor
    public AddBuffEffect(BuffType buffType, Effect effect) {
        this(effect);
        this.buffType = buffType;
    }

    @AE_ConstrArgs(argNames = {"buffType", "effect", "isTransient"})
    public AddBuffEffect(String buffType, Effect effect, Boolean isTransient) {
        this(buffType, effect);
        this.isTransient = isTransient;
    }

    @Override
    public void setForcedLayer(Integer forcedLayer) {
        effect.setForcedLayer(forcedLayer);
    }

    @Override
    public boolean applyThis() {
        if (!getGame().getEffectManager().checkNotResisted(this)) {
            return false;
        }
        // if (buff == null)
        buff = getBuffCache().get(target);
        if (buff != null) {
            buff.setDuration(buff.getIntParam(G_PARAMS.DURATION));
            buff.setDead(false);
            if (!buff.getBasis().hasBuff(buff.getName()) || checkStacking()) {
                game.getManager().buffCreated(buff, buff.getBasis());
            } else // TODO and if it's continuous???
                if (buff.checkBool(GenericEnums.STD_BOOLS.DURATION_ADDED)) // TODO for spells
                {
                    buff.setDuration(baseDuration + duration);
                }
            // apply thru? Or do all normal spells reconstruct anyway?
            return true;
        }
        try {
            setActive(game.getObjectById(ref.getId(Ref.KEYS.ACTIVE)));
            if (getActive() == null) {
                setActive(game.getObjectById(ref.getId(KEYS.SKILL)));
            }
            if (getActive() == null) {
                setActive(game.getObjectById(ref.getId(KEYS.SPELL)));
            }
            if (getActive() == null) {
                setActive(game.getObjectById(ref.getId(KEYS.ABILITY)));
            }
            if (getActive() == null) {
                setActive(ref.getSourceObj());
            }
        } catch (Exception ignored) {
        }
        getBuffTypeLazily();
        // preCheck if continuous wrapping required
        effect = ContinuousEffect.transformEffectToContinuous(effect);
        if (forcedLayer != null) {
            effect.setForcedLayer(forcedLayer);
        }
        if ((getActive() instanceof DC_ActiveObj)) {
            ((DC_ActiveObj) getActive()).setContinuous(true);
        }
        initDuration();
        baseDuration = duration;
        initRetainConditions();
        ref.setBasis(target);


        buffType.setTransient(isTransient());
        buff = (BuffObj) game.createBuff(buffType, active, ref.getSourceObj().getOwner(), ref,
         effect, duration, getRetainConditions());
        if (getAnimation() != null) {
            getAnimation().addPhaseArgs(PHASE_TYPE.BUFF, buff);
        }

        getBuffCache().put(target, buff);
        return buff != null;

    }

    private boolean checkStacking() {
        if (ref.getActive() != null) {
            if (ref.getActive().checkBool(GenericEnums.STD_BOOLS.STACKING)) {
                return true;
            }
        }
        return buff.checkBool(GenericEnums.STD_BOOLS.STACKING);
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
    }

    public BuffType getBuffTypeLazily() {
        if (buffType != null) {
            return buffType;
        }

        this.buffType = (BuffType) DataManager.getType(getBuffTypeName(), DC_TYPE.BUFFS);
        if (buffType == null) {
            if (getBuffTypeName() == null) {
                return new BuffType();
            }
            boolean invisible = getBuffTypeName().contains(StringMaster.INVISIBLE_BUFF_CODE);
            if (TextParser.checkHasRefs(getBuffTypeName())) {
                ref.setID(KEYS.INFO, ref.getId(KEYS.ABILITY));
                String parsedName = TextParser.parse(getBuffTypeName(), ref,
                 TextParser.BUFF_PARSING_CODE);
                setBuffTypeName(parsedName);
            }
            buffType = new BuffType(DataManager.getType(dummyBuffType, DC_TYPE.BUFFS));
            boolean empty = StringMaster.isEmpty(getBuffTypeName());
            if (!empty) {
                empty = StringMaster.isEmpty(getBuffTypeName().trim());
            }
            if (!empty) {
                empty = EMPTY_BUFF_NAME.equals(getBuffTypeName().trim());
            }
            if (empty) {
                setBuffTypeName(getActive().getName());
            }
            buffType.setProperty(G_PROPS.NAME, buffTypeName);
            Obj spell = ref.getObj(KEYS.SPELL);
            if (spell != null) {
                buffType.setProperty(G_PROPS.BUFF_TYPE, GenericEnums.BUFF_TYPE.SPELL.toString());
                if (spell.checkBool(GenericEnums.STD_BOOLS.NON_DISPELABLE)) {
                    buffType.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.NON_DISPELABLE.toString());
                }
                buffType.setImage(spell.getProperty(G_PROPS.IMAGE));
            }
            buffType.setImage(getActive().getProperty(G_PROPS.IMAGE));

            if (invisible) {
                buffType.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.INVISIBLE_BUFF.toString());
            }
            // DataManager.addType(buffType); //what for?
        }
        return buffType;
    }

    private void initRetainConditions() {
        if (ref.getActive() == null) {
            return;
        }
        String prop = ref.getActive().getProperty(PROPS.RETAIN_CONDITIONS, false);
        for (String s : StringMaster.open(prop)) {
            RETAIN_CONDITIONS template = new EnumMaster<RETAIN_CONDITIONS>().retrieveEnumConst(
             RETAIN_CONDITIONS.class, s);
            Condition condition;
            if (template != null) {
                condition = DC_ConditionMaster.getRetainConditionsFromTemplate(template, ref);
            } else {
                condition = ConditionMaster.toConditions(s);
            }
            if (condition != null) {
                getRetainConditions().add(condition);
            }

        }

    }

    public int initDuration() {
        if (isTransient()) {
            duration = 0;
            return 0;
        }
        if ((getActive() instanceof DC_ActiveObj)) {
            if (getActive().getIntParam(G_PARAMS.DURATION) != 0) {
                duration = getActive().getIntParam(G_PARAMS.DURATION);
            }
        }
        if (duration == null) {
            if (durationFormula != null) {
                duration = durationFormula.getInt(ref);
            } else {
                this.duration = buffType.getDuration();
            }
        }
        if (duration == null) {
            duration = ContentManager.INFINITE_VALUE;
        }
        // TODO if (checkSpellBuff()) ++
        if (duration != ContentManager.INFINITE_VALUE) {
            if (getResistanceMod() != null) {
                duration = MathMaster.applyMod(duration, getResistanceMod());
            }
            duration = MathMaster.addFactor(duration, ref.getSourceObj().getIntParam(
             PARAMS.DURATION_MOD));
            duration += ref.getSourceObj().getIntParam(PARAMS.DURATION_BONUS);
        }
        return duration;
    }

    @Override
    public Effect getCopy() {
        if (!isCopied()) {
            setCopied(true);
            return this;
        }
        AddBuffEffect copy = (AddBuffEffect) super.getCopy();
        copy.setBuffCache(getBuffCache());

        return copy;
    }

    @Override
    public String toString() {
        String string = "Buff effect: ";
        if (effect == null) {
            string += effect.toString();
        }
        return string;

    }

    public String getBuffTypeName() {
        return buffTypeName;
    }

    public void setBuffTypeName(String buffTypeName) {
        this.buffTypeName = buffTypeName;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    public void setBuffType(BuffType buffType) {
        this.buffType = buffType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Formula getDurationFormula() {
        return durationFormula;
    }

    public void setDurationFormula(Formula durationFormula) {
        this.durationFormula = durationFormula;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Condition getRetainCondition() {
        return getRetainConditions();
    }

    public void setRetainCondition(Condition retainCondition) {
        this.setRetainConditions(new Conditions(retainCondition));
    }

    public Obj getActive() {
        return active;
    }

    public void setActive(Obj active) {
        this.active = active;
    }

    public BuffObj getBuff() {
        return buff;
    }

    public void setBuff(BuffObj buff) {
        this.buff = buff;
    }

    public Map<Integer, BuffObj> getBuffCache() {
        if (buffCache == null) {
            buffCache = new HashMap<>();
        }
        return buffCache;
    }

    public void setBuffCache(Map<Integer, BuffObj> buffCache) {
        this.buffCache = buffCache;
    }

    @Override
    public Integer getResistanceMod() {
        return resistanceMod;
    }

    @Override
    public void setResistanceMod(int mod) {
        this.resistanceMod = mod;

    }

    public Conditions getRetainConditions() {
        if (retainConditions == null) {
            retainConditions = new Conditions();
        }
        return retainConditions;
    }

    public void setRetainConditions(Conditions retainConditions) {
        this.retainConditions = retainConditions;
    }

    private boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean b) {
        this.isTransient = b;
    }

}
