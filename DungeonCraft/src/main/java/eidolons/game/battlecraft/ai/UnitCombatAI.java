package eidolons.game.battlecraft.ai;

import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.advanced.companion.CompanionMaster;
import eidolons.game.battlecraft.ai.anew.AiImpulse;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.tools.AiExecutor;
import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eidolons.content.consts.VisualEnums.*;
import static eidolons.game.battlecraft.ai.UnitAI.DEFAULT_VERBATIM_MOD;

/**
 * Created by JustMe on 10/17/2018.
 */
public class UnitCombatAI {

    AI_TYPE type;
    AiExecutor executor;
    AI_BEHAVIOR_MODE currentBehavior;
    Unit unit;
    private int engagementDuration;
    private boolean engaged;
    private List<Action> forcedActions = new ArrayList<>();
    private Map<ObjType, Integer> actionPriorityMods;
    private Map<ObjType, Integer> actionPriorityBonuses;
    private boolean ordered;
    private boolean free;
    private INTENT_ICON intentIcon;
    private ActionSequence lastSequence;
    private AiImpulse impulse;

    public UnitCombatAI(Unit unit) {
        this.unit = unit;
    }

    private void initType() {
        type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
                .getProperty(PROPS.AI_TYPE));
        if (unit.isMine() || type == null) {
            CompanionMaster.initCompanionAiParams(unit);
        }
    }

    private void initActionPriorities() {
        actionPriorityBonuses = RandomWizard.constructWeightMap(unit
                .getProperty(PROPS.ACTION_PRIORITY_BONUSES), C_OBJ_TYPE.ACTIVE);
        actionPriorityMods = RandomWizard.constructWeightMap(unit
                .getProperty(PROPS.ACTION_PRIORITY_MODS), C_OBJ_TYPE.ACTIVE);

        for (ObjType spell : DataManager.toTypeList(unit.getProperty(PROPS.VERBATIM_SPELLS),
                DC_TYPE.SPELLS)) {
            Integer mastery = unit.getIntParam(ContentValsManager.getSpellMasteryForSpell(spell));

            // WHY VERBATIM? MAYBE FROM *TYPE*, YES...
            int mod = DEFAULT_VERBATIM_MOD + mastery;
        }
        // default additions?
        /*
         * verbatim spell list per mastery
         */
    }

    public AI_TYPE getType() {
        if (type == null) {
            initType();
        }
        return type;
    }

    public void setType(AI_TYPE type) {
        this.type = type;
    }

    public AiExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(AiExecutor executor) {
        this.executor = executor;
    }

    public AI_BEHAVIOR_MODE getCurrentBehavior() {
        return currentBehavior;
    }

    public void setCurrentBehavior(AI_BEHAVIOR_MODE currentBehavior) {
        this.currentBehavior = currentBehavior;
    }

    public int getEngagementDuration() {
        return engagementDuration;
    }

    public void setEngagementDuration(int engagementDuration) {
        this.engagementDuration = engagementDuration;
    }

    public boolean isEngaged() {
        return engaged;
    }

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }

    public List<Action> getForcedActions() {
        return forcedActions;
    }

    public void setForcedActions(List<Action> forcedActions) {
        this.forcedActions = forcedActions;
    }

    public Map<ObjType, Integer> getActionPriorityMods() {
        return actionPriorityMods;
    }

    public void setActionPriorityMods(Map<ObjType, Integer> actionPriorityMods) {
        this.actionPriorityMods = actionPriorityMods;
    }

    public Map<ObjType, Integer> getActionPriorityBonuses() {
        return actionPriorityBonuses;
    }

    public void setActionPriorityBonuses(Map<ObjType, Integer> actionPriorityBonuses) {
        this.actionPriorityBonuses = actionPriorityBonuses;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public INTENT_ICON getIntentIcon() {
        return intentIcon;
    }

    public void setIntentIcon(INTENT_ICON intentIcon) {
        this.intentIcon = intentIcon;
    }

    public void setLastSequence(ActionSequence lastSequence) {
        if (lastSequence == null) {
            setIntentIcon(null);
        } else
            setIntentIcon(initIntent(lastSequence.getCurrentAction()));
        this.lastSequence = lastSequence;
    }

    private INTENT_ICON initIntent(Action currentAction) {
        AiEnums.GOAL_TYPE type = currentAction.getTask().getType();
        if (type == null) {
            if (currentAction.getActive().isMove()) {
                return INTENT_ICON.ATTACK;
            }
//            if (currentAction.getActive().isTurn()) {
//                return AtbPanel.INTENT_ICON.ATTACK;
//            }
        }
        if (type != null)
            switch (type) {
                case ATTACK:
                    return INTENT_ICON.ATTACK;
                case BUFF:
                case SELF:
                case DEBUFF:
                case RESTORE:
                case DEBILITATE:
                case SUMMONING:
                case ZONE_DAMAGE:
                case CUSTOM_HOSTILE:
                case CUSTOM_SUPPORT:
                case ZONE_SPECIAL:
                    return INTENT_ICON.SPELL;
                case RETREAT:
                case MOVE:
                case APPROACH:
                    return INTENT_ICON.MOVE;
                case WAIT:
                    return INTENT_ICON.WAIT;
                case PREPARE:
                    return INTENT_ICON.PREPARE;
                case DEFEND:
                    return INTENT_ICON.DEFEND;
                case PROTECT:
                    break;
                case SEARCH:
                    return INTENT_ICON.SEARCH;
            }
        return INTENT_ICON.UNKNOWN;
    }

    public ActionSequence getLastSequence() {
        return lastSequence;
    }

    public AiImpulse getImpulse() {
        return impulse;
    }

    public void setImpulse(AiImpulse impulse) {
        this.impulse = impulse;
    }
}
