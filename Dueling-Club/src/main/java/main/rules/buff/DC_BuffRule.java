package main.rules.buff;

import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.Effect;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.rules.DC_RuleImpl;
import main.rules.RuleMaster;
import main.rules.RuleMaster.COMBAT_RULES;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.EffectAnimation;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DC_BuffRule extends DC_RuleImpl {
    protected static final KEYS OBJ_REF = KEYS.SOURCE;
    protected Integer level = -1;
    protected Map<Integer, Conditions> conditionsMap = new HashMap<>();
    protected Unit target;
    Map<Obj, Effect[]> effectCache = new XLinkedMap<>();
    private boolean animationQueued;
    private Map<Obj, Integer> levelCache = new ConcurrentHashMap<>();

    public DC_BuffRule(MicroGame game) {
        super(game);

    }

    @Override
    public String toString() {
        return super.toString() + level;
    }

    public boolean check(Obj obj) {
        if (!applyToBfObjs()) {
            if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                return false;
            }
        }
        Ref ref = obj.getRef().getCopy();
        ref.setMatch(obj.getId());
        ref.setTarget(obj.getId());
        return (conditions.preCheck(ref));
    }

    protected boolean applyToBfObjs() {
        return false;
    }

    public boolean apply(Obj obj) {
        target = (Unit) obj;
        Ref ref = obj.getRef().getCopy();

        ref.setMatch(obj.getId());
        ref.setTarget(obj.getId());
        ref.setBasis(obj.getId());

        if (!check(obj)) {
            return false;
        }
        // checkLogged(obj);
        for (String buffName : getBuffNames()) {
            BuffObj buff = obj.getBuff(buffName);
            if (buff != null) {
                // logged = false;
                buff.kill();
            }
        }

        if (getBuffConditions().preCheck(ref)) {
            if (checkBuffLevel(ref)) {
                if (checkLogged(obj)) {
                    log(obj);
                }

                apply(ref);
            }
        }
        // else
        // effectCache.put(obj, new Effect[getMaxLevel()]);

        return true;
    }

    protected void log(Obj obj) {
        // game.getLogManager().newLogEntryNode(getEntryType(), obj,
        // getLogText(obj, level),
        // isPositive());TODO
        // game.getLogManager().log(getLogText(obj, level));
    }

    protected ENTRY_TYPE getEntryType() {
        switch (getRuleEnum()) {
            case BLEEDING:
                return ENTRY_TYPE.BLEEDING_RULE;
            case FOCUS:
                return ENTRY_TYPE.FOCUS_RULE;
            case MORALE:
                return ENTRY_TYPE.MORALE_RULE;
            case MORALE_KILL:
                return ENTRY_TYPE.MORALE_KILL_RULE;
            case STAMINA:
                return ENTRY_TYPE.STAMINA_RULE;
            case UNCONSCIOUS:
                return ENTRY_TYPE.UNCONSCIOUS;
            case WEIGHT:
                return ENTRY_TYPE.WEIGHT_RULE;
            case WOUNDS:
                return ENTRY_TYPE.WOUNDS_RULE;

        }
        return null;
    }


    protected boolean checkLogged(Obj obj) {

        // return false;

        boolean logged = true;
        try {
            if (effectCache.get(obj) != null) {
                logged = false;
            }
            // else // TODO instead, set the effect's BOOLEAN_APPLIED
            // if (level != null)
            // if (level > 0)
            // if (effectCache.getOrCreate(obj)[level] != null)
            // logged = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logged;
    }

    protected String getLogText(Obj obj, Integer level) {
        return obj.getNameIfKnown() + RuleMaster.getRuleLogText(getRuleEnum(), level);
        // return getBuffTypeName() + " applied to " + obj.getName();
    }

    protected abstract COMBAT_RULES getRuleEnum();

    protected String getEffectFormula() {
        return getEffectFormula(level);
    }

    protected String getEffectFormula(Integer level2) {
        return "";
    }

    protected Condition getBuffConditions() {
        return new Conditions();
    }

    public int getBuffLevel(Unit unit) {
        return levelCache.get(unit);
    }

    public int initBuffLevel(Unit unit) {
        Ref ref = unit.getRef().getCopy();
        ref.setMatch(unit.getId());
        ref.setTarget(unit.getId());
        ref.setBasis(unit.getId());
        boolean result = conditions.preCheck(ref);
        if (!result) {
            return -1;
        }
        return initLevel(ref);
    }

    // protected abstract String getLogText(Integer level);
    @Override
    public void apply(Ref ref) {
        if (level<0)
            return ;
        target = (Unit) ref.getTargetObj();
        getEffect().setAnimationActive(ref.getActive());
        if (checkAnimationDisplayed(ref)) {
            queueAnimation();
        }
        // TODO only animate after Buff Level changes!!!
        super.apply(ref);
        if (animationQueued) {
            playAnimation(ref);
        }
        Effect[] array = effectCache.get(ref.getTargetObj());
        if (array == null) {
            array = new Effect[getMaxLevel() + 1];
        }

        array[level] = effects;

        effectCache.put(ref.getTargetObj(), array);
    }

    protected void playAnimation(Ref ref) {
        DC_ActiveObj action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);
        if (action == null) {
            action = target.getDummyAction();
        }
        EffectAnimation anim = new EffectAnimation(action);
        anim.addPhase(new AnimPhase(PHASE_TYPE.BUFF, ref.getObj(KEYS.BUFF)));
        getGame().getAnimationManager().newAnimation(anim);
        animationQueued = false;
    }

    protected void queueAnimation() {
        animationQueued = true;

    }

    protected boolean checkAnimationDisplayed(Ref ref) {
        if (!checkLogged(ref.getSourceObj())) {
            return false;
        }
        return ref.getGame().isStarted();
    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }

    @Override
    public void initEffects() {
        Effect[] array = effectCache.get(target);
        if (array != null) {
            effects = array[level];
            if (effects != null) {
                return;
            }
        }
        Conditions conditions = null
                // new Conditions(getConditions(level))
                ;
        Effect effect = getEffect();
        effect.setForceStaticParse(false);
        effect.setForcedLayer(Effect.BUFF_RULE);
        effects = new AddBuffEffect(conditions, getBuffTypeName(), effect);
        effects.setIrresistible(true);
        effects.setForcedLayer(Effect.BUFF_RULE);
    }

    protected Condition getConditions(Integer level) {
        Conditions conditions = conditionsMap.get(level);
        if (conditions != null) {
            return conditions;
        }
        conditions = new Conditions(getCondition(level));
        // less or equal than this level
        boolean reverse = isConditionGreater(level);
        conditions.setNegative(reverse);

        // is this really necessary?
        // if (level != ((!reverse) ? getMinLevel() : getMaxLevel())) {
        // Conditions notCondition = new Conditions(getCondition(level
        // + (reverse ? 1 : -1)));
        // conditions.add(
        // // new NotCondition(reverse,
        // notCondition);
        // notCondition.setNegative(!reverse);
        // }

        conditionsMap.put(level, conditions);

        return conditionsMap.get(level);

    }

    protected Condition getCondition(Integer level) {
        return new NumericCondition(true, getFormula(level), StringMaster.getValueRef(OBJ_REF,
                getValue()));
    }

    protected boolean isConditionGreater(Integer level) {
        return false;
    }

    @Override
    public boolean check(Event event) {
        if (!super.check(event)) {
            return false;
        }
        return checkBuffLevel(event.getRef());
    }

    public boolean checkBuffLevel(Ref ref) {
        return initLevel(ref) >= getMinLevel();
    }

    @Override
    public void initConditions() {
        // conditions = new Conditions(getConditions(getMinLevel()));

        conditions = new Conditions(ConditionMaster.getUnit_CharTypeCondition(), ConditionMaster
                .getLivingMatchCondition());

    }

    protected Integer initLevel(Ref ref) {
        level = getMinLevel() - 1;
        for (int i = getMaxLevel(); i >= getMinLevel(); i--) {
            if (getConditions(i).preCheck(ref)) {
                level = i;
            } else {
                // break;
            }
        }
        levelCache.put(ref.getSourceObj(), level);
        return level;
    }

    public Integer getMinLevel() {
        return 0;
    }

    public abstract Integer getMaxLevel();

    @Override
    public void initEventType() {
        event_type = new EventType(CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED, getValue().toString());

    }

    public String getFormula(Integer level) {
        try {
            return getConditionFormulas()[level];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getBuffTypeName() {
        try {
            return getBuffNames()[level];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract Effect getEffect();

    protected abstract VALUE getValue();

    protected abstract String[] getConditionFormulas();

    protected abstract String[] getBuffNames();
}
