package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.continuous.BehaviorModeEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.ability.effects.oneshot.mechanic.DrainEffect;
import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.ability.effects.oneshot.unit.RaiseEffect;
import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.content.DC_ContentManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiQuickItemAction;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.ai.tools.target.SpellAnalyzer;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.AttackOfOpportunityRule;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.master.BuffMaster;
import eidolons.system.DC_Formulas;
import eidolons.system.math.DC_CounterMaster;
import eidolons.system.math.roll.RollMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.ability.effects.common.OwnershipChangeEffect;
import main.ability.effects.oneshot.InstantDeathEffect;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.mode.STD_MODES;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.Param;
import main.content.values.properties.G_PROPS;
import main.data.ConcurrentMap;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import main.system.math.Formula;
import main.system.math.FuncMaster;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;
import main.system.text.TextParser;

import java.util.*;

/**
 * Created by JustMe on 2/15/2017.
 */
public class PriorityManagerImpl extends AiHandler implements PriorityManager {

    private Unit unit;
    private UnitAI unit_ai;
    private float modifier;
    private int priority;
    private BEHAVIOR_MODE behaviorMode;
    private LOG_CHANNEL logChannel = LOG_CHANNEL.AI_DEBUG;
    private Map<Effect, RollEffect> rollMap;

    public PriorityManagerImpl(AiMaster master) {
        super(master);
    }

