package eidolons.game.battlecraft.ai.advanced.companion;

import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;

import java.util.Map;

public class Order {
    ORDER_TYPE type;
    String arg;
    ActionSequence sequence;
    String actionType;
    private Map<GOAL_TYPE, Integer> priorityModsMap;
    private ORDER_PRIORITY_MODS strictPriority;

    public Order(String arg) {
        this.arg = arg;
    }

    public Order(ORDER_TYPE type, String arg) {
        this.arg = arg;
        this.type = type;
    }

    public ORDER_TYPE getType() {
        return type;
    }

    public void setType(ORDER_TYPE type) {
        this.type = type;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public ActionSequence getSequence() {
        return sequence;
    }

    public void setSequence(ActionSequence sequence) {
        this.sequence = sequence;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Map<GOAL_TYPE, Integer> getPriorityModsMap() {
        return priorityModsMap;
    }

    public void setPriorityModsMap(Map<GOAL_TYPE, Integer> priorityModsMap) {
        this.priorityModsMap = priorityModsMap;
    }

    public ORDER_PRIORITY_MODS getStrictPriority() {
        return strictPriority;
    }

    public void setStrictPriority(ORDER_PRIORITY_MODS strictPriority) {
        this.strictPriority = strictPriority;
    }
}