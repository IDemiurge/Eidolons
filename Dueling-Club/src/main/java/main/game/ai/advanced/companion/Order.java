package main.game.ai.advanced.companion;

import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.game.ai.GroupAI;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.ActionSequence;

public class Order {
    ORDER_TYPE type;
    String arg;
    ActionSequence sequence;
    String actionType;
    UnitAI ai;
    GroupAI group;

    public Order(UnitAI ai, ORDER_TYPE type, String arg) {
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

    public UnitAI getAi() {
        return ai;
    }

    public void setAi(UnitAI ai) {
        this.ai = ai;
    }
}