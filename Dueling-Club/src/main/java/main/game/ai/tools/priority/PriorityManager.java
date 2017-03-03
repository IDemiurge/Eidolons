package main.game.ai.tools.priority;

import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.containers.RollEffect;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Costs;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;

import java.util.List;

/**
 * Created by JustMe on 2/15/2017.
 */
public interface PriorityManager {

    int getPriorityForActionSequence(ActionSequence as);

    ActionSequence chooseByPriority(List<ActionSequence> actions);

    void setPriorities(List<ActionSequence> actions);

    int getPriority(ActionSequence sequence);



    //custom formula may be set for action/spell in AV!
    int evaluatePriorityFormula(Action action, String property);

    //not the same as danger!
    float calculateCapacity(Unit unit);

    int getCostFactor(Costs cost, Unit unit);

    int getParamPriority(PARAMETER p, Unit unit);

    int getSpellPriority(GOAL_TYPE type, Action action);

    int getSelfSpellPriority(Action action);

    void addEffectPriority(Action action, Effect e);

    int getSpellCustomHostilePriority(Action action);

    void applyResistPenalty(Action action);

    boolean isResistApplied(Action action);

    int getSpellCustomHostileEffectPriority(DC_Obj target, DC_ActiveObj action,
                                            Effect e);

    int getSummonPriority(Action action);

    void setBasePriority(int i);

    void applyRollPriorityMod(RollEffect rollEffect);

    int getRollPriorityMod(RollEffect rollEffect);

    int getRetreatPriority(ActionSequence as);

    float getCowardiceFactor(Unit coward);

    int getCowerPriority(Unit unit);

    int getStealthPriority(Action action);

    int getDangerFactorByMemory(Unit unit2); //TODO

    int getZoneSpellPriority(Action action, boolean damage);

    int getSearchPriority(ActionSequence as);

    int getParamModSpellPriority(Action action);

    int getParamModSpellPriority(Action action, Boolean buff);

    int getDurationMultiplier(Action action);

    boolean applyParamModPriority(DC_Obj target, Effect e, PARAMETER param,
                                  int amount);

    int getCounterModSpellPriority(Action action);

    int getAttackPriority(ActionSequence as);

    int getCoatingPriority(DC_ActiveObj active, DC_Obj targetObj);

    int getItemPriority(DC_Obj targetObj);

    int getAttackPriority(DC_ActiveObj active, Unit targetObj);

    int getSpecialEffectsPriority(SPECIAL_EFFECTS_CASE CASE, Unit source,
                                  Unit target);

    int getSpecialEffectPriority(Effect e);

    int getAttackOfOpportunityPenalty(DC_ActiveObj action, Unit targetObj);

    void applySequenceLengthPenalty(ActionSequence as);

    void applyCostPenalty(ActionSequence as);

    int getCounterPenalty(Unit targetObj);

    int getDamagePriority(DC_ActiveObj action, Obj targetObj);

    int getDamagePriority(DC_ActiveObj action, Obj targetObj, boolean attack);

    boolean checkKillPrioritized(Obj targetObj, DC_ActiveObj action);

    int getLethalDamagePriority();

    int getUnitPriority(Obj targetObj);

    int getUnitPriority(Obj targetObj, Boolean less_or_more_for_health);

    int getUnitPriority(UnitAI unit_ai, Obj targetObj, Boolean less_or_more_for_health);

    int getHealthFactor(Obj targetObj, Boolean less_or_more_for_health);

    int getModePriority(Action a);

    int getAlertPriority(Unit unit);

    int getWaitPriority(Action action);

    int getDefendPriority(UnitAI unit_ai);

    int getModePriority(Unit unit, STD_MODES mode);

    int getMeleeDangerFactor(Unit unit);

    int getMeleeDangerFactor(Unit unit, boolean adjacentOnly, boolean now);

    int getMeleeThreat(Unit enemy);

    int getMeleeThreat(Unit enemy, boolean now);


    int getCastingPriority(Unit unit);




    public enum AI_EFFECT_PRIORITIZING {
        ATTACK, DAMAGE, BUFF, PARAM_MOD, COUNTER_MOD, SUMMON, MODE, BEHAVIOR_MODE,
    }

    public enum PRIORITY_FUNCS {
        DURATION, DANGER, CAPACITY,
    }
}