    @Override
    public int getPriorityForActionSequence(ActionSequence as) {
        priority = 0;
        modifier = 0;
        // compare damage or default priority on non-damage actions (formula?)
        //
        setUnit(as.getAi().getUnit());
        GOAL_TYPE goal = as.getType();
        initLogChannel(goal);
        Action action = as.getLastAction();
        try {
            action.getActive().getGame().getEffectManager().setEffectRefs(
             action.getActive().getAbilities(), action.getRef());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (!action.getActive().getProperty(PROPS.AI_PRIORITY_FORMULA).isEmpty()) {
            setBasePriority(evaluatePriorityFormula(action, action.getActive().getProperty(
             PROPS.AI_PRIORITY_FORMULA)));
            if (EffectFinder.check(action.getActive(), RollEffect.class)) {
                try {
                    applyRollPriorityMod((RollEffect) EffectFinder.getEffectsOfClass(
                     action.getActive(), RollEffect.class).get(0));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }

            applyResistPenalty(action);
        } else {
            switch (goal) {
                case PROTECT:
                    setBasePriority(getGuardPriority(action));
                    break;
                case ATTACK:
                    setBasePriority(getAttackPriority(as));
                    break;
                case WAIT:
                    setBasePriority(getWaitPriority(action));
                    break;
                case DEFEND:
                case PREPARE:
                    setBasePriority(getModePriority(action));
                    break;
                case SUMMONING:
                    setBasePriority(getSummonPriority(action));
                    break;
                case AUTO_DAMAGE:
                case ZONE_DAMAGE:
                case AUTO_DEBUFF:
                case AUTO_BUFF:
                case SELF:
                case BUFF:
                case DEBUFF:
                case RESTORE:
                case DEBILITATE:
                case CUSTOM_HOSTILE:
                case CUSTOM_SUPPORT:
                    setBasePriority(getSpellPriority(goal, action));
                    break;
                case STEALTH:
                    setBasePriority(getStealthPriority(action));
                    break;
                case SEARCH:
                    // TODO sight range/detection factors,
                    setBasePriority(getSearchPriority(as));
                    break;
                case COWER:
                    setBasePriority(getCowerPriority(getUnit()));
                    break;
                case RETREAT:
                    setBasePriority(getRetreatPriority(as));
                    break;
                case COATING:
                    setBasePriority(getCoatingPriority(action.getActive(), action.getTarget()));
                    break;
                case AGGRO:
                case AMBUSH:
                case APPROACH:
                case STAND_GUARD:
                case MOVE:
                case OTHER:
                case PATROL:
                case STALK:
                case WANDER:
                    priority = 100;
                    break;
                case ZONE_SPECIAL:

                default:
                    setBasePriority(getSpellPriority(goal, action));
                    break;
            }
        }
        if (priority <= 0) {
            if (goal == GOAL_TYPE.ATTACK)
                LogMaster.log(1, "ATK FAILED" + priority + " priority for " + as);
            else
                LogMaster.log(1, priority + " priority for " + as);
            return priority;
        }
//        Integer bonus = unit_ai.getActionPriorityBonuses().get(action.getActive().getName());
//        if (bonus != null) {
//            priority += bonus;
//        }

        as.setPriority(priority);

        Integer mod = as.getPriorityMultiplier();
        mod += (int) (modifier);
        if (mod != null) {
            priority = MathMaster.applyMod(priority, mod);
        }
        LogMaster.log(1, "AI: " + priority + " priority for " + as);
        return priority;

    }

    private int getGuardPriority(Action action) {
        if (getUnitAi().getType() == AI_TYPE.ARCHER
         || getUnitAi().getType() == AI_TYPE.CASTER
         || getUnitAi().getType() == AI_TYPE.SNEAK
         || getUnitAi().getType() == AI_TYPE.CASTER_SUMMONER
         || getUnitAi().getType() == AI_TYPE.CASTER_OFFENSE)
            return 0;
        Coordinates c = action.getTarget().getCoordinates(); //TODO targets should be cells, then be allies there...
        // it's harder for when allies move, eh?
        // >> Add "remove guard" priority penalty
        Collection<Unit> list = game.getUnitsForCoordinates(c);
        float factor = ParamPriorityAnalyzer.getUnitLifeFactor(getUnit()) - 100;
//        getCounterPenalty()
//        getParamAnalyzer().getParamPriority()
        for (Unit unit : list) {
            if (unit.isAlliedTo(getUnit().getOwner())) {
                float danger = getSituationAnalyzer().getDangerFactor(unit);
                if (unit.isUnconscious())
                    danger *= 1.25f;
                factor += danger;
            }
        }
        float mod = unit.getIntParam(PARAMS.N_OF_COUNTERS) * 5;
        mod += unit.getIntParam(PARAMS.C_N_OF_COUNTERS) * 2;
        mod += unit.getIntParam(PARAMS.COUNTER_MOD) / 3 +
         unit.getIntParam(PARAMS.COUNTER_ATTACK_MOD) / 3;

        if (getUnitAi().getType() == AI_TYPE.TANK)
            mod *= 1.5f;
        if (getUnitAi().getType() == AI_TYPE.GUARD)
            mod *= 2f;

        factor = factor * mod;

        return Math.round(factor * getConstValue(AiConst.GOAL_PROTECT));
    }

    private void applyCustomPriorityMethod(PRIORITY_FUNCS func, Object... args) {
        switch (func) {
            case DANGER_TO_ALLY:
                Unit enemy = (Unit) game.getObjectById((Integer) args[1]);
                Unit ally = (Unit) args[0];
                int threat = getThreatAnalyzer().getThreat(ally.getAI(), enemy);
                applyMultiplier(threat, "protect danger");
        }
    }

    @Override
    public int getCowerPriority(Unit unit) {
        int p = Math.round(300 * getCowardiceFactor(unit));
        return p;
    }

    @Override
    public int getStealthPriority(Action action) {
        if (action.getActive() instanceof DC_UnitAction) {
            DC_UnitAction unitAction = (DC_UnitAction) action.getActive();
            if (unitAction.getModeEffect() != null) {
                if (unitAction.getModeEffect().getMode().equals(STD_MODES.STEALTH)) {
                    /*
                     * preCheck detection inevitable?
					 */
                    return 300;
                    // should add to the max target priority...
                }

                if (unitAction.getModeEffect().getMode().equals(STD_MODES.HIDE)) {
                    return 200; // ++ wounds
                }
            }
        }
        return 0;
    }

    @Override
    public int evaluatePriorityFormula(Action action, String property) {

        Boolean less_or_more_for_health = null;
        for (PRIORITY_FUNCS p : PRIORITY_FUNCS.values()) {
            String text = StringMaster.wrapInBraces(p.name().toLowerCase());
            if (!property.contains(text)) {
                continue;
            }
            float i = 1;
            switch (p) {
                case DURATION:
                    i = new Float(getDurationMultiplier(action)) / 100;
                    break;
                case CAPACITY:
                    i = calculateCapacity(action.getSource());
                    break;
                case DANGER:
                    i = getSituationAnalyzer().getMeleeDangerFactor(getUnit(), false, true) / 100;
                    break;
            }

            property = property.replace(text, "" + i);
        }
        if (property.contains("[less]")) {
            less_or_more_for_health = true;
            property = property.replace("[less]", "");
        }
        if (property.contains("[more]")) {
            less_or_more_for_health = false;
            property = property.replace("[more]", "");
        }
        if (less_or_more_for_health != null) {
            int unitPriority = getUnitPriority(action.getTarget(), less_or_more_for_health);
            property = property.replace("{target_power}", "" + unitPriority);
        }
        Integer p = new Formula(property).getInt(action.getRef());
        return p;
    }

    @Override
    public float calculateCapacity(Unit unit) {
        float capacity;
        float sta_factor = 1;
        if (!ParamAnalyzer.isStaminaIgnore(unit)) {
            sta_factor = new Float(unit.getIntParam(PARAMS.STAMINA_PERCENTAGE))
             / MathMaster.PERCENTAGE;
        }
        float foc_factor = 1;
        if (!ParamAnalyzer.isFocusIgnore(unit)) {
            foc_factor = new Float(unit.getIntParam(PARAMS.FOCUS_PERCENTAGE))
             / MathMaster.PERCENTAGE;
        }

        capacity = foc_factor * sta_factor;
        // if (UnitAnalyzer.checkIsCaster(unit))
        // capacity*=(1-new Float(unit.getIntParam(PARAMS.ESSENCE_PERCENTAGE))
        // / MathManager.PERCENTAGE)*UnitMaster.getSpellXpPercentage(unit)/100;
        // ++ count durability of items?
        return capacity;
    }

    public int getSpellPriority(DC_SpellObj spell, Context context) {
        return getSpellPriority(AI_SpellMaster.getGoal(spell),
         AiActionFactory.newAction(spell, context));
    }

    @Override
    public int getSpellPriority(GOAL_TYPE type, Action action) {
        switch (type) {

            case ZONE_DAMAGE:
            case AUTO_DAMAGE:
                priority = getZoneSpellPriority(action, true);
                if (action.getSource().getAiType() == AiEnums.AI_TYPE.CASTER) {
                    applyMultiplier(150, "AI_TYPE.CASTER");
                }
                if (action.getSource().getAiType() == AiEnums.AI_TYPE.CASTER_OFFENSE) {
                    applyMultiplier(200, "AI_TYPE.CASTER_OFFENSE");
                }
                break;
            case SELF:
                priority = getSelfSpellPriority(action);
                break; // could be a lot ...
            // ++ rolls effect, ++ behavior modes,
            case BUFF:
            case DEBUFF:

                priority = getParamModSpellPriority(action, true);
                if (action.getSource().getAiType() == AiEnums.AI_TYPE.CASTER_SUPPORT) {
                    applyMultiplier(150, "AI_TYPE.CASTER_SUPPORT");
                }
                break;
            case RESTORE:
            case DEBILITATE:
                if (SpellAnalyzer.isCounterModSpell(action.getActive())) {
                    priority = getCounterModSpellPriority(action);
                } else {
                    priority = getParamModSpellPriority(action, false);
                }
                if (action.getSource().getAiType() == AiEnums.AI_TYPE.CASTER_SUPPORT) {
                    applyMultiplier(150, "AI_TYPE.CASTER_SUPPORT");
                }
                break;
            case CUSTOM_HOSTILE:
                priority = getSpellCustomHostilePriority(action);
                break;

        }
        if (getUnit().getAiType().isCaster()) {
            priority *= 3;
        }
        return priority;
    }

    @Override
    public int getSelfSpellPriority(Action action) {
        Effects effects = null;
        if (action instanceof AiQuickItemAction)
            effects = EffectFinder.getEffectsFromSpell(
             ((DC_QuickItemAction) action.getActive()).getItem().getActives().get(0));
        else effects =
         EffectFinder.getEffectsFromSpell(action.getActive());
        if (effects.getEffects().isEmpty())
            return 0;

        setBasePriority(getUnitPriority(getUnit(), false));
        for (Effect e : effects) {
            addEffectPriority(action, e);
        }
        return priority;
    }

    @Override
    public void addEffectPriority(Action action, Effect e) {
        if (e instanceof AddBuffEffect) {
            getParamModSpellPriority(action, true);
        }
        if (e instanceof ModifyValueEffect) {
            getParamModSpellPriority(action, false); // separate?
        }
        if (e instanceof ModifyCounterEffect) {
            getCounterModSpellPriority(action);
        }
    }

    @Override
    public int getSpellCustomHostilePriority(Action action) {
        Effects effects = EffectFinder.getEffectsFromSpell(action.getActive());
        // TODO targets???
        for (Effect e : effects) {
            addConstant(getSpellCustomHostileEffectPriority(action.getTarget(), action.getActive(),
             e), e.toString());
        }
        applyResistPenalty(action);
        return priority;
    }

    @Override
    public void applyResistPenalty(Action action) {
        if (isResistApplied(action)) {
            int factor = ParamPriorityAnalyzer.getResistanceFactor(action);
            addMultiplier(factor, "Resistance factor");
        }
    }

    @Override
    public boolean isResistApplied(Action action) {
        if (action.getActive().getProperty(PROPS.RESISTANCE_TYPE).equalsIgnoreCase(
         SpellEnums.RESISTANCE_TYPE.IRRESISTIBLE + "")) {
            return false;
        }
        return !action.getTarget().isOwnedBy(action.getSource().getOwner());
    }

    @Override
    public int getSpellCustomHostileEffectPriority(DC_Obj target, DC_ActiveObj action,
                                                   Effect e) {
        if (e instanceof AddBuffEffect) {
            AddBuffEffect buffEffect = (AddBuffEffect) e;
            // duration
            int mod = 100;
            // mod =
            // getDurationPriorityMod(buffEffect.getDurationFormula().getInt(action.getRef()));
            return getSpellCustomHostileEffectPriority(target, action, buffEffect.getEffect())
             * mod / 100;
        }
        if (e instanceof Effects) {
            Effects effects = (Effects) e;
            int p = 0;
            for (Effect eff : effects) {
                p += getSpellCustomHostileEffectPriority(target, action, eff);
            }
            return p;
        }
        if (e instanceof RollEffect) {
            RollEffect rollEffect = (RollEffect) e;
            int mod = getRollPriorityMod(rollEffect);
            return getSpellCustomHostileEffectPriority(target, action, rollEffect.getEffect())
             * mod / 100;
        }
        if (e instanceof InstantDeathEffect) {
            return 2 * getUnitPriority(target, true);
        }
        if (e instanceof BehaviorModeEffect) {
            int duration = new Formula(action.getParam(G_PARAMS.DURATION)).getInt(action.getRef());
            BehaviorModeEffect behaviorModeEffect = (BehaviorModeEffect) e;
            switch (behaviorModeEffect.getMode()) {
                case BERSERK:
                    return getUnitPriority(target, true) * (Math.min(4, duration / 5 * 3));
                case CONFUSED:
                    return getUnitPriority(target, true) * (Math.min(2, duration / 2));
                case PANIC:
                    return getUnitPriority(target, true) * (Math.min(3, duration / 3 * 2));
                default:
                    break;
            }

        }
        if (e instanceof OwnershipChangeEffect) {
            int duration = new Formula(action.getParam(G_PARAMS.DURATION)).getInt(action.getRef());
            return getUnitPriority(target, true) * (Math.min(5, duration));

        }
        return 0;
    }

    private void initLogChannel(GOAL_TYPE goal) {
        switch (goal) {
            case ATTACK:
            case DEFEND:
            case MOVE:
            case PREPARE:
            case RESTORE:
            case WAIT:
                logChannel = LOG_CHANNEL.AI_DEBUG;
                break;
            default:
                logChannel = LOG_CHANNEL.AI_DEBUG2;
                break;
        }

    }

    @Override
    public int getSummonPriority(Action action) {
        // TODO precalculate summoned unit power, its initiative, its
        // positioning...
        ObjType summoned = AI_SpellMaster.getSummonedUnit(action.getActive(), action.getRef());

        Integer unitXp = 0;
        if (summoned == null) {
            return 0;
        }
        try {
            SummonEffect summonEffect = (SummonEffect) EffectFinder.getEffectsOfClass(
             action.getActive().getAbilities(), SummonEffect.class).get(0);
            unitXp = summonEffect.getSummonedUnitXp().getInt(action.getRef());

            if (summonEffect instanceof RaiseEffect) {
                RaiseEffect raiseEffect = (RaiseEffect) summonEffect;
                unitXp += raiseEffect.getCorpse().getIntParam(PARAMS.TOTAL_XP) / 5;
                // TODO for items!
                Obj weapon = raiseEffect.getCorpse().getRef().getObj(KEYS.WEAPON);
                if (weapon != null) {
                    unitXp += weapon.getIntParam(PARAMS.GOLD_COST) / 5;
                }
                Obj armor = raiseEffect.getCorpse().getRef().getObj(KEYS.ARMOR);
                if (armor != null) {
                    unitXp += armor.getIntParam(PARAMS.GOLD_COST) / 5;
                }
                Obj weapon2 = raiseEffect.getCorpse().getRef().getObj(KEYS.OFFHAND);
                if (weapon2 != null) {
                    unitXp += weapon2.getIntParam(PARAMS.GOLD_COST) / 5;
                }
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        int power = summoned.getIntParam(PARAMS.POWER) + unitXp / DC_Formulas.POWER_XP_FACTOR;
        setBasePriority(power);

        if (summoned.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {

        }

        // getUnitPriority(targetObj)

        // try to getOrCreate as close to enemies as possible...
        Coordinates coordinate = action.getRef().getTargetObj().getCoordinates();
        boolean prefer_melee = UnitAnalyzer.isMeleePreferred(summoned);
        if (prefer_melee) {
            prefer_melee = UnitAnalyzer.isOffensePreferred(summoned);
        }
        // TODO special for forcefield - add consts per enemy path blocked
        for (Entity entity : !prefer_melee ? Analyzer.getAllies(getUnitAi()) : Analyzer
         .getAdjacentEnemies(getUnit(), false)) {
            // getOrCreate distance?
            if (entity instanceof Obj) {
                Obj obj = (Obj) entity;
                int distance = Math.max(1, PositionMaster.getDistance(coordinate, obj
                 .getCoordinates()));
                // if (prefer_melee)
                addMultiplier(getUnitPriority(obj) / distance, obj.getName() + "'s proximity");
            }
        }
        // create unit quietly? precalc its best moves?
        applyMultiplier(getConstInt(AiConst.SUMMON_PRIORITY_MOD), "SUMMON_PRIORITY_MOD");
        // priority = MathManager.applyMod(priority, SUMMON_PRIORITY_MOD);//
        // into a
        // AI_SUMMON_PRIORITY_MOD!
        return priority;
    }

    @Override
    public void setBasePriority(int i) {
        // main.system.auxiliary.LogMaster.log(getLogChannel(),
        // "Base Priority set: " + i);
        priority = i;

    }

    @Override
    public void applyRollPriorityMod(RollEffect rollEffect) {
        int perc = RollMaster.getRollChance(rollEffect.getRollType(), rollEffect.getSuccess(),
         rollEffect.getFail(), rollEffect.getRef());
        if (perc < 100) {
            applyMultiplier(perc, rollEffect.getRollType() + " Roll");
        }
    }

    @Override
    public int getRollPriorityMod(RollEffect rollEffect) {
        return RollMaster.getRollChance(rollEffect.getRollType(), rollEffect.getSuccess(),
         rollEffect.getFail(), rollEffect.getRef());
    }

    @Override
    public int getRetreatPriority(ActionSequence as) {
        float cowardice_factor = getCowardiceFactor(as.getAi().getUnit());
        if (cowardice_factor == 0) { // panic?
            priority = 0;
            return priority;
        }
        if (getUnitAi().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.PANIC) {
            cowardice_factor *= 2;
        }

        int currentMeleeDangerFactor = 0;
        try {
            currentMeleeDangerFactor = getSituationAnalyzer().getMeleeDangerFactor(getUnit(), false, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (currentMeleeDangerFactor == 0 && getUnitAi().getBehaviorMode() != AiEnums.BEHAVIOR_MODE.PANIC) {
            priority = 0;
            return priority;
        }
        Coordinates c = getUnit().getCoordinates();
        getUnit().setCoordinates(as.getLastAction().getTarget().getCoordinates());
        int meleeDangerFactor = 0;
        try {
            meleeDangerFactor = getSituationAnalyzer().getMeleeDangerFactor(getUnit(), false, false);
            // meleeDangerFactor =getDangerFactorByMemory(unit); TODO for panic!

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getUnit().setCoordinates(c);
        }
        priority = currentMeleeDangerFactor - meleeDangerFactor;
        if (priority <= 0) {
            return 0;
        }
        if (getUnitAi().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.PANIC) {
            addConstant(250, "Panic");
        }
        priority = MathMaster.round(priority * cowardice_factor * getConstInt(AiConst.RETREAT_PRIORITY_FACTOR));

        // if (meleeDangerFactor != 0)
        // priority = priority / meleeDangerFactor;
        // else if (unit_ai.getBehaviorMode() != BEHAVIOR_MODE.PANIC)
        // priority = 0;
        // preCheck melee threat if unit were on this cell?
        // preCheck ranged threat? hide behind obstacles...
        return priority;
    }

    @Override
    public float getCowardiceFactor(Unit coward) {
        // ARCHER/CASTER!
        int mod = 100;
        if (coward.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            mod = 200;
        }
        if (coward.getAiType() == AiEnums.AI_TYPE.CASTER) {
            mod = 150;
        }
        // 10/spirit
        if (coward.getIntParam(PARAMS.C_MORALE) < 0) {
            mod += -coward.getIntParam(PARAMS.C_MORALE);
        }
        if (coward.checkAiMod(AI_MODIFIERS.COWARD)) {
            mod = mod * 3 / 2;
        }

        int spirit = coward.getIntParam(PARAMS.SPIRIT);
        float factor = 10 / new Float((Math.sqrt(spirit) + new Float(spirit) / 3)) * mod / 100;

        return factor;
    }

    @Override
    public int getDangerFactorByMemory(Unit unit2) {
        // for (DC_HeroObj enemy: Analyzer.getEnemies(unit2, false)){
        // memoryMaps.getOrCreate(unit_ai).getOrCreate(enemy);TODO
        // }
        return 0;
    }

    @Override
    public int getZoneSpellPriority(Action action, boolean damage) {
        int base_priority = 0;
        DC_ActiveObj active = action.getActive();
        Targeting targeting = active.getTargeting();

        if (targeting instanceof FixedTargeting || targeting instanceof SelectiveTargeting) {
            targeting = TargetingMaster.getZoneEffect(active);
        }
        // Set<Obj> objects = targeting.getFilter().getObjects(action.getRef());
        Ref REF = action.getRef().getCopy();
        targeting.select(REF);
        List<Obj> objects = (REF.getGroup() != null) ? REF.getGroup().getObjects()
         : new ArrayList<>(targeting.getFilter().getObjects(action.getRef()));
        for (Obj obj : objects) { // getZoneTargets(active)
            // TODO
            if (obj instanceof Unit) {
                if (obj.isNeutral() || obj.isDead()) {
                    continue;
                }
                Unit target = (Unit) obj;
                int p = (damage) ? getDamagePriority(active, target, false)
                 : getParamModSpellPriority(action);
                Boolean less_or_more_for_health = null;
                if (p == 200) {
                    less_or_more_for_health = null;// ?
                }
                p = getUnitPriority(target, less_or_more_for_health) * p / 100;

                boolean ally = target.isOwnedBy(getUnit().getOwner());
                if (ally) {
                    if (action.getSource().checkAiMod(AI_MODIFIERS.CRUEL)) {
                        p /= 2;
                    }
                    if (action.getSource().checkAiMod(AI_MODIFIERS.MERCIFUL)) {
                        p *= 2;
                    }
                    base_priority -= p;
                } else {
                    if (action.getSource().checkAiMod(AI_MODIFIERS.TRUE_BRUTE)) {
                        p *= 2;
                    }
                    base_priority += p;
                    base_priority += base_priority / 6;
                }
            }
        }
        return base_priority;
    }

    @Override
    public int getSearchPriority(ActionSequence as) {
        // TODO Auto-generated method stub
        return 25;
    }

    @Override
    public int getParamModSpellPriority(Action action) {
        return getParamModSpellPriority(action, null);
    }

    @Override
    public int getParamModSpellPriority(Action action, Boolean buff) {
        DC_ActiveObj spell = action.getActive();
        DC_Obj target = action.getTarget();

        if (buff == null) {
            buff = EffectFinder.check(spell.getAbilities(), AddBuffEffect.class);
        }
        if (buff) {
            if (!spell.checkBool(GenericEnums.STD_BOOLS.STACKING)) {
                try {
                    List<ObjType> buffsFromSpell = BuffMaster.getBuffsFromSpell(spell);
                    if (buffsFromSpell.isEmpty()) {
                        priority = 0;
                        return 0;
                    }
                    ObjType objType = buffsFromSpell.get(0);
                    if (!objType.checkBool(GenericEnums.STD_BOOLS.STACKING)) {
                        if (target.hasBuff(objType.getName())) {
                            priority = 0;
                            return 0;
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

        int priority = (getUnitPriority(target, false));

        boolean ally = target.getOwner().equals(getUnit().getOwner());
        // boolean mod = EffectMaster.preCheck(spell.getAbilities(),
        // ModifyValueEffect.class);
        List<Effect> effects = EffectFinder.getEffectsOfClass(spell.getAbilities(),
         (buff) ? AddBuffEffect.class : ModifyValueEffect.class);
        if (buff) {
            List<Effect> list = new ArrayList<>();
            for (Effect e : effects) {
                list.addAll(EffectFinder.getBuffEffects(e, ModifyValueEffect.class));
            }
            effects = list; // TODO count the duration from buffEffect
        }
        initRollMap(spell, effects);
        boolean valid = false;
        for (Effect e : effects) {
            ModifyValueEffect valueEffect = (ModifyValueEffect) e;
            for (String sparam : StringMaster.open(valueEffect.getParamString())) {
                for (PARAMETER param : DC_ContentManager.getParams(sparam)) {
                    //TODO apply generic fix!
                    if (param == PARAMS.C_INITIATIVE_BONUS)
                        param = PARAMS.C_INITIATIVE;

                    if (TextParser.checkHasValueRefs(valueEffect.getFormula().toString())) {
                        String parsed = TextParser.parse(valueEffect.getFormula().toString(),
                         action.getRef(), TextParser.ACTIVE_PARSING_CODE);
                        parsed = TextParser.replaceCodes(parsed);
                        valueEffect.setFormula(new Formula(parsed));

                    }
                    int amount = valueEffect.getFormula().getInt(action.getRef());

                    if (valueEffect.getMod_type() == MOD.MODIFY_BY_PERCENT) {
                        amount = MathMaster.getFractionValueCentimal(target.getIntParam(param,
                         valueEffect.getFormula().toString()
                          .contains(StringMaster.BASE_CHAR)), amount);

                    }
                    boolean drain = (e instanceof DrainEffect);

                    int final_value = target.getIntParam(param) + amount;
                    int min_max = valueEffect.initMinMaxAmount(target, amount);
                    if (min_max != Integer.MAX_VALUE && min_max != Integer.MIN_VALUE) {
                        if (amount >= 0) {

                            if (final_value > min_max) {
                                amount = min_max - target.getIntParam(param);
                            }
                        } else {
                            if (amount < min_max) {
                                amount = target.getIntParam(param) - min_max;
                            }
                        }
                    }
                    if (!ally && !drain) {
                        amount = -amount;
                    }
                    priority = (int) (priority *
                     getParamModFactor(target, e, param, amount));
                    if (drain) {
                        // TODO limit the amount!
                        priority =
                         (int) (priority *
                          getParamModFactor(getUnit(), null, param, amount));
                    }
                }
            }

            if (!buff) {
                if (!ally && valid) {
                    applyResistPenalty(action);
                }
            } else {
                priority = priority * (getDurationMultiplier(action)) / 100;
            }
        }
        if (!valid) {
            return 0;
//            applyMultiplier(0, "Empty");
        }
        return priority;
    }

    @Override
    public int getDurationMultiplier(Action action) {
        int multiplier;
        int duration = new Formula(action.getActive().getParam(G_PARAMS.DURATION)).getInt(action
         .getRef());
        duration = MathMaster
         .addFactor(duration, ParamPriorityAnalyzer.getResistanceFactor(action));
        multiplier = (int) Math.sqrt(duration *
         getConstInt(AiConst.GEN_SPELL_DURATION_SQRT_MULTIPLIER))
         + getConstInt(AiConst.GEN_SPELL_DURATION_MULTIPLIER);
        return multiplier;
    }

    @Override
    public float getParamModFactor(DC_Obj target, Effect e, PARAMETER param,
                                   int amount) {
        boolean valid = false;
        RollEffect roll = rollMap.get(e);
        if (roll != null) {
            roll.getRef().setTarget(target.getId());
        }
        float numericPriority = 0;
        if (!ParamPriorityAnalyzer.isParamIgnored(param, target))
            try {
                numericPriority = getParamPriority(param);
            } catch (Exception ex) {
                main.system.ExceptionMaster.printStackTrace(ex);
            }
//         ParamPriorityAnalyzer.getParamNumericPriority(param, target);
        int mod = 100;
        if (roll != null) {
            mod = getRollPriorityMod(roll);
        }
        int multiplier = Math.abs(Math.round(numericPriority * (amount) * mod / 100));
        if (multiplier != 0) {
            valid = true;
        }

        int percentagePriority = 0;
        if (!ParamAnalyzer.isParamIgnored(getUnit(), param)) {
            percentagePriority = (int) getPriorityConstantMaster().getParamPriority(param)
             * amount;
        }
        int percentage = MathMaster.getCentimalPercentage(amount, target.getIntParam(param));


        multiplier += MathMaster.getFractionValueCentimal(percentagePriority, percentage) * mod / 100;


        return new Float(multiplier) / 100;
    }

    private void initRollMap(DC_ActiveObj spell, List<Effect> effects) {
        rollMap = new ConcurrentMap<>();

        List<RollEffect> rollEffects = EffectFinder.getRollEffects(spell);
        for (RollEffect roll : rollEffects) {
            for (Effect e : effects) {
                Effect effect = roll.getEffect();
                if (effect instanceof Effects) {
                    Effects rolledEffects = (Effects) effect;
                    for (Effect e1 : rolledEffects.getEffects()) {
                        rollMap.put(e1, roll);
                    }
                    break;
                }
                if (effect == e) {
                    rollMap.put(e, roll);
                    break;
                }

            }
        }
    }

    @Override
    public int getCounterModSpellPriority(Action action) {
        // preCheck immunity? find counter rule by name and preCheck....

        List<Effect> effects = EffectFinder.getEffectsOfClass(action.getActive().getAbilities(),
         ModifyCounterEffect.class);
        initRollMap(action.getActive(), effects);
        for (Effect e : effects) {
            if (e instanceof ModifyCounterEffect) {
                ModifyCounterEffect modifyCounterEffect = (ModifyCounterEffect) e;
                float mod = DC_CounterMaster.getCounterPriority(modifyCounterEffect
                 .getCounterName(), action.getTarget());
                if (rollMap.get(e) != null) {
                    mod = mod * 100 / getRollPriorityMod(rollMap.get(e));
                }

                int amount = modifyCounterEffect.getFormula().getInt(action.getRef());
                setBasePriority(getUnitPriority(action.getTarget(), true));
                addMultiplier(Math.round(mod * amount), modifyCounterEffect.getCounterName()
                 + StringMaster.wrapInParenthesis(amount + ""));
            }
        }
        applyResistPenalty(action);

        return priority;
    }

    @Override
    public int getAttackPriority(ActionSequence as) {
        Action action = as.getLastAction();
        if (action.getTarget() instanceof DC_Cell) {
            return 0;
        }
        Unit targetObj = (Unit) action.getTarget();
        DC_ActiveObj active = action.getActive();
        return getAttackPriority(active, targetObj);

    }

    @Override
    public int getCoatingPriority(DC_ActiveObj active, DC_Obj targetObj) {
        List<Effect> effects = EffectFinder.getEffectsOfClass(active, ModifyCounterEffect.class);
        if (effects.isEmpty()) {
            return priority;
        }
        ModifyCounterEffect e = (ModifyCounterEffect) effects.get(0);
        setBasePriority(getUnitPriority(getUnit(), true));
        addConstant(getItemPriority(targetObj), targetObj.getName());
        Integer amount = e.getFormula().getInt(active.getRef());
        int mod = amount;
        mod *= DC_CounterMaster.getCounterPriority(e.getCounterName(), targetObj);
        addMultiplier(mod, e.getCounterName() + " coating "
         + StringMaster.wrapInParenthesis(amount + ""));
        if (targetObj.getCounter(e.getCounterName()) > 0) {
            addMultiplier(-75 * targetObj.getCounter(e.getCounterName()) / amount, e
             .getCounterName()
             + " - Already Coated "
             + StringMaster.wrapInParenthesis(targetObj.getCounter(e.getCounterName()) + ""));
        }
        // TODO if (quick) if (ammo) if (alreadyCoated)
        return priority;
    }

    @Override
    public int getItemPriority(DC_Obj targetObj) {
        return targetObj.getIntParam(PARAMS.GOLD) / 5;
    }

    @Override
    public int getAttackPriority(DC_ActiveObj active, BattleFieldObject targetObj) {

        if (getUnit().getBehaviorMode() != AiEnums.BEHAVIOR_MODE.BERSERK
         && getUnit().getBehaviorMode() != AiEnums.BEHAVIOR_MODE.CONFUSED) {
            if (targetObj.isOwnedBy(active.getOwnerObj().getOwner())) {
                return -10000;
            }
        }
        boolean attack = !(active instanceof DC_SpellObj);
        int enemy_priority = getUnitPriority(targetObj);
        int priority = enemy_priority;
        int damage_priority = getDamagePriority(active, targetObj, attack);
        if (active.isThrow()) {
            if (damage_priority < getUnitPriority(targetObj) * 2) {
                return -1;// TODO
            }
        }
        // getActionNumberFactor

        int counter_penalty = 0;
        if (active.getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            // if (AttackOfOpportunityRule.checkAction(active)) {
            // counter_penalty = getAttackOfOpportunityPenalty(active,
            // targetObj);
            // }

        } else {
            if (!active.isRanged() && targetObj.canCounter() &&
             !getUnit().checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)
             && !active.checkProperty(G_PROPS.STANDARD_PASSIVES,
             UnitEnums.STANDARD_PASSIVES.NO_RETALIATION.getName())) {
                if ((damage_priority != getLethalDamagePriority()
                 && damage_priority != getUnconsciousDamagePriority())
                 || targetObj.checkPassive(UnitEnums.STANDARD_PASSIVES.FIRST_STRIKE)) {
                    counter_penalty = getCounterPenalty(active, (Unit) targetObj);
                }
            }
        }
        // TODO ++ counter penalty

        // TODO spells!
        if (active instanceof DC_SpellObj) {
            //check mod effects too
            Action action = AiActionFactory.newAction(active,
             new Context(getUnit(), targetObj));
            damage_priority +=
             getSpellPriority(GOAL_TYPE.DEBILITATE, action);
            damage_priority +=
             getSpellPriority(GOAL_TYPE.DEBUFF,
              action);
            if (getUnit().getAiType().isCaster()) {
                damage_priority *= 4;
            }

        } else {
            if (active.isRanged()) {
                if (getUnit().getAiType() == AiEnums.AI_TYPE.ARCHER) {
                    damage_priority *= 2;
                }
            } else if (getUnit().getAiType() == AiEnums.AI_TYPE.BRUTE) {
                damage_priority *= 2;
            }
        }

        damage_priority =
         Math.max(getConstInt(AiConst.DAMAGE_PERCENTAGE_MOD_MINIMUM), damage_priority - counter_penalty);

        priority = priority * damage_priority / 100;

        priority += getConstInt(
         (active instanceof DC_SpellObj) ?
          AiConst.DEFAULT_SPELL_ATTACK_PRIORITY :
          AiConst.DEFAULT_ATTACK_PRIORITY);
        // applyCostPenalty(as);

        // TODO spec effects!!!

        if (active.isThrow()) {
            try {
                applyThrowPenalty(active);
                priority = this.priority;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                addMultiplier(-50, "throw");
            }

        }

//    TODO     int p = getSpecialEffectsPriority(SPECIAL_EFFECTS_CASE.ON_ATTACK, active.getOwnerObj(),
//         targetObj);

        if (targetObj.isNeutral()) {
            addMultiplier(-90, "Neutral");
        }
        return priority;
    }

    private void applyThrowPenalty(DC_ActiveObj active) {
        DC_WeaponObj item = null;
        boolean offhand = active.isOffhand();
        boolean quick = active instanceof DC_QuickItemAction;
        if (quick) {
            DC_QuickItemAction itemActiveObj = (DC_QuickItemAction) active;
            item = itemActiveObj.getItem().getWrappedWeapon();
        } else {
            item = getUnit().getWeapon(offhand);
        }
        if (item == null) {
            return;
        }
        int factor = (int) -Math.sqrt(item.getIntParam(PARAMS.GOLD_COST))
         * 50;
        if (!quick) {
            factor *= 2;
        }
        if (!offhand) {
            factor *= 2;
        }
        addConstant(factor, "item thrown");
        if (priority > 0) {
            addMultiplier(factor / 5, "item thrown");
        }
    }

    @Override
    public int getSpecialEffectsPriority(SPECIAL_EFFECTS_CASE CASE, Unit source,
                                         Unit target) {
        int p = 0;
        if (source.getSpecialEffects() != null) {
            if (source.getSpecialEffects().get(CASE) != null) {
                Effect e = source.getSpecialEffects().get(CASE);
                if (e instanceof Effects) {
                    Effects effects = (Effects) e;
                    for (Effect ef : effects) {
                        p += getSpecialEffectPriority(e);
                    }
                }
            }
        }
        return p;
    }

    @Override
    public int getSpecialEffectPriority(Effect e) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getAttackOfOpportunityPenalty(DC_ActiveObj action, Unit targetObj) {
        int penalty = 0;
        List<DC_ActiveObj> list = AttackOfOpportunityRule.getAttacks(action);

        for (DC_ActiveObj a : list) {
            penalty -= getDamagePriority(a, getUnit()) / 2;
        }

        return penalty;
    }


    @Override
    public int getCounterPenalty(DC_ActiveObj active, Unit targetObj) {
        // TODO cache!
        Active action = game.getActionManager().getCounterAttackAction(getUnit(), targetObj, active);
        if (action == null)
            return 0;
        return Math.round(-getDamagePriority(
         (DC_ActiveObj) action, getUnit())
         * getConstInt(AiConst.COUNTER_FACTOR));
    }

    @Override
    public int getDamagePriority(DC_ActiveObj action, Obj targetObj) {
        return getDamagePriority(action, targetObj, true);
    }

    @Override
    public int getDamagePriority(DC_ActiveObj action, Obj targetObj, boolean attack) {
        int damage = 0;
        List<Effect> effects = EffectFinder.getEffectsOfClass(action, (attack) ? AttackEffect.class
         : DealDamageEffect.class);
        initRollMap(action, effects);
        for (Effect e : effects) {
            try {
                int mod = 100;
                RollEffect roll = rollMap.get(e);
                if (roll != null) {
                    roll.getRef().setTarget(targetObj.getId());

                    mod = getRollPriorityMod(roll);
                }
                damage += FutureBuilder.getDamage(action, targetObj, e) * mod / 100;

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        float mod = getConstValue(AiConst.DAMAGE_PRIORITY_MOD);

        if (DamageCalculator.isUnconscious(damage, targetObj)) {
            if (checkKillPrioritized(targetObj, action)) {
                return getUnconsciousDamagePriority() * (int) (mod * 100) / 100;
            }
        }
        if (checkKillPrioritized(targetObj, action)) {
            if (DamageCalculator.isLethal(damage, targetObj)) {
                int p = getLethalDamagePriority();
                if (targetObj instanceof Unit) {
                    if (((Unit) targetObj).isUnconscious())
                        p = getConstInt(AiConst.LETHAL_DAMAGE_MOD_VS_UNCONSCIOUS);
                }
                return p * (int) (mod * 100) / 100;
            }
        }
        int e = targetObj.getIntParam(PARAMS.C_ENDURANCE);
        int t = targetObj.getIntParam(PARAMS.C_TOUGHNESS);
        e = MathMaster.getCentimalPercentage(damage, e);
        t = MathMaster.getCentimalPercentage(damage, t);
//TODO unconscious rule specifics
        return damage + Math.max(e, t * 2 / 3) * (int) (mod * 100) / 100;
    }

    @Override
    public boolean checkKillPrioritized(Obj targetObj, DC_ActiveObj action) {
        if (targetObj.isNeutral()) {
            return false;
        }
        // preCheck if action deals exceeding damage?
        // if (PositionMaster.getDistance(targetObj, unit) > 1) {
        // if (Analyzer.getEnemies((DC_HeroObj) targetObj, false).size() > 0) {
        // // melee threat instead?
        //
        // return false;
        // }
        // }// preCheck if wounded
        return true;
    }

    @Override
    public int getLethalDamagePriority() {
        return getConstInt(AiConst.LETHAL_DAMAGE_MOD);
    }

    @Override
    public int getUnconsciousDamagePriority() {
        return getConstInt(AiConst.UNCONSCIOUS_DAMAGE_MOD);
    }

    @Override
    public int getUnitPriority(Obj targetObj) {
        return getUnitPriority(targetObj, null); // TODO TODO !!!
    }

    @Override
    public int getUnitPriority(Obj targetObj, Boolean less_or_more_for_health) {
        return getUnitPriority(getUnitAi(), targetObj, less_or_more_for_health);
    }

    @Override
    public int getUnitPriority(UnitAI unit_ai, Obj targetObj, Boolean less_or_more_for_health) {
        BattleFieldObject target_unit = (BattleFieldObject) targetObj;
        if (targetObj instanceof DC_HeroAttachedObj) {
            DC_HeroAttachedObj attachedObj = (DC_HeroAttachedObj) targetObj;
            targetObj = attachedObj.getOwnerObj();
        }
        Integer basePriority = targetObj.getIntParam(PARAMS.POWER);
        if (unit_ai != null) {
            if (unit_ai.getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
                basePriority = 100;
            } else if (targetObj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                if (!Analyzer.isBlockingMovement(unit_ai.getUnit(), (Unit) targetObj)) {
                    return 0;
                }
                basePriority = 20;
            }
        }
        Integer healthMod = 100;
        if (less_or_more_for_health != null) {
            healthMod = getHealthFactor(targetObj, less_or_more_for_health);
        } else {
            if (targetObj instanceof Unit)
                if (((Unit) targetObj).isUnconscious()) {
                    return Math.round(
                     basePriority * getConstValue(AiConst.UNCONSCIOUS_UNIT_PRIORITY_MOD)); // [QUICK FIX] - more subtle?
                }
        }
        basePriority = basePriority * healthMod / 100;
        return basePriority;// TODO
        // limit?
    }

    @Override
    public int getHealthFactor(Obj targetObj, Boolean less_or_more_for_health) {
        Integer healthMod = 100;
        int end = targetObj.getIntParam(PARAMS.ENDURANCE_PERCENTAGE) / MathMaster.MULTIPLIER;
        int tou = targetObj.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE) / MathMaster.MULTIPLIER;
        int mod = Math.min(tou, end);
        if (mod == 0) {
            return (less_or_more_for_health ? 0 : 100);
        }
        int profileMod = ((int) getConstValue(getUnit().isHostileTo((DC_Player) targetObj.getOwner())
         ? AiConst.ENEMY_PRIORITY_HEALTH
         : AiConst.ALLY_PRIORITY_HEALTH));
        if (less_or_more_for_health) {
            healthMod = healthMod * profileMod / (100 + mod);
        } else {

            healthMod = healthMod * profileMod /
             Math.max(1, 100 - mod);
        }
        return healthMod;
    }

    @Override
    public int getModePriority(Action a) {
        STD_MODE_ACTIONS action = new EnumMaster<STD_MODE_ACTIONS>().retrieveEnumConst(
         STD_MODE_ACTIONS.class, a.getActive().getName());
        if (action == null) {
            return 0; // custom modes? TODO
        }
        switch (action) {
            case Concentrate:
                priority = getModePriority(getUnit(), STD_MODES.CONCENTRATION);
                break;
            case Defend:
                priority = getDefendPriority(getUnitAi());
                break;
            case On_Alert:
                priority = getAlertPriority(getUnit());
                break;
            case Meditate:
                priority = getModePriority(getUnit(), STD_MODES.MEDITATION);
                break;
            case Rest:
                priority = getModePriority(getUnit(), STD_MODES.RESTING);
                break;
        }
        return priority;
    }

    @Override
    public int getAlertPriority(Unit unit) {
        return getModePriority(unit, STD_MODES.ALERT);
    }

    @Override
    public int getWaitPriority(Action action) {
        Unit target = (Unit) action.getTarget();
        boolean ally = (target.getOwner().equals(getUnit().getOwner()));
        if (ally) {
            if (getUnitAi().getType() == AiEnums.AI_TYPE.CASTER || getUnitAi().getType() == AiEnums.AI_TYPE.ARCHER) {
                if (!Analyzer.isBlocking(getUnit(), target)) {
                    return 0;
                }
            }
            if (!Analyzer.isBlockingMovement(getUnit(), target)) // if I were sorting
            // continuously even
            // as I calculate, I
            // could reference
            // current top
            // priority action
            // to see what to
            // wait for
            {
                return 0;
            }
        } else {
            if (getSituationAnalyzer().getMeleeDangerFactor(getUnit()) != 0) {
                return 0;
            }
            if (Analyzer.checkRangedThreat(target)) {
                return 0;
            } //TODO check if engaged alone!
            if (getUnit().checkInSightForUnit(target)) {
                return 0;
            }

        }

        int ap = action.getSource().getIntParam(PARAMS.C_N_OF_ACTIONS) - 1;
        // differentiate between waiting on enemy and ally?
        int base_priority = getConstInt(AiConst.WAIT_PRIORITY_FACTOR) * ap;

        int factor = (ally) ? 100 : 50;

        priority = MathMaster.getFractionValueCentimal(base_priority, factor);

        if (!ally) {
            addMultiplier(100 - ParamPriorityAnalyzer.getUnitLifeFactor(getUnit()), "Unit life factor");

            addMultiplier(getThreatAnalyzer().getMeleeThreat(target), "Enemy melee threat");

        } else {
            addMultiplier(100 - ParamPriorityAnalyzer.getUnitLifeFactor(target), "ally life factor");
        }
        // preCheck if ally is blocking move/view
        // relevance factor -> "need to take your place" OR
        // "waiting for you to come closer"

        return priority;
    }

    @Override
    public int getDefendPriority(UnitAI unit_ai) {
        // subtract current ap

        int base_priority = unit_ai.getType() == AiEnums.AI_TYPE.TANK ? 75 : 25;
        return 5 + base_priority * getSituationAnalyzer().getMeleeDangerFactor(getUnit()) / 100;
    }

    @Override
    public int getModePriority(Unit unit, STD_MODES mode) {
        PARAMETER p = ContentManager.getPARAM(mode.getParameter());
        if (p == null) {
            return 0;
        }
        Integer percentage = unit.getIntParam(ContentManager.getPercentageParam(new Param(
         ContentManager.getBaseParameterFromCurrent(p))))
         / MathMaster.MULTIPLIER;
        int base = 100 - percentage; // how important/good it is for
        // this unit
        int paramModifier = getParamAnalyzer().getParamPriority(p, unit); // how important/good is it now
        int dangerModifier = 100;
        switch (mode) {
            case MEDITATION:
                if (ParamAnalyzer.isEssenceIgnore(unit))
                    return 0;
                base = getSituationAnalyzer().getCastingPriority(unit);
            case CONCENTRATION:
            case RESTING:
                dangerModifier = getSituationAnalyzer().getMeleeDangerFactor(unit, false, true);
                dangerModifier -= getSituationAnalyzer().getRangedDangerFactor(unit);
                addMultiplier(base, "param percentage missing");
                base = base * getRestorationPriorityMod(unit) / 100;
                break;
            default:
                return 0;
        }

//        addMultiplier(base, "param percentage missing");
        // ++ Life factor?
        int timeModifier = 100 + getSituationAnalyzer().getTimeModifier()
         - getUnit().getParamPercentage(PARAMS.N_OF_ACTIONS);
        addMultiplier(timeModifier, "time");
        applyMultiplier(paramModifier, "param Modifier");
        applyMultiplier(dangerModifier, "danger factor");
        return base;
        // modifier * (DEFAULT_PRIORITY - percentage) / factor;
    }

    private int getRestorationPriorityMod(Unit unit) {
        int mod = getConstInt(AiConst.DEFAULT_RESTORATION_PRIORITY_MOD);
        mod = (int) (mod * getConstValue(AiConst.SELF_VALUE));
        return mod;
    }

    public void applyMultiplier(int factor, String string) {
        if (factor == 0) {
            priority = 0;

        } else {
            modifier = modifier * (factor) / 100;
            if (priority < 0) {
                priority = 0;
            }
        }
        if (getUnitAi().getLogLevel() >= UnitAI.LOG_LEVEL_FULL) {
            LogMaster.log(getLogChannel(), " Applying [" + string
             + "] multiplier: " + factor + "; priority = " + priority);
        }

    }

    public void addConstant(int constant, String string) {
        priority += (constant);
        if (getUnitAi().getLogLevel() >= UnitAI.LOG_LEVEL_FULL) {
            LogMaster.log(getLogChannel(), " Adding [" + string
             + "] constant: " + constant + "; priority = " + priority);
        }
    }

    private LOG_CHANNEL getLogChannel() {
        return logChannel;
    }

    public void addMultiplier(int factor, String string) {
//        if (factor == 0) {
//            return;
//        }
//        if (priority < 0 && factor < 0) {
//            return;
//        }
        modifier += (factor);
//        if (priority < 0) {
//            priority = 0;
//        }
        if (getUnitAi().getLogLevel() >= UnitAI.LOG_LEVEL_FULL) {
            LogMaster.log(getLogChannel(), " Adding [" + string
             + "] multiplier: " + factor + "; priority = " + priority);
        }
    }

    @Override
    public ActionSequence chooseByPriority(List<ActionSequence> actions) {
        setPriorities(actions);
        if (isApplyConvergingPathsPriorities()) {
            applyConvergingPathsPriorities(actions);
        }
        return getByPriority(actions);
    }

    private boolean isApplyConvergingPathsPriorities() {
        return false;
    }

    public ActionSequence getByPriority(List<ActionSequence> actions) {
        Chronos.mark("Priority sorting ");
        ActionSequence sequence = new FuncMaster<ActionSequence>().
         getGreatest_(actions, a -> a.getPriority());
        Chronos.logTimeElapsedForMark("Priority sorting ");
        return sequence;
    }

    @Override
    public void setPriorities(List<ActionSequence> actions) {
        Chronos.mark("Priority calc");
        setUnit(actions.get(0).getAi().getUnit());
        setUnitAi(actions.get(0).getAi());
        for (ActionSequence as : actions) {
            Integer mod = getUnitAi().getGoalPriorityMod(as.getTask().getType());
            if (mod == null) {
                mod = 0;
            }
            mod += getPriorityModifier().getPriorityModifier(as);
            if (getMetaGoalMaster().isOn())
                try {
                    getUnitAi().setMetaGoals(getMetaGoalMaster().initMetaGoalsForUnit(getUnitAi()));
                    mod += getMetaGoalMaster().getPriorityMultiplier(as);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            as.setPriorityMultiplier(mod);


        }
        for (ActionSequence action : actions) { // into separate method to
            // debug!
            if (action == null) {
                continue;
            }


            // Chronos.mark("Calculating priority for " + action);
            int priority = -1;
            try {
                priority = getPriority(action);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            // Chronos.logTimeElapsedForMark("Calculating priority for " +
            // action);
            action.setPriority(priority);
        }
        Chronos.logTimeElapsedForMark("Priority calc");
    }

    @Override
    public int getPriority(ActionSequence sequence) {
        behaviorMode = getUnitAi().getBehaviorMode();
        if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK) {
            return getPriorityForActionSequence(sequence);
        } else if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
            return RandomWizard.getRandomInt(100);
        }
        return getPriorityForActionSequence(sequence);
    }

    public void applyConvergingPathsPriorities(List<ActionSequence> actions) {
        // TODO make sure that they are not pruned at path-building phase!
        // split into groups and for each group, add bonus per group member!
        Map<Action, List<ActionSequence>> asGroups = new HashMap<>();
        for (ActionSequence as : actions) {
            Action firstAction = as.get(0);
            List<ActionSequence> group = asGroups.get(firstAction);
            if (group == null) {
                group = new ArrayList<>();
                asGroups.put(firstAction, group);
            }
            group.add(as);
        }
        for (Action firstAction : asGroups.keySet()) {
            for (ActionSequence as : asGroups.get(firstAction)) {
                for (ActionSequence as2 : asGroups.get(firstAction)) {
                    if (as2 == as) {
                        continue;
                    }
                    if (as2.getLastAction().getTarget() != null) {
                        if (as2.getLastAction().getTarget().equals(as.getLastAction().getTarget())) {
                            continue;
                        }
                    }
                    int bonus = MathMaster.round(as2.getPriority()
                     / asGroups.get(firstAction).size() * (getConstInt(AiConst.CONVERGING_FACTOR)));
                    LogMaster.log(getLogChannel(), bonus
                     + " Converging Paths bonus added to " + as.getPriority()
                     + as.getActions() + " from " + as2.getPriority() + as2.getActions());
                    as.setPriority(as.getPriority() + bonus);
                }
            }
        }
    }

    public Comparator<? super ActionSequence> getComparator() {
        return new Comparator<ActionSequence>() {
            @Override
            public int compare(ActionSequence o1, ActionSequence o2) {
                int a1 = o1.getPriority();
                int a2 = o2.getPriority();
                if (a1 == a2) {
                    // return (RandomWizard.random()) ? 1 : -1;
                    return 0;
                }
                if (a1 > a2) {
                    return -1;
                }
                return 1;
            }
        };
    }


    public int getPriorityForEffect(AI_EFFECT_PRIORITIZING aep, Effect e, Action a) {
        DC_Obj target = a.getTarget();
        switch (aep) {
            case COUNTER_MOD:
                break;
            case ATTACK:
                break;

            case DAMAGE:

                // return getdParamModSpellPriority(a);
            case BUFF:
                return getParamModSpellPriority(a, true);
            case PARAM_MOD:
                return getParamModSpellPriority(a);
            case SUMMON:
                return getSummonPriority(a);
            case MODE:
                break;
            case BEHAVIOR_MODE:
                break;

        }
        return 0;
    }

    public Unit getUnit() {
        if (unit != null)
            return unit;
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public UnitAI getUnitAi() {
        if (unit_ai != null)
            return unit_ai;
        return super.getUnitAI();
    }

    public void setUnitAi(UnitAI unit_ai) {
        this.unit_ai = unit_ai;
        setUnit(unit_ai.getUnit());
    }
}
