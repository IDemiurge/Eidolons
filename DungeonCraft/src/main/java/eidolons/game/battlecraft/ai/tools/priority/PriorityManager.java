package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.ability.effects.Effect;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.Obj;

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
    int evaluatePriorityFormula(AiAction aiAction, String property);

    //not the same as danger!
    float calculateCapacity(Unit unit);


    int getSpellPriority(GOAL_TYPE type, AiAction aiAction);

    int getSelfSpellPriority(AiAction aiAction);

    void addEffectPriority(AiAction aiAction, Effect e);

    int getSpellCustomHostilePriority(AiAction aiAction);

    void applyResistPenalty(AiAction aiAction);

    boolean isResistApplied(AiAction aiAction);

    int getSpellCustomHostileEffectPriority(DC_Obj target, ActiveObj action,
                                            Effect e);

    int getSummonPriority(AiAction aiAction);

    void setBasePriority(int i);

    void applyRollPriorityMod(RollEffect rollEffect);

    int getRollPriorityMod(RollEffect rollEffect);

    int getRetreatPriority(ActionSequence as);

    float getCowardiceFactor(Unit coward);

    int getCowerPriority(Unit unit);

    int getZoneSpellPriority(AiAction aiAction, boolean damage);

    int getSearchPriority(ActionSequence as);

    int getParamModSpellPriority(AiAction aiAction);

    int getParamModSpellPriority(AiAction aiAction, Boolean buff);

    int getDurationMultiplier(AiAction aiAction);

    float getParamModFactor(DC_Obj target, Effect e, PARAMETER param,
                            int amount);

    int getCounterModSpellPriority(AiAction aiAction);

    int getAttackPriority(ActionSequence as);

    int getCoatingPriority(ActiveObj active, DC_Obj targetObj);

    int getItemPriority(DC_Obj targetObj);

    int getAttackPriority(ActiveObj active, BattleFieldObject targetObj);

    int getCounterPenalty(ActiveObj active, Unit targetObj);

    int getDamagePriority(ActiveObj action, Obj targetObj);

    int getDamagePriority(ActiveObj action, Obj targetObj, boolean attack);

    boolean checkKillPrioritized(Obj targetObj, ActiveObj action);

    int getLethalDamagePriority();

    int getUnconsciousDamagePriority();

    int getUnitPriority(Obj targetObj);

    int getUnitPriority(Obj targetObj, Boolean less_or_more_for_health);

    int getUnitPriority(UnitAI unit_ai, Obj targetObj, Boolean less_or_more_for_health);

    int getHealthFactor(Obj targetObj, Boolean less_or_more_for_health);

    int getModePriority(AiAction a);

    int getAlertPriority(Unit unit);

    int getWaitPriority(AiAction aiAction);

    int getDefendPriority(UnitAI unit_ai);

    int getModePriority(Unit unit, STD_MODES mode);

    AiMaster getMaster();

}
